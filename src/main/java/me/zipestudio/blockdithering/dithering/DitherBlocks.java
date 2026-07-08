package me.zipestudio.blockdithering.dithering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import me.zipestudio.blockdithering.config.LeafyConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DitherBlocks {

	private static final Rules WHITELIST = new Rules();
	private static final Rules BLACKLIST = new Rules();

	private DitherBlocks() { }

	public static List<String> defaultEntries() {
		return new ArrayList<>(List.of(
				"minecraft:*glass*",
				"minecraft:*bars*",
				"#minecraft:flowers",
				"#minecraft:fences",
				"#minecraft:chains",
				"#minecraft:corals",
				"#minecraft:wall_corals",
				"minecraft:dead_*coral*",
				"#minecraft:saplings",
				"#minecraft:climbable",
				"#minecraft:replaceable",
				"minecraft:*copper_grate*",
				"minecraft:ice",
				"minecraft:cobweb",
				"minecraft:vault",
				"minecraft:spawner",
				"minecraft:kelp",
				"minecraft:kelp_plant",
				"minecraft:frogspawn",
				"minecraft:sculk_vein",
				"minecraft:weeping_vines",
				"minecraft:sugar_cane",
				"minecraft:scaffolding",
				"minecraft:honey_block",
				"minecraft:slime_block",
				"minecraft:firefly_bush",
				"minecraft:big_dripleaf",
				"minecraft:small_dripleaf",
				"minecraft:bamboo_sapling",
				"minecraft:warped_fungus",
				"minecraft:crimson_fungus",
				"minecraft:pale_hanging_moss"
		));
	}

	public static List<String> defaultBlacklist() {
		return new ArrayList<>(List.of(
				"minecraft:flowering_azalea_leaves"
		));
	}

	private static final Map<BlockState, Boolean> CACHE = new ConcurrentHashMap<>();

	public static boolean isTarget(BlockState state) {
		if (!LeafyConfig.getInstance().isModEnabled()) {
			return false;
		}
		return CACHE.computeIfAbsent(state, DitherBlocks::compute);
	}

	private static boolean compute(BlockState state) {
		VoxelShape occlusion = state.getOcclusionShape();
		for (Direction dir : Direction.values()) {
			if (Block.isFaceFull(occlusion, dir)) {
				return false;
			}
		}

		LeafyConfig config = LeafyConfig.getInstance();
		WHITELIST.ensure(config.getActiveWhitelist());
		BLACKLIST.ensure(config.getActiveBlacklist());

		if (BLACKLIST.matches(state)) {
			return false;
		}

		return WHITELIST.matches(state);
	}

	public static void invalidate() {
		CACHE.clear();
		WHITELIST.invalidate();
		BLACKLIST.invalidate();
	}

	private static final class Rules {

		private record Parsed(List<String> source, Set<Block> blocks, List<TagKey<Block>> tags, List<Pattern> patterns) { }

		private static final Parsed EMPTY = new Parsed(null, Set.of(), List.of(), List.of());

		private volatile Parsed parsed = EMPTY;

		void ensure(List<String> entries) {
			if (this.parsed.source() == entries) {
				return;
			}
			Set<Block> parsedBlocks = new HashSet<>();
			List<TagKey<Block>> parsedTags = new ArrayList<>();
			List<Pattern> parsedPatterns = new ArrayList<>();
			if (entries != null) {
				for (String raw : entries) {
					if (raw == null || raw.isBlank()) {
						continue;
					}
					String entry = raw.trim();
					try {
						if (entry.startsWith("#")) {
							parsedTags.add(TagKey.create(Registries.BLOCK, Identifier.parse(entry.substring(1))));
						} else if (entry.indexOf('*') >= 0) {
							parsedPatterns.add(toPattern(entry));
						} else {
							Block block = BuiltInRegistries.BLOCK.getValue(Identifier.parse(entry));
							if (block != null) {
								parsedBlocks.add(block);
							}
						}
					} catch (Exception ignored) {
					}
				}
			}
			this.parsed = new Parsed(entries, parsedBlocks, parsedTags, parsedPatterns);
		}

		boolean matches(BlockState state) {
			Parsed p = this.parsed;
			Block block = state.getBlock();
			if (p.blocks().contains(block)) {
				return true;
			}
			for (TagKey<Block> tag : p.tags()) {
				if (state.is(tag)) {
					return true;
				}
			}
			if (!p.patterns().isEmpty()) {
				Identifier id = BuiltInRegistries.BLOCK.getKey(block);
				if (id != null) {
					String key = id.toString();
					for (Pattern pattern : p.patterns()) {
						if (pattern.matcher(key).matches()) {
							return true;
						}
					}
				}
			}
			return false;
		}

		void invalidate() {
			this.parsed = EMPTY;
		}

		private static Pattern toPattern(String wildcard) {
			StringBuilder regex = new StringBuilder();
			String[] parts = wildcard.split("\\*", -1);
			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					regex.append(".*");
				}
				if (!parts[i].isEmpty()) {
					regex.append(Pattern.quote(parts[i]));
				}
			}
			return Pattern.compile(regex.toString());
		}
	}
}
