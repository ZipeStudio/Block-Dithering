package me.zipestudio.blockdithering.yacl;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import java.util.List;
import lombok.experimental.ExtensionMethod;
import me.zipestudio.blockdithering.BlockDithering;
import net.lopymine.mossylib.yacl.api.*;
import net.lopymine.mossylib.yacl.extension.SimpleOptionExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import me.zipestudio.blockdithering.config.LeafyConfig;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherPresets;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;

@ExtensionMethod(SimpleOptionExtension.class)
public class YACLConfigurationScreen {

	private YACLConfigurationScreen() {
		throw new IllegalStateException("Screen class");
	}

	public static Screen createScreen(Screen parent) {
		LeafyConfig defConfig = LeafyConfig.getNewInstance();
		LeafyConfig config = LeafyConfig.getInstance();

		ListOption<String> whitelistOption = buildListOption("dither_blocks_whitelist", new Binding<>() {
			@Override
			public void setValue(List<String> value) {
				config.setPresetWhitelist(config.getActivePreset(), value);
			}

			@Override
			public List<String> getValue() {
				return config.getActiveWhitelist();
			}

			@Override
			public List<String> defaultValue() {
				return DitherPresets.byId(config.getActivePreset()).whitelistCopy();
			}
		});
		ListOption<String> blacklistOption = buildListOption("dither_blocks_blacklist", new Binding<>() {
			@Override
			public void setValue(List<String> value) {
				config.setPresetBlacklist(config.getActivePreset(), value);
			}

			@Override
			public List<String> getValue() {
				return config.getActiveBlacklist();
			}

			@Override
			public List<String> defaultValue() {
				return DitherPresets.byId(config.getActivePreset()).blacklistCopy();
			}
		});

		return SimpleYACLScreen.startBuilder(BlockDithering.MOD_ID, parent, LeafyYaclScreen::new, () -> {
					config.saveAsync();
					DitherBlocks.invalidate();
					reloadChunks();
				})
				.categories(
						getGeneralCategory(defConfig, config, whitelistOption, blacklistOption),
						SimpleCategory.startBuilder("whitelist").groups(whitelistOption),
						SimpleCategory.startBuilder("blacklist").groups(blacklistOption))
				.build();
	}

	@SuppressWarnings("unchecked")
	private static ListOption<String> buildListOption(String id, Binding<List<String>> binding) {
		return (ListOption<String>) SimpleOption.<String>startListBuilder(id)
				.withBinding(binding, true)
				.custom(builder -> builder.controller(StringControllerBuilder::create).initial(""))
				.build(BlockDithering.MOD_ID);
	}

	private static void reloadChunks() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level != null && minecraft.levelRenderer != null) {
			minecraft.levelRenderer.allChanged();
		}
	}

	private static SimpleCategory getGeneralCategory(LeafyConfig defConfig, LeafyConfig config, ListOption<String> whitelist, ListOption<String> blacklist) {
		return SimpleCategory.startBuilder("general")
				.groups(getMainGroup(defConfig, config), getDitheringGroup(defConfig, config), getPresetsGroup(config, whitelist, blacklist));
	}

	private static SimpleGroup getPresetsGroup(LeafyConfig config, ListOption<String> whitelist, ListOption<String> blacklist) {
		return SimpleGroup.startBuilder("presets").options(
				presetButton(config, whitelist, blacklist, DitherPresets.DEFAULT),
				presetButton(config, whitelist, blacklist, DitherPresets.GLASS_ICE),
				presetButton(config, whitelist, blacklist, DitherPresets.CUSTOM)
		);
	}

	private static SimpleOption.ButtonBuilder presetButton(LeafyConfig config, ListOption<String> whitelist, ListOption<String> blacklist, DitherPresets.Preset preset) {
		return SimpleOption.startButtonBuilder("preset_" + preset.id(), (screen, button) -> {
			if (preset.id().equals(config.getActivePreset())) {
				return;
			}
			whitelist.applyValue();
			blacklist.applyValue();
			config.setActivePreset(preset.id());
			whitelist.requestSet(config.getActiveWhitelist());
			blacklist.requestSet(config.getActiveBlacklist());
			config.saveAsync();
			DitherBlocks.invalidate();
			reloadChunks();
		});
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
