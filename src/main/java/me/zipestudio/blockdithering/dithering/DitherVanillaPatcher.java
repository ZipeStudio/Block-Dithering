package me.zipestudio.blockdithering.dithering;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DitherVanillaPatcher {

	private static final String MARKER = "// blockdithering:dithering";

	public static final float MARKER_SCALE = 0.9F;
	public static final float MARKER_THRESHOLD = 0.95F;

	private static final Pattern MAIN_PATTERN = Pattern.compile("void\\s+main\\s*\\(\\s*(?:void)?\\s*\\)\\s*\\{");

	//? if >=1.21.11 {
	/*private static final String PARAMETERS = """
		layout(std140) uniform DitheringData {
		    float DitherMinValue;
		    float DitherPixelSize;
		    float DitherNearDistance;
		    float DitherFarDistance;
		};
		""";
	private static final String DISTANCE = "sphericalVertexDistance";
	*///?} else {
	private static final String DISTANCE = "de_sphericalDistance";
	private static final String PARAMETERS = """
		uniform float DitherMinValue;
		uniform float DitherPixelSize;
		uniform float DitherNearDistance;
		uniform float DitherFarDistance;
		in float de_sphericalDistance;
		""";

	private static final String OUTLINE_FRAGMENT_CALL =
		"\n\tif (abs(vertexColor.a * 255.0 - " + DitherMarker.OUTLINE_ALPHA + ".0) < 0.5) {\n"
			+ "\t\tcolor.a = DitherOutlineAlpha * ColorModulator.a;\n"
			+ "\t\tde_applyDistanceDither(DitherOutlineDistance, gl_FragCoord.xy);\n"
			+ "\t}\n";

	public static String patchLegacy(String programName, boolean vertex, String source) {
		if (DitherTargets.isLegacyTarget(programName)) {
			return vertex ? patchVertex(source, "Position + ChunkOffset") : patchFragment(source);
		}
		if (DitherTargets.isLegacyOutline(programName)) {
			return vertex ? patchVertex(source, "Position") : patchFragmentWith(source, "uniform float DitherOutlineDistance;\nuniform float DitherOutlineAlpha;\n", OUTLINE_FRAGMENT_CALL);
		}
		return source;
	}

	private static String patchVertex(String source, String position) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}
		Matcher main = MAIN_PATTERN.matcher(source);
		if (!main.find()) {
			return source;
		}
		return source.substring(0, main.start())
			+ MARKER + "\nout float " + DISTANCE + ";\n\n"
			+ source.substring(main.start(), main.end())
			+ "\n\t" + DISTANCE + " = length((ModelViewMat * vec4(" + position + ", 1.0)).xyz);\n"
			+ source.substring(main.end());
	}
	//?}

	private static final String FRAGMENT_BODY = PARAMETERS + """

		const mat4 DE_DITHER_MAT = mat4(
		    1.0 / 17.0,  9.0 / 17.0,  3.0 / 17.0,  11.0 / 17.0,
		    13.0 / 17.0, 5.0 / 17.0,  15.0 / 17.0, 7.0 / 17.0,
		    4.0 / 17.0,  12.0 / 17.0, 2.0 / 17.0,  10.0 / 17.0,
		    16.0 / 17.0, 8.0 / 17.0,  14.0 / 17.0, 6.0 / 17.0
		);

		float de_easeInOutCubic(float x) {
		    return x < 0.5 ? 4.0 * x * x * x : 1.0 - pow(-2.0 * x + 2.0, 3.0) / 2.0;
		}

		void de_applyDistanceDither(float cameraDistance, vec2 fragCoord) {
		    float v = clamp(smoothstep(DitherNearDistance, DitherFarDistance, cameraDistance), DitherMinValue, 1.0);
		    v = de_easeInOutCubic(v);
		    vec2 cell = fragCoord / DitherPixelSize;
		    int x = int(cell.x);
		    int y = int(cell.y);
		    if (v < DE_DITHER_MAT[x % 4][y % 4]) {
		        discard;
		    }
		}
		""";

	private static final String FRAGMENT_CALL =
		"\n\tif (vertexColor.a < " + MARKER_THRESHOLD + ") {\n"
			+ "\t\tcolor.a = min(color.a / " + MARKER_SCALE + ", 1.0);\n"
			+ "\t\tde_applyDistanceDither(" + DISTANCE + ", gl_FragCoord.xy);\n"
			+ "\t}\n";

	public static String patchFragment(String source) {
		return patchFragmentWith(source, "", FRAGMENT_CALL);
	}

	private static String patchFragmentWith(String source, String extraHeader, String call) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}
		Matcher main = MAIN_PATTERN.matcher(source);
		if (!main.find()) {
			return source;
		}
		int fragIdx = source.indexOf("fragColor", main.end());
		if (fragIdx < 0) {
			return source;
		}
		String header = MARKER + " begin\n" + extraHeader + FRAGMENT_BODY + MARKER + " end\n\n";
		return source.substring(0, main.start())
			+ header
			+ source.substring(main.start(), fragIdx)
			+ call
			+ source.substring(fragIdx);
	}
}
