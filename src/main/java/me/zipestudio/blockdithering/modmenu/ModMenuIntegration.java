package me.zipestudio.blockdithering.modmenu;

import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.yacl.YACLConfigurationScreen;
import net.lopymine.mossylib.modmenu.AbstractModMenuIntegration;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegration extends AbstractModMenuIntegration {

	@Override
	protected String getModId() {
		return BlockDithering.MOD_ID;
	}

	@Override
	protected Screen createConfigScreen(Screen screen) {
		return YACLConfigurationScreen.createScreen(screen);
	}
}
