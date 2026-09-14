package me.zipestudio.blockdithering.mixin.render;

//? if >=1.21.11 {
/*import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	//? if >=26.2 {
	/^@Inject(at = @At("HEAD"), method = "render")
	private void blockdithering$updateDitheringBuffer(CallbackInfo ci) {
		DitheringDataBuffer.update();
	}
	^///?} else {
	@Inject(at = @At("HEAD"), method = "renderLevel")
	private void blockdithering$updateDitheringBuffer(CallbackInfo ci) {
		DitheringDataBuffer.update();
	}
	//?}

}
*///?}
