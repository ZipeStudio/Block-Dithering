package me.zipestudio.blockdithering.mixin.iris;

//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
 *///?} elif >=1.21.11 {
/*import net.minecraft.client.renderer.block.model.BlockStateModel;
*///?} else {
import net.minecraft.client.resources.model.BakedModel;
//?}

import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderer.class, priority = 1500)
public class SodiumTerrainBlockIdMixin {

	@Inject(method = "renderModel", at = @At("HEAD"))
	//? if >=1.21.11 {
	/*private void blockdithering$forceMarkerBlockId(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
	*///?} else {
	private void blockdithering$forceMarkerBlockId(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
	//?}
		if (DitherBlocks.isTarget(state)) {
			((VertexEncoderInterface) this).overrideBlock(IrisDitherShaderPatcher.BLOCK_ID_SENTINEL);
		}
	}

	@Inject(method = "renderModel", at = @At("RETURN"))
	//? if >=1.21.11 {
	/*private void blockdithering$restoreBlockId(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
	*///?} else {
	private void blockdithering$restoreBlockId(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
	//?}
		if (DitherBlocks.isTarget(state)) {
			((VertexEncoderInterface) this).restoreBlock();
		}
	}

}