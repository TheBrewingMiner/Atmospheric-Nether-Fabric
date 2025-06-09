package net.thebrewingminer.atmosphericnether.custom.feature;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.thebrewingminer.atmosphericnether.custom.feature.speleothem.CustomizableLargeDripstoneConfiguration;
import net.thebrewingminer.atmosphericnether.custom.feature.speleothem.CustomizableLargeDripstoneFeature;
import net.thebrewingminer.atmosphericnether.AtmosphericNether;

public class ModConfiguredFeature {
    public static final Feature<CustomizableLargeDripstoneConfiguration> CUSTOMIZABLE_LARGE_DRIPSTONE =
            new CustomizableLargeDripstoneFeature(CustomizableLargeDripstoneConfiguration.CODEC);

    public static void register() {
        Registry.register(
                Registries.FEATURE,
                Identifier.of(AtmosphericNether.MOD_ID, "customizable_large_dripstone"),
                CUSTOMIZABLE_LARGE_DRIPSTONE
        );
    }
}
