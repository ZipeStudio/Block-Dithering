package me.zipestudio.blockdithering.yacl;

import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import lombok.experimental.ExtensionMethod;
import me.zipestudio.blockdithering.BlockDithering;
import net.lopymine.mossylib.yacl.api.*;
import net.lopymine.mossylib.yacl.extension.SimpleOptionExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import me.zipestudio.blockdithering.config.LeafyConfig;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;

@ExtensionMethod(SimpleOptionExtension.class)
public class YACLConfigurationScreen {

	private YACLConfigurationScreen() {
		throw new IllegalStateException("Screen class");
	}

	public static Screen createScreen(Screen parent) {
		LeafyConfig defConfig = LeafyConfig.getNewInstance();
		LeafyConfig config = LeafyConfig.getInstance();

		return SimpleYACLScreen.startBuilder(BlockDithering.MOD_ID, parent, LeafyYaclScreen::new, () -> {
					config.saveAsync();
					DitherBlocks.invalidate();
					reloadChunks();
				})
				.categories(
						getGeneralCategory(defConfig, config),
						getBlocksCategory(defConfig, config),
						getBlacklistCategory(defConfig, config))
				.build();
	}

	private static void reloadChunks() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null && minecraft.levelRenderer != null) {
			minecraft.levelRenderer.allChanged();
		}
	}

	private static SimpleCategory getGeneralCategory(LeafyConfig defConfig, LeafyConfig config) {
		return SimpleCategory.startBuilder("general")
				.groups(getMainGroup(defConfig, config), getDitheringGroup(defConfig, config));
	}

	private static SimpleCategory getBlocksCategory(LeafyConfig defConfig, LeafyConfig config) {
		return SimpleCategory.startBuilder("whitelist")
				.options(SimpleOption.<String>startListBuilder("dither_blocks_whitelist")
						.withBinding(defConfig.getBlocksWhitelist(), config::getBlocksWhitelist, config::setBlocksWhitelist, true)
						.custom(builder -> builder.controller(StringControllerBuilder::create).initial("")));
	}

	private static SimpleCategory getBlacklistCategory(LeafyConfig defConfig, LeafyConfig config) {
		return SimpleCategory.startBuilder("blacklist")
				.options(SimpleOption.<String>startListBuilder("dither_blocks_blacklist")
						.withBinding(defConfig.getBlocksBlacklist(), config::getBlocksBlacklist, config::setBlocksBlacklist, true)
						.custom(builder -> builder.controller(StringControllerBuilder::create).initial("")));
	}

	private static SimpleGroup getMainGroup(LeafyConfig defConfig, LeafyConfig config) {
		return SimpleGroup.startBuilder("main").options(
				SimpleOption.<Boolean>startBuilder("mod_enabled")
						.withBinding(defConfig.isModEnabled(), config::isModEnabled, config::setModEnabled, true)
						.withController()
		);
	}

	private static SimpleGroup getDitheringGroup(LeafyConfig defConfig, LeafyConfig config) {
		DitheringDataConfig def = defConfig.getDitheringOptions();
		DitheringDataConfig cur = config.getDitheringOptions();
		return SimpleGroup.startBuilder("dithering_options").options(
				SimpleOption.<Double>startBuilder("dither_pixel_size")
						.withBinding(def.getPixelSize(), cur::getPixelSize, cur::setPixelSize, true)
						.withController(0.0D, 32.0D, 0.1D),
				SimpleOption.<Double>startBuilder("dither_min_visibility")
						.withBinding(def.getMinVisibility(), cur::getMinVisibility, cur::setMinVisibility, true)
						.withController(0.0D, 1.0D, 0.01D),
				SimpleOption.<Double>startBuilder("dither_near_distance")
				.withBinding(def.getNearDistance(), cur::getNearDistance, cur::setNearDistance, true)
				.withController(-4.0D, 16.0D, 0.1D),
				SimpleOption.<Double>startBuilder("dither_far_distance")
				.withBinding(def.getFarDistance(), cur::getFarDistance, cur::setFarDistance, true)
				.withController(0.0D, 32.0D, 0.1D)

		);
	}

}


