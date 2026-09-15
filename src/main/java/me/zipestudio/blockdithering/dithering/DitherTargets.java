package me.zipestudio.blockdithering.dithering;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class DitherTargets {

	public static final Set<ResourceLocation> FRAGMENT_SHADERS = Set.of(
			ResourceLocation.fromNamespaceAndPath("minecraft", "core/terrain")
	);

	public static final ResourceLocation SODIUM_FRAGMENT = ResourceLocation.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque");

	public static boolean isTarget(ResourceLocation fragmentShader) {
		return fragmentShader != null && FRAGMENT_SHADERS.contains(fragmentShader);
	}

	public static boolean isSodiumTarget(ResourceLocation fragmentShader) {
		return SODIUM_FRAGMENT.equals(fragmentShader);
	}

	public static final ResourceLocation OUTLINE_FRAGMENT = ResourceLocation.fromNamespaceAndPath("minecraft", "core/rendertype_lines");

	public static boolean isOutlineTarget(ResourceLocation fragmentShader) {
		return OUTLINE_FRAGMENT.equals(fragmentShader);
	}

	public static final Set<String> LEGACY_SHADERS = Set.of(
			"rendertype_solid",
			"rendertype_cutout_mipped",
			"rendertype_cutout",
			"rendertype_translucent"
	);

	public static final String LEGACY_OUTLINE_SHADER = "rendertype_lines";

	public static boolean isLegacyTarget(String shaderName) {
		return shaderName != null && LEGACY_SHADERS.contains(stripMinecraftNamespace(shaderName));
	}

	public static boolean isLegacyOutline(String shaderName) {
		return shaderName != null && LEGACY_OUTLINE_SHADER.equals(stripMinecraftNamespace(shaderName));
	}

	private static String stripMinecraftNamespace(String shaderName) {
		return shaderName.startsWith("minecraft:") ? shaderName.substring("minecraft:".length()) : shaderName;
	}
}
