package me.zipestudio.blockdithering.dithering;

//? if <1.21.11 {
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.client.renderer.RenderType;

public final class DitherOutline {

	private static final List<AtomicReference<Runnable>> SLOTS = new CopyOnWriteArrayList<>();

	private static volatile boolean active;

	public static final RenderType RENDER_TYPE = new RenderType(
			"blockdithering_outline",
			RenderType.lines().format(),
			RenderType.lines().mode(),
			RenderType.lines().bufferSize(),
			false,
			false,
			() -> {
				RenderType.lines().setupRenderState();
				setActive(true);
			},
			() -> {
				setActive(false);
				RenderType.lines().clearRenderState();
			}
	) { };

	public static boolean isActive() {
		return active;
	}

	public static Consumer<Runnable> newListenerSlot() {
		AtomicReference<Runnable> slot = new AtomicReference<>();
		SLOTS.add(slot);
		return slot::set;
	}

	private static void setActive(boolean value) {
		active = value;
		for (AtomicReference<Runnable> slot : SLOTS) {
			Runnable listener = slot.get();
			if (listener != null) {
				listener.run();
			}
		}
	}

	private DitherOutline() { }
}
//?}
