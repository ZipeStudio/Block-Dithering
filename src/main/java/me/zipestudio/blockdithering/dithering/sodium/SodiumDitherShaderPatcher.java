package me.zipestudio.blockdithering.dithering.sodium;

import java.util.Locale;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;

public class SodiumDitherShaderPatcher {

	//? if >=26.2 {
	/*public static final String UNIFORM_BLOCK_NAME = "u_DitheringData";
	*///?} else {
	public static final String MIN_VALUE_UNIFORM = "BlockDitheringMinValue";
	public static final String PIXEL_SIZE_UNIFORM = "BlockDitheringPixelSize";
	public static final String NEAR_DISTANCE_UNIFORM = "BlockDitheringNearDistance";
	public static final String FAR_DISTANCE_UNIFORM = "BlockDitheringFarDistance";
	//?}

	private static final String MARKER = "// blockdithering:dithering";
	private static final String COLOR_APPLY = "color *= v_Color;";

	//? if >=26.2 {
	/*private static final String PARAMETERS = """
		layout(std140) uniform u_DitheringData {
		    float BlockDitheringMinValue;
		    float BlockDitheringPixelSize;
		    float BlockDitheringNearDistance;
		    float BlockDitheringFarDistance;
		};
		""";
	*///?} else {
	private static final String PARAMETERS = """
		uniform float BlockDitheringMinValue;
		uniform float BlockDitheringPixelSize;
		uniform float BlockDitheringNearDistance;
		uniform float BlockDitheringFarDistance;
		""";
	//?}

	//? if <1.21.11 {
	private static final String DISTANCE_INPUT = "in float blockdithering_sphericalDistance;\n";

	public static String patchVertex(String source) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}
		int mainIdx = source.indexOf("void main");
		int positionIdx = mainIdx < 0 ? -1 : source.indexOf("gl_Position", mainIdx);
		if (positionIdx < 0) {
			return source;
		}
		return source.substring(0, mainIdx)
			+ MARKER + "\nout float blockdithering_sphericalDistance;\n\n"
			+ source.substring(mainIdx, positionIdx)
			+ "blockdithering_sphericalDistance = length(position);\n\t"
			+ source.substring(positionIdx);
	}
	//?} else {
	/*private static final String DISTANCE_INPUT = "";
	*///?}

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

		void blockdithering_applyDistanceDither(float cameraDistance, vec2 fragCoord) {
		    float v = clamp(smoothstep(BlockDitheringNearDistance, BlockDitheringFarDistance, cameraDistance), BlockDitheringMinValue, 1.0);
		    v = blockdithering_easeInOutCubic(v);
		    vec2 cell = fragCoord / max(BlockDitheringPixelSize, 1.0);
		    int x = int(cell.x);
		    int y = int(cell.y);
		    if (v < BLOCKDITHERING_DITHER_MAT[x % 4][y % 4]) {
		        discard;
		    }
		}
		""";

	public static String patchFragment(String source) {
		if (source == null || source.contains(MARKER)) {
			return source;
		}
		int applyIdx = source.indexOf(COLOR_APPLY);
		if (applyIdx < 0) {
			return source;
		}
		int mainIdx = source.indexOf("void main");
		if (mainIdx < 0) {
			return source;
		}

		String header = MARKER + " begin\n" + PARAMETERS + DISTANCE_INPUT + "\n" + DITHER_BODY + MARKER + " end\n\n";

		int insertAt = applyIdx + COLOR_APPLY.length();
		String call = "\n\tif (v_Color.a < " + glsl(DitherVanillaPatcher.MARKER_THRESHOLD) + ") {\n"
			+ "\t\tcolor.a = min(color.a / " + glsl(DitherVanillaPatcher.MARKER_SCALE) + ", 1.0);\n"
			//? if >=1.21.11 {
			/*+ "\t\tblockdithering_applyDistanceDither(v_FragDistance.y, gl_FragCoord.xy);\n"
			*///?} else {
			+ "\t\tblockdithering_applyDistanceDither(blockdithering_sphericalDistance, gl_FragCoord.xy);\n"
			//?}
			+ "\t}\n";

		return source.substring(0, mainIdx)
			+ header
			+ source.substring(mainIdx, insertAt)
			+ call
			+ source.substring(insertAt);
	}

	private static String glsl(double value) {
		return String.format(Locale.ROOT, "%.6f", value);
	}

	private SodiumDitherShaderPatcher() { }
}
