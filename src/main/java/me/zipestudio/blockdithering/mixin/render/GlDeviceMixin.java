package me.zipestudio.blockdithering.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.shaders.ShaderType;
import me.zipestudio.blockdithering.dithering.DitherTargets;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice")
public class GlDeviceMixin {

	@WrapOperation(
			method = "compileShader",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/shaders/ShaderSource;get(Lnet/minecraft/resources/Identifier;Lcom/mojang/blaze3d/shaders/ShaderType;)Ljava/lang/String;"
			)
	)
	private String blockdithering$injectDitheringSource(ShaderSource instance, Identifier id, ShaderType type, Operation<String> original) {
		String source = instance.get(id, type);
		if (source == null || type != ShaderType.FRAGMENT || !DitherTargets.isTarget(id)) {
			return source;
		}
		return DitherVanillaPatcher.patchFragment(source);
	}
}
