package me.zipestudio.blockdithering.mixin.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//? if <1.21.11 {
import me.zipestudio.blockdithering.dithering.DitheringDataBuffer;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {

	//? if <1.21.11 {
	@Unique
	private static final ThreadLocal<Boolean> blockdithering$inAddVertex = ThreadLocal.withInitial(() -> Boolean.FALSE);

	@Inject(method = "addVertex(FFFIFFIIFFF)V", at = @At("RETURN"))
	private void blockdithering$leaveAddVertex(CallbackInfo ci) {
		blockdithering$inAddVertex.set(Boolean.FALSE);
	}

	@ModifyVariable(method = "setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int blockdithering$markSetColor(int color) {
		if (DitherMarker.OUTLINE.get()) {
			DitheringDataBuffer.outlineAlpha = ((color >>> 24) & 0xFF) / 255.0F;
			return (color & 0x00FFFFFF) | (DitherMarker.OUTLINE_ALPHA << 24);
		}
		if (!DitherMarker.ACTIVE.get() || blockdithering$inAddVertex.get()) {
			return color;
		}
		return blockdithering$mark(color);
	}

	@ModifyVariable(method = "setColor(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;", at = @At("HEAD"), argsOnly = true, ordinal = 3)
	private int blockdithering$markSetColorAlpha(int alpha) {
		if (DitherMarker.OUTLINE.get()) {
			DitheringDataBuffer.outlineAlpha = alpha / 255.0F;
			return DitherMarker.OUTLINE_ALPHA;
		}
		if (!DitherMarker.ACTIVE.get() || blockdithering$inAddVertex.get()) {
			return alpha;
		}
		return (int) (alpha * DitherVanillaPatcher.MARKER_SCALE);
	}
	//?}

	@ModifyVariable(method = "addVertex(FFFIFFIIFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int blockdithering$markColorAlpha(int color) {
		if (!DitherMarker.ACTIVE.get()) {
			return color;
		}
		//? if <1.21.11
		blockdithering$inAddVertex.set(Boolean.TRUE);
		return blockdithering$mark(color);
	}

	@org.spongepowered.asm.mixin.Unique
	private static int blockdithering$mark(int color) {
		int alpha = (color >>> 24) & 0xFF;
		int marked = (int) (alpha * DitherVanillaPatcher.MARKER_SCALE);
		return (color & 0x00FFFFFF) | (marked << 24);
	}
}
