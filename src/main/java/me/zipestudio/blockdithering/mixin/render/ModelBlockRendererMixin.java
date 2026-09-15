package me.zipestudio.blockdithering.mixin.render;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
//? if neoforge {
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.model.data.ModelData;
//?}

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {

	@WrapMethod(method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V")
	private void blockdithering$markTargetBlock(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack,
			VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay, Operation<Void> original) {
		DitherMarker.runMarked(state, () -> original.call(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay));
	}

	//? if neoforge {
	@WrapMethod(method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V")
	private void blockdithering$markTargetBlockNeo(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack,
			VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay, ModelData modelData, RenderType renderType,
			Operation<Void> original) {
		DitherMarker.runMarked(state, () -> original.call(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, modelData, renderType));
	}
	//?}
}
//?}
