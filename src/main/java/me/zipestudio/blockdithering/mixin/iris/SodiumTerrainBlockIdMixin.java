package me.zipestudio.blockdithering.mixin.iris;

import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class, priority = 1500)
public class SodiumTerrainBlockIdMixin {

	@Inject(method = "renderModel", at = @At("HEAD"))
	private void blockdithering$forceMarkerBlockId(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
		if (DitherBlocks.isTarget(state)) {
			((VertexEncoderInterface) this).overrideBlock(IrisDitherShaderPatcher.BLOCK_ID_SENTINEL);
		}
	}

	@Inject(method = "renderModel", at = @At("RETURN"))
	private void blockdithering$restoreBlockId(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
		if (DitherBlocks.isTarget(state)) {
			((VertexEncoderInterface) this).restoreBlock();
		}
	}

}
