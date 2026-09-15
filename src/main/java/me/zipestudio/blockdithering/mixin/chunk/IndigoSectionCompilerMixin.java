package me.zipestudio.blockdithering.mixin.chunk;

//? if fabric && >=1.21.11 && <26.1 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.zipestudio.blockdithering.dithering.DitherMarker;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
public class IndigoSectionCompilerMixin {

	@WrapOperation(
			method = "compile",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"
			)
	)
	private RenderShape blockdithering$markTargetBlock(BlockState state, Operation<RenderShape> original) {
		RenderShape[] shape = new RenderShape[1];
		DitherMarker.runMarked(state, () -> shape[0] = original.call(state));
		return shape[0];
	}
}
*///?}
