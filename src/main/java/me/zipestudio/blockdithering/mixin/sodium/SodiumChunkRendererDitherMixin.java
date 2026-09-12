package me.zipestudio.blockdithering.mixin.sodium;

//? if >=26.2 {
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import me.zipestudio.blockdithering.dithering.sodium.SodiumDitherShaderPatcher;
import net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultChunkRenderer.class)
public class SodiumChunkRendererDitherMixin {

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderPass;setUniform(Ljava/lang/String;Lcom/mojang/blaze3d/buffers/GpuBuffer;)V",
					shift = At.Shift.AFTER
			)
	)
	private void blockdithering$bindDitheringData(CallbackInfo ci, @Local RenderPass pass) {
		pass.setUniform(SodiumDitherShaderPatcher.UNIFORM_BLOCK_NAME, DitheringDataBuffer.BUFFER);
	}
}
//?}
