package net.thebrewingminer.atmosphericnether.custom.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;

public class EndermanAggroData {
    public static final TrackedData<Boolean> SPAWNED_IN_DISTURBED_BIOME = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Integer> AGGRO_COOLDOWN = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.INTEGER);
}

