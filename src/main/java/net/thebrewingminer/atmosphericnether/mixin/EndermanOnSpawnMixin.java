package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.thebrewingminer.atmosphericnether.custom.entity.IDisturbedBiomeFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class EndermanOnSpawnMixin extends LivingEntity {
    protected EndermanOnSpawnMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        if ((Object)this instanceof EndermanEntity enderman) {
            IDisturbedBiomeFlag disturbedBiomeFlag = (IDisturbedBiomeFlag) enderman;
            RegistryKey<Biome> biomeKey = world.getBiome(enderman.getBlockPos()).getKey().orElse(null);
            Identifier dispiritedForest = new Identifier("tbm_nether", "forests/dispirited_forest");
            Identifier oldGrowthDispiritedForest = new Identifier("tbm_nether", "forests/old_growth_dispirited_forest");
            boolean spawnedInDisturbedBiome = false;

            if (biomeKey != null){
                spawnedInDisturbedBiome = (biomeKey.getValue().equals(dispiritedForest) || biomeKey.getValue().equals(oldGrowthDispiritedForest));
            }

            if (spawnedInDisturbedBiome) {
                disturbedBiomeFlag.setSpawnedInDisturbedBiome(true);
            }
        }
    }
}
