package me.zipestudio.blockdithering.dithering;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DitherVanillaPatcher {

	private static final String MARKER = "// blockdithering:dithering";

	public static final float MARKER_SCALE = 0.9F;
	public static final float MARKER_THRESHOLD = 0.95F;

	private static final Pattern MAIN_PATTERN = Pattern.compile("void\\s+main\\s*\\(\\s*(?:void)?\\s*\\)\\s*\\{");

	private static final String FRAGMENT_BODY = """
		layout(std140) uniform DitheringData {
		    float DitherMinValue;
		    float DitherPixelSize;
		    float DitherNearDistance;
		    float DitherFarDistance;
		};

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
			+ "\t\tde_applyDistanceDither(sphericalVertexDistance, gl_FragCoord.xy);\n"
			+ "\t}\n";

	public static String patchFragment(String source) {
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
		String header = MARKER + " begin\n" + FRAGMENT_BODY + MARKER + " end\n\n";
		return source.substring(0, main.start())
			+ header
			+ source.substring(main.start(), fragIdx)
			+ FRAGMENT_CALL
			+ source.substring(fragIdx);
	}
}
