package me.zipestudio.blockdithering.mixin.render;

//? if >=26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import me.zipestudio.blockdithering.dithering.iris.IrisState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class BlockOutlineMixin {

	@WrapOperation(
			method = "submitHitOutline",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShapeOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/client/renderer/rendertype/RenderType;IFZ)V"
			)
	)
	private void blockdithering$ditherTargetOutline(SubmitNodeCollector collector, PoseStack poseStack, VoxelShape shape, RenderType renderType,
			int color, float width, boolean translucent, Operation<Void> original, @Local(argsOnly = true) BlockOutlineRenderState state) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null || shape.isEmpty() || (IrisState.isShaderPackInUse() && !IrisState.isOutlineProgramPatched())
				|| !DitherBlocks.isTarget(minecraft.level.getBlockState(state.pos()))) {
			original.call(collector, poseStack, shape, renderType, color, width, translucent);
			return;
		}
		Vector3f origin = poseStack.last().pose().getTranslation(new Vector3f());
		AABB box = shape.bounds().move(origin.x, origin.y, origin.z);
		double dx = Math.max(Math.max(box.minX, 0.0D), -box.maxX);
		double dy = Math.max(Math.max(box.minY, 0.0D), -box.maxY);
		double dz = Math.max(Math.max(box.minZ, 0.0D), -box.maxZ);
		DitheringDataBuffer.outlineDistance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		DitheringDataBuffer.outlineAlpha = ((color >>> 24) & 0xFF) / 255.0F;
		int marked = (color & 0x00FFFFFF) | (DitherMarker.OUTLINE_ALPHA << 24);
		original.call(collector, poseStack, shape, renderType, marked, width, translucent);
	}
}
*///?}
