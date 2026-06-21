package me.zipestudio.blockdithering.mixin.iris;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import net.irisshaders.iris.pipeline.programs.SodiumPrograms;
import net.irisshaders.iris.pipeline.transform.PatchShaderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SodiumPrograms.class)
public class SodiumProgramsMixin {

	@WrapOperation(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/irisshaders/iris/pipeline/programs/SodiumPrograms;createGlShaders(Ljava/lang/String;Ljava/util/Map;)Ljava/util/Map;"
			)
	)
	private Map<PatchShaderType, ?> blockdithering$patchTerrain(
			SodiumPrograms self, String name, Map<PatchShaderType, String> sources, Operation<Map<PatchShaderType, ?>> original
	) {
		String pass = name == null ? "" : name.toLowerCase(Locale.ROOT);
		boolean terrain = pass.startsWith("terrain") || pass.equals("translucent");
		if (!terrain) {
			return original.call(self, name, sources);
		}

		String geometry = sources.get(PatchShaderType.GEOMETRY);
		if (geometry != null && !geometry.isBlank()) {
			BlockDithering.LOGGER.info("Sodium+Iris pass '{}' has a geometry stage, skipping dither", pass);
			return original.call(self, name, sources);
		}

		String vertex = sources.get(PatchShaderType.VERTEX);
		String fragment = sources.get(PatchShaderType.FRAGMENT);
		String patchedVertex = IrisDitherShaderPatcher.patchVertexShader(vertex);
		String patchedFragment = IrisDitherShaderPatcher.patchFragmentShader(fragment);

		if (patchedVertex == null || patchedFragment == null) {
			BlockDithering.LOGGER.info("Sodium+Iris pass '{}' NOT patched (vertex ok={}, fragment ok={})",
					pass, patchedVertex != null, patchedFragment != null);
			return original.call(self, name, sources);
		}

		Map<PatchShaderType, String> copy = new EnumMap<>(PatchShaderType.class);
		copy.putAll(sources);
		copy.put(PatchShaderType.VERTEX, patchedVertex);
		copy.put(PatchShaderType.FRAGMENT, patchedFragment);
		try {
			Map<PatchShaderType, ?> result = original.call(self, name, copy);
			BlockDithering.LOGGER.info("Sodium+Iris terrain pass '{}' dither-patched", pass);
			return result;
		} catch (Throwable t) {
			BlockDithering.LOGGER.warn("Sodium+Iris pass '{}' dither patch failed to compile, using original: {}",
					pass, t.getMessage());
			return original.call(self, name, sources);
		}
	}
}
