package me.zipestudio.blockdithering.mixin.render;

import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Inject(at = @At("HEAD"), method = "renderLevel")
	private void blockdithering$updateDitheringBuffer(CallbackInfo ci) {
		DitheringDataBuffer.update();
	}
}
