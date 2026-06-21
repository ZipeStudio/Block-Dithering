package me.zipestudio.blockdithering.client;

import net.lopymine.mossylib.logger.MossyLogger;

import me.zipestudio.blockdithering.BlockDithering;

public class BlockDitheringClient {

	public static MossyLogger LOGGER = BlockDithering.LOGGER.extend("Client");

	public static void onInitializeClient() {
		LOGGER.info("{} Client Initialized", BlockDithering.MOD_NAME);
	}

}
