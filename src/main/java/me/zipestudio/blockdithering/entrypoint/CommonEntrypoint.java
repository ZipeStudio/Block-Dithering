package me.zipestudio.blockdithering.entrypoint;

//? if fabric {

/*import me.zipestudio.blockdithering.BlockDithering;

import net.fabricmc.api.ModInitializer;

public class CommonEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		BlockDithering.onInitialize();
	}
}

*///?} elif neoforge {

import me.zipestudio.blockdithering.BlockDithering;
import net.neoforged.fml.common.Mod;

@Mod(BlockDithering.MOD_ID)
public class CommonEntrypoint {

	public CommonEntrypoint() {
		BlockDithering.onInitialize();
	}

}

//?}