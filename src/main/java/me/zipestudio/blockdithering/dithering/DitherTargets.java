package me.zipestudio.blockdithering.dithering;

import java.util.Set;
import net.minecraft.resources.Identifier;

public class DitherTargets {

	public static final Set<Identifier> FRAGMENT_SHADERS = Set.of(
			Identifier.fromNamespaceAndPath("minecraft", "core/terrain")
	);

	public static final Identifier SODIUM_FRAGMENT = Identifier.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque");

	public static boolean isTarget(Identifier fragmentShader) {
		return fragmentShader != null && FRAGMENT_SHADERS.contains(fragmentShader);
	}

	public static boolean isSodiumTarget(Identifier fragmentShader) {
		return SODIUM_FRAGMENT.equals(fragmentShader);
	}
}
