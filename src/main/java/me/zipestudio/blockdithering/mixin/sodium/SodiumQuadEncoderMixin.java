package me.zipestudio.blockdithering.mixin.sodium;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import net.caffeinemc.mods.sodium.client.render.frapi.render.QuadEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = QuadEncoder.class, remap = false)
public class SodiumQuadEncoderMixin {

	@WrapOperation(
			method = "writeQuadVertices(Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;Lnet/caffeinemc/mods/sodium/api/vertex/buffer/VertexBufferWriter;ILorg/joml/Matrix4f;ZLorg/joml/Matrix3f;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/api/util/ColorARGB;toABGR(I)I"
			)
	)
	private static int blockdithering$markVertexAlpha(int argb, Operation<Integer> original) {
		int abgr = original.call(argb);
		if (!DitherMarker.ACTIVE.get()) {
			return abgr;
		}
		int alpha = (abgr >>> 24) & 0xFF;
		int marked = (int) (alpha * DitherVanillaPatcher.MARKER_SCALE);
		return (abgr & 0x00FFFFFF) | (marked << 24);
	}
}
//?}
