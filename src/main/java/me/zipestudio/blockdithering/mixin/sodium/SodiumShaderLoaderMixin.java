package me.zipestudio.blockdithering.mixin.sodium;

//? if <26.2 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import me.zipestudio.blockdithering.dithering.sodium.SodiumDitherShaderPatcher;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShaderLoader.class)
public class SodiumShaderLoaderMixin {

	@WrapOperation(
			method = "loadShader",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/client/gl/shader/ShaderLoader;getShaderSource(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/String;"
			)
	)
	private static String blockdithering$patchSodiumTerrainShader(ResourceLocation id, Operation<String> original) {
		String source = original.call(id);
		if (source != null && id.getPath().endsWith("block_layer_opaque.fsh")) {
			return SodiumDitherShaderPatcher.patchFragment(source);
		}
		//? if <1.21.11 {
		if (source != null && id.getPath().endsWith("block_layer_opaque.vsh")) {
			return SodiumDitherShaderPatcher.patchVertex(source);
		}
		//?}
		return source;
	}
}
//?}
