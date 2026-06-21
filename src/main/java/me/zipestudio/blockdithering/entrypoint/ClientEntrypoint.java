package me.zipestudio.blockdithering.entrypoint;

//? if fabric {

/*import net.fabricmc.api.ClientModInitializer;

import me.zipestudio.blockdithering.client.BlockDitheringClient;

public class ClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockDitheringClient.onInitializeClient();
	}
}

*///?} elif neoforge {

import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.client.BlockDitheringClient;
import me.zipestudio.blockdithering.modmenu.ModMenuIntegration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = BlockDithering.MOD_ID, dist = Dist.CLIENT)
public class ClientEntrypoint {

	public ClientEntrypoint(ModContainer container) {
		BlockDitheringClient.onInitializeClient();
		ModMenuIntegration integration = new ModMenuIntegration();
		integration.register(container);
	}

}

//?}