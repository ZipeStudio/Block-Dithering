package me.zipestudio.blockdithering.mixin.render;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import me.zipestudio.blockdithering.dithering.DitherOutline;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import me.zipestudio.blockdithering.dithering.iris.IrisState;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class HitOutlineMixin {

	@Shadow @Final private RenderBuffers renderBuffers;

	@WrapOperation(
			method = "renderLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/LevelRenderer;renderHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
			)
	)
	private void blockdithering$ditherTargetOutline(LevelRenderer renderer, PoseStack poseStack, VertexConsumer consumer, Entity entity,
			double camX, double camY, double camZ, BlockPos pos, BlockState state, Operation<Void> original) {
		if (!DitherBlocks.isTarget(state)) {
			original.call(renderer, poseStack, consumer, entity, camX, camY, camZ, pos, state);
			return;
		}
		VoxelShape shape = state.getShape(entity.level(), pos, CollisionContext.of(entity));
		AABB box = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
		double dx = Math.max(Math.max(box.minX - camX, 0.0D), camX - box.maxX);
		double dy = Math.max(Math.max(box.minY - camY, 0.0D), camY - box.maxY);
		double dz = Math.max(Math.max(box.minZ - camZ, 0.0D), camZ - box.maxZ);
		DitheringDataBuffer.outlineDistance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (IrisState.isShaderPackInUse()) {
			VertexConsumer outlineConsumer = this.renderBuffers.bufferSource().getBuffer(DitherOutline.RENDER_TYPE);
			original.call(renderer, poseStack, outlineConsumer, entity, camX, camY, camZ, pos, state);
			return;
		}
		DitherMarker.OUTLINE.set(Boolean.TRUE);
		try {
			original.call(renderer, poseStack, consumer, entity, camX, camY, camZ, pos, state);
		} finally {
			DitherMarker.OUTLINE.set(Boolean.FALSE);
		}
	}
}
//?}
