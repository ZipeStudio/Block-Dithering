package me.zipestudio.blockdithering.mixin.render;

//? if <1.21.11 {
import me.zipestudio.blockdithering.dithering.DitherTargets;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin {

	@Shadow public abstract String getName();

	@Shadow public abstract int getId();

	@Inject(method = "apply", at = @At("TAIL"))
	private void blockdithering$uploadDitheringUniforms(CallbackInfo ci) {
		if (DitherTargets.isLegacyTarget(this.getName())) {
			DitheringDataBuffer.upload(this.getId());
		} else if (DitherTargets.isLegacyOutline(this.getName())) {
			DitheringDataBuffer.uploadOutline(this.getId());
		}
	}
}
//?}
