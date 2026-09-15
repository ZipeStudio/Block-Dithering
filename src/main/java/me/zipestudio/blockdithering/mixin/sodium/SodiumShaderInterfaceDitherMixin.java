package me.zipestudio.blockdithering.mixin.sodium;

//? if <26.2 {
import me.zipestudio.blockdithering.config.LeafyConfig;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;
import me.zipestudio.blockdithering.dithering.sodium.SodiumDitherShaderPatcher;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultShaderInterface.class)
public class SodiumShaderInterfaceDitherMixin {

	@Unique
	private GlUniformFloat blockdithering$minValue;
	@Unique
	private GlUniformFloat blockdithering$pixelSize;
	@Unique
	private GlUniformFloat blockdithering$nearDistance;
	@Unique
	private GlUniformFloat blockdithering$farDistance;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void blockdithering$bindDitherUniforms(ShaderBindingContext context, ChunkShaderOptions options, CallbackInfo ci) {
		this.blockdithering$minValue = context.bindUniformOptional(SodiumDitherShaderPatcher.MIN_VALUE_UNIFORM, GlUniformFloat::new);
		this.blockdithering$pixelSize = context.bindUniformOptional(SodiumDitherShaderPatcher.PIXEL_SIZE_UNIFORM, GlUniformFloat::new);
		this.blockdithering$nearDistance = context.bindUniformOptional(SodiumDitherShaderPatcher.NEAR_DISTANCE_UNIFORM, GlUniformFloat::new);
		this.blockdithering$farDistance = context.bindUniformOptional(SodiumDitherShaderPatcher.FAR_DISTANCE_UNIFORM, GlUniformFloat::new);
	}

	@Inject(method = "setupState", at = @At("TAIL"))
	private void blockdithering$uploadDitherUniforms(CallbackInfo ci) {
		DitheringDataConfig d = LeafyConfig.getInstance().getDitheringOptions();
		if (this.blockdithering$minValue != null) {
			this.blockdithering$minValue.setFloat((float) Math.clamp(d.getMinVisibility(), 0.0D, 1.0D));
		}
		if (this.blockdithering$pixelSize != null) {
			this.blockdithering$pixelSize.setFloat((float) Math.max(d.getPixelSize(), 1.0D));
		}
		if (this.blockdithering$nearDistance != null) {
			this.blockdithering$nearDistance.setFloat((float) d.getNearDistance());
		}
		if (this.blockdithering$farDistance != null) {
			this.blockdithering$farDistance.setFloat((float) d.getFarDistance());
		}
	}
}
//?}
