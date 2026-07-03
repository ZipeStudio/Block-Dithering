package me.zipestudio.blockdithering.mixin.chunk;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

//? if >=26.1 {
/*import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
*///?} else {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.BlockAndTintGetter;
//? if fabric {
import com.mojang.blaze3d.vertex.VertexConsumer;
//?} elif neoforge {
/*import java.util.function.Function;
*///?}
//?}

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {

	//? if >=26.1 {
	/*@WrapOperation(
			//? if fabric {
			method = "compile",
			//?} elif neoforge {
			method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
			//?}
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"
			)
	)
	private void blockdithering$markTargetBlock(ModelBlockRenderer renderer, BlockQuadOutput output, float x, float y, float z,
			BlockAndTintGetter level, BlockPos pos, BlockState blockState, BlockStateModel model, long seed,
			Operation<Void> original) {
		boolean target = DitherBlocks.isTarget(blockState);
		if (target) {
			DitherMarker.ACTIVE.set(Boolean.TRUE);
		}
		try {
			original.call(renderer, output, x, y, z, level, pos, blockState, model, seed);
		} finally {
			if (target) {
				DitherMarker.ACTIVE.set(Boolean.FALSE);
			}
		}
	}
	*///?} else {
	@WrapOperation(
			//? if fabric {
			method = "compile",
			//?} elif neoforge {
			/*method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
			*///?}
			at = @At(
					value = "INVOKE",
					//? if fabric {
					target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLjava/util/List;)V"
					//?} elif neoforge {
					/*target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/function/Function;ZLjava/util/List;)V"
					*///?}
			)
	)
	private void blockdithering$markTargetBlock(BlockRenderDispatcher dispatcher, BlockState blockState, BlockPos pos,
			BlockAndTintGetter level, PoseStack poseStack,
			//? if fabric {
			VertexConsumer bufferLookup,
			//?} elif neoforge {
			/*Function bufferLookup,
			*///?}
			boolean checkSides, List parts,
			Operation<Void> original) {
		boolean target = DitherBlocks.isTarget(blockState);
		if (target) {
			DitherMarker.ACTIVE.set(Boolean.TRUE);
		}
		try {
			original.call(dispatcher, blockState, pos, level, poseStack, bufferLookup, checkSides, parts);
		} finally {
			if (target) {
				DitherMarker.ACTIVE.set(Boolean.FALSE);
			}
		}
	}
	//?}

}
