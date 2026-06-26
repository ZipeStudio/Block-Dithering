package me.zipestudio.blockdithering.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record PresetData(List<String> whitelist, List<String> blacklist) {

	public static final Codec<PresetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().optionalFieldOf("whitelist", List.of()).forGetter(PresetData::whitelist),
			Codec.STRING.listOf().optionalFieldOf("blacklist", List.of()).forGetter(PresetData::blacklist)
	).apply(instance, PresetData::new));
}
