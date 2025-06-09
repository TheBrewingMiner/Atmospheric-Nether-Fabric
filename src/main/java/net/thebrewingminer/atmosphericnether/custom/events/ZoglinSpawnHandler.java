package net.thebrewingminer.atmosphericnether.custom.events;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;
import net.thebrewingminer.atmosphericnether.custom.entity.ZoglinHelper;

public class ZoglinSpawnHandler {
    public static void registerSpawning(){
        SpawnRestriction.register(
                EntityType.ZOGLIN,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                ZoglinHelper::canSpawn
        );
    }
}
