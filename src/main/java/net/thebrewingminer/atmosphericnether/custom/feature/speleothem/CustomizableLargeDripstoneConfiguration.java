package net.thebrewingminer.atmosphericnether.custom.feature.speleothem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.LargeDripstoneFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class CustomizableLargeDripstoneConfiguration extends LargeDripstoneFeatureConfig {
    public final BlockStateProvider block;
    public final TagKey<Block> baseTag;
    public static final Codec<TagKey<Block>> BLOCK_TAG_CODEC = Codec.STRING.flatXmap(       // Custom codec to wrap string from JSON file to what MC expects.
            idString -> {
                try {
                    Identifier id = new Identifier(idString);
                    TagKey<Block> tagKey = TagKey.of(RegistryKeys.BLOCK, id);
                    return DataResult.success(tagKey);
                } catch (Exception e) {
                    return DataResult.error(() -> ("Invalid tag id: " + idString));
                }
            },
            tagKey -> DataResult.success(tagKey.id().toString())
    );

    public static final Codec<CustomizableLargeDripstoneConfiguration> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        BlockStateProvider.TYPE_CODEC.fieldOf("block").forGetter((config) -> config.block),
        BLOCK_TAG_CODEC.fieldOf("base_tag").orElse(BlockTags.BASE_STONE_OVERWORLD).forGetter(config -> config.baseTag),
        Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter((config) -> config.floorToCeilingSearchRange),
        IntProvider.createValidatingCodec(1, 60).fieldOf("column_radius").forGetter((config) -> config.columnRadius),
        FloatProvider.createValidatedCodec(0.0F, 20.0F).fieldOf("height_scale").forGetter((config) -> config.heightScale),
        Codec.floatRange(0.1F, 1.0F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter((config) -> config.maxColumnRadiusToCaveHeightRatio),
        FloatProvider.createValidatedCodec(0.1F, 10.0F).fieldOf("stalactite_bluntness").forGetter((config) -> config.stalactiteBluntness),
        FloatProvider.createValidatedCodec(0.1F, 10.0F).fieldOf("stalagmite_bluntness").forGetter((config) -> config.stalagmiteBluntness),
        FloatProvider.createValidatedCodec(0.0F, 2.0F).fieldOf("wind_speed").forGetter((config) -> config.windSpeed),
        Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter((config) -> config.minRadiusForWind),
        Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter((config) -> config.minBluntnessForWind)
    ).apply(instance, CustomizableLargeDripstoneConfiguration::new));

    public CustomizableLargeDripstoneConfiguration(BlockStateProvider block, TagKey<Block> replaceableBlocks, int floorToCeilingSearchRange, IntProvider columnRadius, FloatProvider heightScale, float maxColumnRadiusToCaveHeightRatio, FloatProvider stalactiteBluntness, FloatProvider stalagmiteBluntness, FloatProvider windSpeed, int minRadiusForWind, float minBluntnessForWind) {
        super(floorToCeilingSearchRange, columnRadius, heightScale, maxColumnRadiusToCaveHeightRatio, stalactiteBluntness, stalagmiteBluntness, windSpeed, minRadiusForWind, minBluntnessForWind);
        this.block = block;
        this.baseTag = replaceableBlocks;
    }
}
