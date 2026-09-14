package me.zipestudio.blockdithering.mixin.iris;

//? if <1.21.11 {
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.iris.IrisDitherShaderPatcher;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractBlockRenderContext.class, priority = 1500)
public class SodiumBlockRenderContextMixin {

	@Inject(
			method = "bufferDefaultModel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/RenderType;Lnet/caffeinemc/mods/sodium/client/services/SodiumModelData;)Ljava/util/List;",
					shift = At.Shift.AFTER
			),
			remap = false
	)
	private void blockdithering$restoreMarkerBlockId(BakedModel model, BlockState state, CallbackInfo ci) {
		if ((Object) this instanceof VertexEncoderInterface encoder && DitherBlocks.isTarget(state)) {
			encoder.overrideBlock(IrisDitherShaderPatcher.BLOCK_ID_SENTINEL);
		}
	}
}
//?}
