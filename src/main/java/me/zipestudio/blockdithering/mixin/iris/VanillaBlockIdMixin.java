package me.zipestudio.blockdithering.mixin.iris;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import me.zipestudio.blockdithering.dithering.iris.IrisBlockIdAccess;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
//? if neoforge {
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.model.data.ModelData;
//?}

@Mixin(value = ModelBlockRenderer.class, priority = 1500)
public class VanillaBlockIdMixin {

	@WrapMethod(method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V")
	private void blockdithering$markBlockId(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack,
			VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay, Operation<Void> original) {
		blockdithering$runWithMarkerId(state, consumer, () -> original.call(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay));
	}

	//? if neoforge {
	@WrapMethod(method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V")
	private void blockdithering$markBlockIdNeo(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack,
			VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay, ModelData modelData, RenderType renderType,
			Operation<Void> original) {
		blockdithering$runWithMarkerId(state, consumer, () -> original.call(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData, renderType));
	}
	//?}

	@Unique
	private static void blockdithering$runWithMarkerId(BlockState state, VertexConsumer consumer, Runnable render) {
		if (!(consumer instanceof IrisBlockIdAccess buffer) || !DitherBlocks.isTarget(state)) {
			render.run();
			return;
		}
		int previous = buffer.blockdithering$getBlockId();
		buffer.blockdithering$setBlockId(IrisDitherShaderPatcher.BLOCK_ID_SENTINEL);
		try {
			render.run();
		} finally {
			buffer.blockdithering$setBlockId(previous);
		}
	}
}
//?}
