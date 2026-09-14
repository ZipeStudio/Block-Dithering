package me.zipestudio.blockdithering.mixin.iris;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.state.ShaderAttributeInputs;
import net.irisshaders.iris.pipeline.programs.ShaderCreator;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ShaderCreator.class, remap = false)
public class VanillaShaderCreatorMixin {

	@WrapOperation(
			method = "create",
			at = @At(
					value = "INVOKE",
					target = "Lnet/irisshaders/iris/pipeline/transform/TransformPatcher;patchVanilla(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/irisshaders/iris/gl/blending/AlphaTest;ZZLnet/irisshaders/iris/gl/state/ShaderAttributeInputs;Lit/unimi/dsi/fastutil/objects/Object2ObjectMap;)Ljava/util/Map;"
			)
	)
	private static Map<PatchShaderType, String> blockdithering$patchTerrain(String name, String vertex, String geometry, String tessControl,
			String tessEval, String fragment, AlphaTest alpha, boolean isLines, boolean hasChunkOffset, ShaderAttributeInputs inputs,
			Object2ObjectMap<?, ?> textureMap, Operation<Map<PatchShaderType, String>> original) {
		Map<PatchShaderType, String> sources = original.call(name, vertex, geometry, tessControl, tessEval, fragment, alpha, isLines, hasChunkOffset, inputs, textureMap);
		String program = name == null ? "" : name.toLowerCase(Locale.ROOT);
		if (sources == null || program.contains("shadow")) {
			return sources;
		}
		if (program.contains("line")) {
			String patchedFragment = IrisDitherShaderPatcher.patchOutlineFragmentShader(sources.get(PatchShaderType.FRAGMENT));
			BlockDithering.LOGGER.info("Iris line program '{}' outline-patched: {}", name, patchedFragment != null);
			if (patchedFragment == null) {
				return sources;
			}
			Map<PatchShaderType, String> patched = new EnumMap<>(PatchShaderType.class);
			patched.putAll(sources);
			patched.put(PatchShaderType.FRAGMENT, patchedFragment);
			return patched;
		}
		if (!program.contains("terrain")) {
			return sources;
		}
		String transformedGeometry = sources.get(PatchShaderType.GEOMETRY);
		if (transformedGeometry != null && !transformedGeometry.isBlank()) {
			BlockDithering.LOGGER.info("Iris vanilla terrain program '{}' has a geometry stage, skipping dither", name);
			return sources;
		}
		String patchedVertex = IrisDitherShaderPatcher.patchVertexShader(sources.get(PatchShaderType.VERTEX));
		String patchedFragment = IrisDitherShaderPatcher.patchFragmentShader(sources.get(PatchShaderType.FRAGMENT));
		if (patchedVertex == null || patchedFragment == null) {
			BlockDithering.LOGGER.info("Iris vanilla terrain program '{}' NOT patched (vertex ok={}, fragment ok={})",
					name, patchedVertex != null, patchedFragment != null);
			return sources;
		}
		Map<PatchShaderType, String> patched = new EnumMap<>(PatchShaderType.class);
		patched.putAll(sources);
		patched.put(PatchShaderType.VERTEX, patchedVertex);
		patched.put(PatchShaderType.FRAGMENT, patchedFragment);
		BlockDithering.LOGGER.info("Iris vanilla terrain program '{}' dither-patched", name);
		return patched;
	}
}
//?}
