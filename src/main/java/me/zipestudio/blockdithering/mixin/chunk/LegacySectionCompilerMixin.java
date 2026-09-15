package me.zipestudio.blockdithering.mixin.chunk;

//? if fabric && <1.21.11 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public class LegacySectionCompilerMixin {

	@WrapOperation(
			method = "compile",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V"
			)
	)
	private void blockdithering$markTargetBlock(BlockRenderDispatcher dispatcher, BlockState state, BlockPos pos, BlockAndTintGetter level,
			PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random, Operation<Void> original) {
		DitherMarker.runMarked(state, () -> original.call(dispatcher, state, pos, level, poseStack, consumer, checkSides, random));
	}
}
*///?}
