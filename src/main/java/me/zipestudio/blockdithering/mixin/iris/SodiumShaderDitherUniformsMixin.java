package me.zipestudio.blockdithering.mixin.iris;

import me.zipestudio.blockdithering.config.LeafyConfig;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;
import net.irisshaders.iris.pipeline.programs.SodiumShader;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SodiumShader.class)
public class SodiumShaderDitherUniformsMixin {

	@Unique
	private boolean blockdithering$resolved;
	@Unique
	private int blockdithering$farLoc = -1;
	@Unique
	private int blockdithering$nearLoc = -1;
	@Unique
	private int blockdithering$minLoc = -1;
	@Unique
	private int blockdithering$pixelLoc = -1;

	@Inject(method = "updateUniforms", at = @At("TAIL"))
	private void blockdithering$uploadDitherUniforms(CallbackInfo ci) {
		int program = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
		if (program == 0) {
			return;
		}
		if (!blockdithering$resolved) {
			blockdithering$farLoc = GL20.glGetUniformLocation(program, "BlockDitheringFar");
			blockdithering$nearLoc = GL20.glGetUniformLocation(program, "BlockDitheringNear");
			blockdithering$minLoc = GL20.glGetUniformLocation(program, "BlockDitheringMinValue");
			blockdithering$pixelLoc = GL20.glGetUniformLocation(program, "BlockDitheringPixelSize");
			blockdithering$resolved = true;
		}
		if (blockdithering$farLoc < 0 && blockdithering$nearLoc < 0
				&& blockdithering$minLoc < 0 && blockdithering$pixelLoc < 0) {
			return;
		}
		DitheringDataConfig d = LeafyConfig.getInstance().getDitheringOptions();
		if (blockdithering$farLoc >= 0) {
			GL20.glUniform1f(blockdithering$farLoc, (float) d.getFarDistance());
		}
		if (blockdithering$nearLoc >= 0) {
			GL20.glUniform1f(blockdithering$nearLoc, (float) d.getNearDistance());
		}
		if (blockdithering$minLoc >= 0) {
			GL20.glUniform1f(blockdithering$minLoc, (float) Math.clamp(d.getMinVisibility(), 0.0, 1.0));
		}
		if (blockdithering$pixelLoc >= 0) {
			GL20.glUniform1f(blockdithering$pixelLoc, (float) Math.max(d.getPixelSize(), 1.0));
		}
	}
}
