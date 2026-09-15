package me.zipestudio.blockdithering.mixin.iris;

import me.zipestudio.blockdithering.config.LeafyConfig;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.FloatSupplier;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.CommonUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommonUniforms.class)
public class CommonUniformsDitherMixin {

	@Inject(method = "addDynamicUniforms", at = @At("TAIL"), remap = false)
	private static void blockdithering$addDitherUniforms(DynamicUniformHolder uniforms, FogMode fogMode, CallbackInfo ci) {
		uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "BlockDitheringFar",
				(FloatSupplier) () -> (float) blockdithering$options().getFarDistance());
		uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "BlockDitheringNear",
				(FloatSupplier) () -> (float) blockdithering$options().getNearDistance());
		uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "BlockDitheringMinValue",
				(FloatSupplier) () -> (float) Math.clamp(blockdithering$options().getMinVisibility(), 0.0D, 1.0D));
		uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "BlockDitheringPixelSize",
				(FloatSupplier) () -> (float) Math.max(blockdithering$options().getPixelSize(), 1.0D));
		uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "BlockDitheringOutlineDistance",
				(FloatSupplier) () -> DitheringDataBuffer.outlineDistance);
		uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, "BlockDitheringOutlineAlpha",
				(FloatSupplier) () -> DitheringDataBuffer.outlineAlpha);
		//? if <1.21.11 {
		uniforms.uniform1f("BlockDitheringOutline",
				(FloatSupplier) () -> me.zipestudio.blockdithering.dithering.DitherOutline.isActive() ? 1.0F : 0.0F,
				me.zipestudio.blockdithering.dithering.DitherOutline.newListenerSlot()::accept);
		//?}
	}

	@Unique
	private static DitheringDataConfig blockdithering$options() {
		return LeafyConfig.getInstance().getDitheringOptions();
	}
}
