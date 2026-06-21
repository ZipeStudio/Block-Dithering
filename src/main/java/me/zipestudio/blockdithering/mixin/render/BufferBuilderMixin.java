package me.zipestudio.blockdithering.mixin.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {

	@ModifyVariable(method = "addVertex(FFFIFFIIFFF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int blockdithering$markColorAlpha(int color) {
		if (!DitherMarker.ACTIVE.get()) {
			return color;
		}
		int alpha = (color >>> 24) & 0xFF;
		int marked = (int) (alpha * DitherVanillaPatcher.MARKER_SCALE);
		return (color & 0x00FFFFFF) | (marked << 24);
	}
}
