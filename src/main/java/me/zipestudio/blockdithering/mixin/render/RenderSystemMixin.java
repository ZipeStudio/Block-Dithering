package me.zipestudio.blockdithering.mixin.render;

//? if >=1.21.11 {
/*import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {

	@Inject(method = "bindDefaultUniforms", at = @At("TAIL"))
	private static void blockdithering$bindDitheringData(RenderPass pass, CallbackInfo ci) {
		pass.setUniform("DitheringData", DitheringDataBuffer.BUFFER);
	}
}
*///?}
