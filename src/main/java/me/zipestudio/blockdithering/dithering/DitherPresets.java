package me.zipestudio.blockdithering.dithering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.zipestudio.blockdithering.config.PresetData;

public final class DitherPresets {

	private DitherPresets() { }

	public static final String DEFAULT_ID = "default";

	public record Preset(String id, List<String> whitelist, List<String> blacklist) {

		public List<String> whitelistCopy() {
			return new ArrayList<>(whitelist);
		}

		public List<String> blacklistCopy() {
			return new ArrayList<>(blacklist);
		}
	}

	public static final Preset DEFAULT = new Preset("default",
			DitherBlocks.defaultEntries(),
			DitherBlocks.defaultBlacklist());

	public static final Preset GLASS_ICE = new Preset("glass_ice",
			List.of(
					"minecraft:*glass*",
					"#minecraft:glass_panes",
					"#minecraft:glass_blocks",
					"#c:glass_panes",
					"#c:glass_blocks",
					"minecraft:ice"
			),
			List.of());

	public static final Preset CUSTOM = new Preset("custom",
			List.of(),
			List.of());

	public static final List<Preset> ALL = List.of(DEFAULT, GLASS_ICE, CUSTOM);

	public static Preset byId(String id) {
		for (Preset preset : ALL) {
			if (preset.id().equals(id)) {
				return preset;
			}
		}
		return DEFAULT;
	}

	public static Map<String, PresetData> defaultMap() {
		Map<String, PresetData> map = new LinkedHashMap<>();
		for (Preset preset : ALL) {
			map.put(preset.id(), new PresetData(preset.whitelistCopy(), preset.blacklistCopy()));
		}
		return map;
	}
}
