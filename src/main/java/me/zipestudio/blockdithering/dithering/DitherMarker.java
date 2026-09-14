package me.zipestudio.blockdithering.dithering;

public class DitherMarker {

	public static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

	public static final int OUTLINE_ALPHA = 1;
	public static final ThreadLocal<Boolean> OUTLINE = ThreadLocal.withInitial(() -> Boolean.FALSE);

	private DitherMarker() { }
}
