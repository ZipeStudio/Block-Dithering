package me.zipestudio.blockdithering.config;

import lombok.*;
import net.lopymine.mossylib.loader.MossyLoader;
import net.lopymine.mossylib.utils.*;
import org.slf4j.*;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.DitherBlocks;
import me.zipestudio.blockdithering.dithering.DitheringDataConfig;

import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class LeafyConfig {

	public static final Codec<LeafyConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("mod_enabled", true, Codec.BOOL, LeafyConfig::isModEnabled),
			option("dithering_options", DitheringDataConfig.defaults(), DitheringDataConfig.CODEC, LeafyConfig::getDitheringOptions),
			option("dither_blocks_whitelist", DitherBlocks.defaultEntries(), Codec.STRING.listOf(), LeafyConfig::getBlocksWhitelist),
			option("dither_blocks_blacklist", DitherBlocks.defaultBlacklist(), Codec.STRING.listOf(), LeafyConfig::getBlocksBlacklist)
	).apply(instance, LeafyConfig::new));

	private static final File CONFIG_FILE = MossyLoader.getConfigDir().resolve(BlockDithering.MOD_ID + ".json5").toFile();
	private static final Logger LOGGER = LoggerFactory.getLogger(BlockDithering.MOD_NAME + "/Config");
	private static LeafyConfig INSTANCE;

	private boolean modEnabled;
	private DitheringDataConfig ditheringOptions;
	private List<String> blocksWhitelist;
	private List<String> blocksBlacklist;

	@SuppressWarnings("unused")
	private LeafyConfig() {
		throw new IllegalArgumentException();
	}

	public static LeafyConfig getInstance() {
		return INSTANCE == null ? reload() : INSTANCE;
	}

	public static LeafyConfig reload() {
		INSTANCE = LeafyConfig.read();
		DitherBlocks.invalidate();
		return INSTANCE;
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
