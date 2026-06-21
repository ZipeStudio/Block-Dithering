package me.zipestudio.blockdithering.dithering;

import java.util.Set;
import net.minecraft.resources.Identifier;

public class DitherTargets {

	public static final Set<Identifier> FRAGMENT_SHADERS = Set.of(
			Identifier.fromNamespaceAndPath("minecraft", "core/terrain")
	);

	public static boolean isTarget(Identifier fragmentShader) {
		return fragmentShader != null && FRAGMENT_SHADERS.contains(fragmentShader);
	}
}
