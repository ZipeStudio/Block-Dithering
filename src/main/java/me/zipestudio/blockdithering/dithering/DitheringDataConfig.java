package me.zipestudio.blockdithering.dithering;

import com.mojang.serialization.Codec;
import lombok.*;

import static com.mojang.serialization.Codec.DOUBLE;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class DitheringDataConfig {

    public static final Codec<DitheringDataConfig> CODEC = create((instance) -> instance.group(
            option("pixel_size", 1.0D, DOUBLE, DitheringDataConfig::getPixelSize),
            option("min_visibility", 0.45D, DOUBLE, DitheringDataConfig::getMinVisibility),
            option("near_distance", -0.5D, DOUBLE, DitheringDataConfig::getNearDistance),
            option("far_distance", 3.0D, DOUBLE, DitheringDataConfig::getFarDistance)
    ).apply(instance, DitheringDataConfig::new));

    private double pixelSize;
    private double minVisibility;
    private double nearDistance;
    private double farDistance;

    public static DitheringDataConfig defaults() {
        return new DitheringDataConfig(
                1.0D,
                0.45D,
                -0.5D,
                3.0D
        );
    }
}
