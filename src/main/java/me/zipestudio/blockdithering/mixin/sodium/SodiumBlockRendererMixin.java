package me.zipestudio.blockdithering.mixin.sodium;


//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
*///?} else {
import net.minecraft.client.renderer.block.model.BlockStateModel;
//?}

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public class SodiumBlockRendererMixin {

	@Unique
	private boolean blockdithering$dither;

	@Inject(method = "renderModel", at = @At("HEAD"))
	private void blockdithering$flagTargetBlock(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
		this.blockdithering$dither = DitherBlocks.isTarget(state);
	}

	@WrapOperation(
			method = "bufferQuad",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/api/util/ColorARGB;toABGR(I)I"
			)
	)
	private int blockdithering$markVertexAlpha(int argb, Operation<Integer> original) {
		int abgr = original.call(argb);
		if (!this.blockdithering$dither) {
			return abgr;
		}
		int alpha = (abgr >>> 24) & 0xFF;
		int marked = (int) (alpha * DitherVanillaPatcher.MARKER_SCALE);
		return (abgr & 0x00FFFFFF) | (marked << 24);
	}
}
