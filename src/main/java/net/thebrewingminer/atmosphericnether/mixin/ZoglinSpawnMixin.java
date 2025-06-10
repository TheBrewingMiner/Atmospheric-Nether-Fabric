package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldAccess;
import net.thebrewingminer.atmosphericnether.custom.entity.ZoglinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileEntity.class)
public abstract class ZoglinSpawnMixin {
    @Inject(method = "canSpawnIgnoreLightLevel", at = @At("HEAD"), cancellable = true)
    private static void zoglinCanSpawn(EntityType<? extends HostileEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir){
        if (type == EntityType.ZOGLIN){
            boolean canSpawnIgnoreLightLevel = world.getDifficulty() != Difficulty.PEACEFUL && HostileEntity.canMobSpawn(type, world, spawnReason, pos, random);
            boolean isNotOnWartBlock = ZoglinHelper.canSpawn(world, pos);
            boolean zoglinCanSpawn = canSpawnIgnoreLightLevel && isNotOnWartBlock;
            cir.setReturnValue(zoglinCanSpawn);
        }
    }
}