package me.zipestudio.blockdithering.mixin.render;

//? if >=1.21.11 && <26.2 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import me.zipestudio.blockdithering.dithering.iris.IrisState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
//? if >=26.1 {
/^import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
^///?} else {
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
//?}

@Mixin(LevelRenderer.class)
public class RenderHitOutlineMixin {

	@WrapMethod(method = "renderHitOutline")
	private void blockdithering$ditherTargetOutline(PoseStack poseStack, VertexConsumer consumer, double camX, double camY, double camZ,
			BlockOutlineRenderState state, int color, float width, Operation<Void> original) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null || state.shape().isEmpty() || (IrisState.isShaderPackInUse() && !IrisState.isOutlineProgramPatched())
				|| !DitherBlocks.isTarget(minecraft.level.getBlockState(state.pos()))) {
			original.call(poseStack, consumer, camX, camY, camZ, state, color, width);
			return;
		}
		AABB box = state.shape().bounds().move(state.pos());
		double dx = Math.max(Math.max(box.minX - camX, 0.0D), camX - box.maxX);
		double dy = Math.max(Math.max(box.minY - camY, 0.0D), camY - box.maxY);
		double dz = Math.max(Math.max(box.minZ - camZ, 0.0D), camZ - box.maxZ);
		DitheringDataBuffer.outlineDistance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		DitheringDataBuffer.outlineAlpha = ((color >>> 24) & 0xFF) / 255.0F;
		int marked = (color & 0x00FFFFFF) | (DitherMarker.OUTLINE_ALPHA << 24);
		original.call(poseStack, consumer, camX, camY, camZ, state, marked, width);
	}
}
*///?}
