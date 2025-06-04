package net.thebrewingminer.atmosphericnether;

import net.fabricmc.api.ModInitializer;
import net.thebrewingminer.atmosphericnether.custom.events.ZoglinSpawnHandler;
import net.thebrewingminer.atmosphericnether.custom.feature.ModConfiguredFeature;

public class AtmosphericNether implements ModInitializer {
	public static final String MOD_ID = "atmospheric_nether";

	@Override
	public void onInitialize() {
		ModConfiguredFeature.register();
		ZoglinSpawnHandler.registerSpawning();
	}
}
