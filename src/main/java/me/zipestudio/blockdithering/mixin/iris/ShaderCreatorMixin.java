package me.zipestudio.blockdithering.mixin.iris;

//? if >=26.2 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.Locale;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import net.irisshaders.iris.gl.shader.ShaderCompileException;
import net.irisshaders.iris.pipeline.programs.PartialShader;
import net.irisshaders.iris.pipeline.programs.ShaderCreator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShaderCreator.class)
public class ShaderCreatorMixin {

	@WrapMethod(method = "link")
	private static PartialShader blockdithering$patchTerrain(
			String name, String vertex, String geometry, String tessControl, String tessEval, String fragment,
			VertexFormat vertexFormat, boolean isFullbright, Operation<PartialShader> original
	) throws ShaderCompileException {
		String pass = name == null ? "" : name.toLowerCase(Locale.ROOT);
		boolean terrain = pass.contains("terrain") && !pass.contains("shadow");
		if (!terrain) {
			return original.call(name, vertex, geometry, tessControl, tessEval, fragment, vertexFormat, isFullbright);
		}

		if (geometry != null && !geometry.isBlank()) {
			BlockDithering.LOGGER.info("Iris terrain program '{}' has a geometry stage, skipping dither", name);
			return original.call(name, vertex, geometry, tessControl, tessEval, fragment, vertexFormat, isFullbright);
		}

		String patchedVertex = IrisDitherShaderPatcher.patchVertexShader(vertex);
		String patchedFragment = IrisDitherShaderPatcher.patchFragmentShader(fragment);
		if (patchedVertex == null || patchedFragment == null) {
			BlockDithering.LOGGER.info("Iris terrain program '{}' NOT patched (vertex ok={}, fragment ok={})",
					name, patchedVertex != null, patchedFragment != null);
			return original.call(name, vertex, geometry, tessControl, tessEval, fragment, vertexFormat, isFullbright);
		}

		try {
			PartialShader result = original.call(name, patchedVertex, geometry, tessControl, tessEval, patchedFragment, vertexFormat, isFullbright);
			BlockDithering.LOGGER.info("Iris terrain program '{}' dither-patched", name);
			return result;
		} catch (Throwable t) {
			BlockDithering.LOGGER.warn("Iris terrain program '{}' dither patch failed, using original: {}", name, t.getMessage());
			return original.call(name, vertex, geometry, tessControl, tessEval, fragment, vertexFormat, isFullbright);
		}
	}
}
*///?}
