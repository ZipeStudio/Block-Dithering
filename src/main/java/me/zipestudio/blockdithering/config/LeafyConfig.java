package me.zipestudio.blockdithering.config;

import lombok.*;
import net.lopymine.mossylib.loader.MossyLoader;
import net.lopymine.mossylib.utils.*;
import org.slf4j.*;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitherPresets;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class LeafyConfig {

	public static final Codec<LeafyConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("config_version", 0, Codec.INT, LeafyConfig::getConfigVersion),
			option("mod_enabled", true, Codec.BOOL, LeafyConfig::isModEnabled),
			option("dithering_options", (Supplier<DitheringDataConfig>) DitheringDataConfig::defaults, DitheringDataConfig.CODEC, LeafyConfig::getDitheringOptions),
			option("active_preset", DitherPresets.DEFAULT_ID, Codec.STRING, LeafyConfig::getActivePreset),
			option("presets", DitherPresets.defaultMap(), Codec.unboundedMap(Codec.STRING, PresetData.CODEC), LeafyConfig::getPresets),
			option("dither_blocks_whitelist", List.of(), Codec.STRING.listOf(), LeafyConfig::getLegacyWhitelist),
			option("dither_blocks_blacklist", List.of(), Codec.STRING.listOf(), LeafyConfig::getLegacyBlacklist)
	).apply(instance, LeafyConfig::new));

	private static final File CONFIG_FILE = MossyLoader.getConfigDir().resolve(BlockDithering.MOD_ID + ".json5").toFile();
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockDithering.MOD_NAME + "/Config");
	private static LeafyConfig INSTANCE;

	private static final int CURRENT_CONFIG_VERSION = 1;

	private int configVersion;
	private boolean modEnabled;
	private DitheringDataConfig ditheringOptions;
	private String activePreset;
	private Map<String, PresetData> presets;
	private List<String> legacyWhitelist;
	private List<String> legacyBlacklist;

	public PresetData presetData(String id) {
		PresetData data = presets.get(id);
		if (data != null) {
			return data;
		}
		DitherPresets.Preset preset = DitherPresets.byId(id);
		return new PresetData(preset.whitelistCopy(), preset.blacklistCopy());
	}

	public List<String> getActiveWhitelist() {
		return presetData(activePreset).whitelist();
	}

	public List<String> getActiveBlacklist() {
		return presetData(activePreset).blacklist();
	}

	public List<String> getPresetWhitelist(String id) {
		return presetData(id).whitelist();
	}

	public List<String> getPresetBlacklist(String id) {
		return presetData(id).blacklist();
	}

	public void setPresetWhitelist(String id, List<String> list) {
		Map<String, PresetData> copy = new LinkedHashMap<>(presets);
		copy.put(id, new PresetData(new ArrayList<>(list), getPresetBlacklist(id)));
		presets = copy;
	}

	public void setPresetBlacklist(String id, List<String> list) {
		Map<String, PresetData> copy = new LinkedHashMap<>(presets);
		copy.put(id, new PresetData(getPresetWhitelist(id), new ArrayList<>(list)));
		presets = copy;
	}

	@SuppressWarnings("unused")
	private LeafyConfig() {
		throw new IllegalArgumentException();
	}

	public static LeafyConfig getInstance() {
		return INSTANCE == null ? reload() : INSTANCE;
	}

	public static LeafyConfig reload() {
		INSTANCE = LeafyConfig.read();
		INSTANCE.migrate();
		DitherBlocks.invalidate();
		return INSTANCE;
	}

	private void migrate() {
		int from = configVersion;
		int v = from;

		if (v < 1) {
			migrateLegacy();
			v = 1;
		}

		if (v != from) {
			configVersion = v;
			save();
			LOGGER.info("Migrated config from version {} to {}.", from, v);
		}
	}

	private void migrateLegacy() {
		boolean hasLegacy = (legacyWhitelist != null && !legacyWhitelist.isEmpty())
				|| (legacyBlacklist != null && !legacyBlacklist.isEmpty());
		if (!hasLegacy) {
			return;
		}
		Map<String, PresetData> copy = new LinkedHashMap<>(presets);
		copy.put(DitherPresets.CUSTOM.id(), new PresetData(
				legacyWhitelist == null ? new ArrayList<>() : new ArrayList<>(legacyWhitelist),
				legacyBlacklist == null ? new ArrayList<>() : new ArrayList<>(legacyBlacklist)));
		presets = copy;
		activePreset = DitherPresets.CUSTOM.id();
		legacyWhitelist = new ArrayList<>();
		legacyBlacklist = new ArrayList<>();
		LOGGER.info("Migrated legacy v1.0.0 block lists into the '{}' preset.", DitherPresets.CUSTOM.id());
	}

	public static LeafyConfig getNewInstance() {
		return CodecUtils.parseNewInstanceHacky(CODEC);
	}

	private static LeafyConfig read() {
		return ConfigUtils.readConfig(CODEC, CONFIG_FILE, LOGGER);
	}

	public void saveAsync() {
		CompletableFuture.runAsync(this::save);
	}

	public void save() {
		ConfigUtils.saveConfig(this, CODEC, CONFIG_FILE, LOGGER);
	}
}
