package me.zipestudio.blockdithering.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSectionsToRender.class)
public class ChunkSectionsToRenderMixin {

	@Inject(
			method = "renderGroup",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V",
					shift = At.Shift.AFTER
			)
	)
	private void blockdithering$bindDitheringData(CallbackInfo ci, @Local RenderPass renderPass) {
		renderPass.setUniform("DitheringData", DitheringDataBuffer.BUFFER);
	}
}
