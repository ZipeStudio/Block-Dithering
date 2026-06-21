package me.zipestudio.blockdithering.dithering.sodium;

import java.util.Locale;
import me.zipestudio.blockdithering.config.LeafyConfig;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;

public class SodiumDitherShaderPatcher {

	private static final String MARKER = "// blockdithering:dithering";
	private static final String COLOR_APPLY = "color *= v_Color;";

	private static final String EASE_AND_MATRIX = """
		const mat4 BLOCKDITHERING_DITHER_MAT = mat4(
		    1.0 / 17.0,  9.0 / 17.0,  3.0 / 17.0,  11.0 / 17.0,
		    13.0 / 17.0, 5.0 / 17.0,  15.0 / 17.0, 7.0 / 17.0,
		    4.0 / 17.0,  12.0 / 17.0, 2.0 / 17.0,  10.0 / 17.0,
		    16.0 / 17.0, 8.0 / 17.0,  14.0 / 17.0, 6.0 / 17.0
		);

		float blockdithering_easeInOutCubic(float x) {
		    return x < 0.5 ? 4.0 * x * x * x : 1.0 - pow(-2.0 * x + 2.0, 3.0) / 2.0;
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

		DitheringDataConfig c = LeafyConfig.getInstance().getDitheringOptions();
		String header = MARKER + " begin\n" + EASE_AND_MATRIX + ditherFunction(c) + MARKER + " end\n\n";

		int insertAt = applyIdx + COLOR_APPLY.length();
		String call = "\n\tif (v_Color.a < " + glsl(DitherVanillaPatcher.MARKER_THRESHOLD) + ") {\n"
			+ "\t\tcolor.a = min(color.a / " + glsl(DitherVanillaPatcher.MARKER_SCALE) + ", 1.0);\n"
			+ "\t\tblockdithering_applyDistanceDither(v_FragDistance.y, gl_FragCoord.xy);\n"
			+ "\t}\n";

		return source.substring(0, mainIdx)
			+ header
			+ source.substring(mainIdx, insertAt)
			+ call
			+ source.substring(insertAt);
	}

	private static String ditherFunction(DitheringDataConfig c) {
		return "void blockdithering_applyDistanceDither(float cameraDistance, vec2 fragCoord) {\n"
			+ "    float v = clamp(smoothstep(" + glsl(c.getNearDistance()) + ", " + glsl(c.getFarDistance())
			+ ", cameraDistance), " + glsl(c.getMinVisibility()) + ", 1.0);\n"
			+ "    v = blockdithering_easeInOutCubic(v);\n"
			+ "    vec2 cell = fragCoord / " + glsl(c.getPixelSize()) + ";\n"
			+ "    int x = int(cell.x);\n"
			+ "    int y = int(cell.y);\n"
			+ "    if (v < BLOCKDITHERING_DITHER_MAT[x % 4][y % 4]) {\n"
			+ "        discard;\n"
			+ "    }\n"
			+ "}\n";
	}

	private static String glsl(double value) {
		return String.format(Locale.ROOT, "%.6f", value);
	}

	private SodiumDitherShaderPatcher() { }
}
