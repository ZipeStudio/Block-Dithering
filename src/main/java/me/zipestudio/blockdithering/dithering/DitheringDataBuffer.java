package me.zipestudio.blockdithering.dithering;

//? if >=1.21.11 {
/*import com.mojang.blaze3d.buffers.*;
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
*///?} else {
import me.zipestudio.blockdithering.config.LeafyConfig;
import org.lwjgl.opengl.GL20;

public class DitheringDataBuffer {

	public static volatile float outlineDistance;
	public static volatile float outlineAlpha = 0.4F;

	public static void uploadOutline(int program) {
		upload(program);
		setFloat(program, "DitherOutlineDistance", outlineDistance);
		setFloat(program, "DitherOutlineAlpha", outlineAlpha);
	}

	public static void upload(int program) {
		DitheringDataConfig data = LeafyConfig.getInstance().getDitheringOptions();
		setFloat(program, "DitherMinValue", (float) data.getMinVisibility());
		setFloat(program, "DitherPixelSize", (float) data.getPixelSize());
		setFloat(program, "DitherNearDistance", (float) data.getNearDistance());
		setFloat(program, "DitherFarDistance", (float) data.getFarDistance());
	}

	private static void setFloat(int program, String name, float value) {
		int location = GL20.glGetUniformLocation(program, name);
		if (location >= 0) {
			GL20.glUniform1f(location, value);
		}
	}
}
//?}
