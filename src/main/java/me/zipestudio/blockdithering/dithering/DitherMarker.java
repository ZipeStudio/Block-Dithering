package me.zipestudio.blockdithering.dithering;

public class DitherMarker {

	public static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

	private DitherMarker() { }
}
