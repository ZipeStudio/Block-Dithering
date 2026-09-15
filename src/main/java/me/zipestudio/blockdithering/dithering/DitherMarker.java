package me.zipestudio.blockdithering.dithering;

import net.minecraft.world.level.block.state.BlockState;

public class DitherMarker {

	public static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

	public static final int OUTLINE_ALPHA = 1;
	public static final ThreadLocal<Boolean> OUTLINE = ThreadLocal.withInitial(() -> Boolean.FALSE);

	public static void runMarked(BlockState state, Runnable render) {
		Boolean previous = ACTIVE.get();
		ACTIVE.set(DitherBlocks.isTarget(state));
		try {
			render.run();
		} finally {
			ACTIVE.set(previous);
		}
	}

	private DitherMarker() { }
}
