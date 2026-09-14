package me.zipestudio.blockdithering.mixin.iris;

//? if <1.21.11 {
import com.mojang.blaze3d.vertex.BufferBuilder;
import me.zipestudio.blockdithering.dithering.iris.IrisBlockIdAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BufferBuilder.class, priority = 1500)
public class IrisBufferBlockIdMixin implements IrisBlockIdAccess {

	@Shadow(remap = false) private int currentBlock;

	@Override
	public int blockdithering$getBlockId() {
		return this.currentBlock;
	}

	@Override
	public void blockdithering$setBlockId(int blockId) {
		this.currentBlock = blockId;
	}
}
//?}
