package me.zipestudio.blockdithering;

import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

public class BlockDithering {

	public static final String MOD_NAME = /*$ mod_name*/ "Block Dithering";
	public static final String MOD_ID = /*$ mod_id*/ "block_dithering";

	public static MossyLogger LOGGER = new MossyLogger(BlockDithering.MOD_NAME);

	public static ResourceLocation id(String path) {
		//? if >=1.21 {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
		//?} else {
		/*return ResourceLocation.tryBuild(MOD_ID, path);
		 *///?}
	}

	public static ResourceLocation parseId(String path) {
		//? if >=1.21 {
		return ResourceLocation.parse(path);
		//?} else {
		/*return new ResourceLocation(path);
		 *///?}
	}

	public static MutableComponent text(String path, Object... args) {
		return Component.translatable(String.format("%s.%s", MOD_ID, path), args);
	}

	public static void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}
}