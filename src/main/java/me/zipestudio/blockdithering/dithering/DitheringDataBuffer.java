package me.zipestudio.blockdithering.dithering;

import com.mojang.blaze3d.buffers.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import me.zipestudio.blockdithering.config.LeafyConfig;
import org.lwjgl.system.MemoryStack;

public class DitheringDataBuffer {

	public static final int SIZE = new Std140SizeCalculator()
			.putFloat()
			.putFloat()
			.putFloat()
			.putFloat()
			.get();

	public static final GpuBuffer BUFFER = RenderSystem.getDevice().createBuffer(() -> "BlockDithering DitheringData UBO", 136, SIZE);

	public static void update() {
		DitheringDataConfig data = LeafyConfig.getInstance().getDitheringOptions();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, SIZE)
					.putFloat((float) data.getMinVisibility())
					.putFloat((float) data.getPixelSize())
					.putFloat((float) data.getNearDistance())
					.putFloat((float) data.getFarDistance())
					.get();
			RenderSystem.getDevice().createCommandEncoder().writeToBuffer(BUFFER.slice(), byteBuffer);
		}
	}
}
