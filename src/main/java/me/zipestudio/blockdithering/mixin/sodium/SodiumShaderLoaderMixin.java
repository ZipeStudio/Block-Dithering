package me.zipestudio.blockdithering.mixin.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import me.zipestudio.blockdithering.dithering.sodium.SodiumDitherShaderPatcher;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShaderLoader.class)
public class SodiumShaderLoaderMixin {

	@WrapOperation(
			method = "loadShader",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/client/gl/shader/ShaderLoader;getShaderSource(Lnet/minecraft/resources/Identifier;)Ljava/lang/String;"
			)
	)
	private static String blockdithering$patchSodiumTerrainShader(Identifier id, Operation<String> original) {
		String source = original.call(id);
		if (source != null && id.getPath().endsWith("block_layer_opaque.fsh")) {
			return SodiumDitherShaderPatcher.patchFragment(source);
		}
		return source;
	}
}
