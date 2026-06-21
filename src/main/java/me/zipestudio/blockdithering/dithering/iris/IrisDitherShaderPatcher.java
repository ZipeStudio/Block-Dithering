package me.zipestudio.blockdithering.dithering.iris;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;

public class IrisDitherShaderPatcher {

	private static final String MARKER = "// blockdithering:dithering";
	private static final String VARYING = "blockdithering_marker";

	public static final int BLOCK_ID_SENTINEL = 31000;

	private static final Pattern MAIN_PATTERN = Pattern.compile("void\\s+main\\s*\\(\\s*(?:void)?\\s*\\)\\s*\\{");

	private static final String DITHER_BODY = """
		const mat4 BLOCKDITHERING_DITHER_MAT = mat4(
		    1.0 / 17.0,  9.0 / 17.0,  3.0 / 17.0,  11.0 / 17.0,
		    13.0 / 17.0, 5.0 / 17.0,  15.0 / 17.0, 7.0 / 17.0,
		    4.0 / 17.0,  12.0 / 17.0, 2.0 / 17.0,  10.0 / 17.0,
		    16.0 / 17.0, 8.0 / 17.0,  14.0 / 17.0, 6.0 / 17.0
		);

		float blockdithering_easeInOutCubic(float x) {
		    return x < 0.5 ? 4.0 * x * x * x : 1.0 - pow(-2.0 * x + 2.0, 3.0) / 2.0;
		}

		float blockdithering_cameraDistance() {
		    vec3 ndc = vec3(gl_FragCoord.xy / vec2(viewWidth, viewHeight), gl_FragCoord.z) * 2.0 - 1.0;
		    vec4 viewPos = gbufferProjectionInverse * vec4(ndc, 1.0);
		    return length(viewPos.xyz / viewPos.w);
		}

		void blockdithering_applyDistanceDither() {
		    float v = clamp(smoothstep(BlockDitheringNear, BlockDitheringFar, blockdithering_cameraDistance()), BlockDitheringMinValue, 1.0);
		    v = blockdithering_easeInOutCubic(v);
		    vec2 cell = gl_FragCoord.xy / max(BlockDitheringPixelSize, 1.0);
		    int x = int(cell.x);
		    int y = int(cell.y);
		    if (v < BLOCKDITHERING_DITHER_MAT[x % 4][y % 4]) {
		        discard;
		    }
		}
		""";

	public static String patchVertexShader(String source) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}
		if (!source.contains("mc_Entity")) {
			return null;
		}
		Matcher main = MAIN_PATTERN.matcher(source);
		if (!main.find()) {
			return null;
		}
		String header = MARKER + " begin\nout float " + VARYING + ";\n" + MARKER + " end\n\n";
		int encoded = (BLOCK_ID_SENTINEL + 1) * 2;
		String assign = "\n\tint blockdithering_e = int(mc_Entity + 0.5);\n"
			+ "\t" + VARYING + " = (blockdithering_e == " + BLOCK_ID_SENTINEL + " || blockdithering_e == " + encoded + ") ? 0.0 : 1.0;\n";
		return source.substring(0, main.start())
			+ header
			+ source.substring(main.start(), main.end())
			+ assign
			+ source.substring(main.end());
	}

	public static String patchFragmentShader(String source) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}
		Matcher main = MAIN_PATTERN.matcher(source);
		if (!main.find()) {
			return null;
		}

		StringBuilder header = new StringBuilder();
		header.append(MARKER).append(" begin\n");
		header.append("in float ").append(VARYING).append(";\n");
		header.append("uniform float BlockDitheringFar;\n");
		header.append("uniform float BlockDitheringNear;\n");
		header.append("uniform float BlockDitheringMinValue;\n");
		header.append("uniform float BlockDitheringPixelSize;\n");
		if (!source.contains("viewWidth")) {
			header.append("uniform float viewWidth;\n");
		}
		if (!source.contains("viewHeight")) {
			header.append("uniform float viewHeight;\n");
		}
		if (!source.contains("gbufferProjectionInverse")) {
			header.append("uniform mat4 gbufferProjectionInverse;\n");
		}
		header.append(DITHER_BODY);
		header.append(MARKER).append(" end\n\n");

		String call = "\n\tif (" + VARYING + " < " + DitherVanillaPatcher.MARKER_THRESHOLD + ") {\n"
			+ "\t\tblockdithering_applyDistanceDither();\n"
			+ "\t}\n";

		return source.substring(0, main.start())
			+ header
			+ source.substring(main.start(), main.end())
			+ call
			+ source.substring(main.end());
	}

	private IrisDitherShaderPatcher() { }
}
