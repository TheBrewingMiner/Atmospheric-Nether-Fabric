package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.thebrewingminer.atmosphericnether.custom.entity.EndermanAggroData;

@Mixin(EndermanEntity.class)
public abstract class EndermanSpawnAggroMixin extends MobEntity {

    public EndermanSpawnAggroMixin(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void registerCustomTrackedData(CallbackInfo ci) {
        this.dataTracker.startTracking(EndermanAggroData.SPAWNED_IN_DISTURBED_BIOME, false);
        this.dataTracker.startTracking(EndermanAggroData.AGGRO_COOLDOWN, 0);
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void endermanAggroHandler(CallbackInfo ci) {
        if (this.getWorld().isClient) return;
        EndermanEntity enderman = (EndermanEntity) (Object) this;

        if (!enderman.getDataTracker().get(EndermanAggroData.SPAWNED_IN_DISTURBED_BIOME)) return;

        final int cooldownCount = 180;
        if (enderman.getTarget() instanceof PlayerEntity) {
            enderman.getDataTracker().set(EndermanAggroData.AGGRO_COOLDOWN, cooldownCount);
            return;
        }

        int cooldown = enderman.getDataTracker().get(EndermanAggroData.AGGRO_COOLDOWN);
        if (cooldown > 0) {
            enderman.getDataTracker().set(EndermanAggroData.AGGRO_COOLDOWN, cooldown - 1);
            return;
        }

        int verticalOffset = 12;
        int horizontalOffset = verticalOffset/2;
        double closestDist = Double.MAX_VALUE;
        PlayerEntity closestPlayer = null;

        for (PlayerEntity player : this.getWorld().getEntitiesByClass(PlayerEntity.class,
                new Box(enderman.getBlockPos()).expand(verticalOffset, horizontalOffset, verticalOffset),
                p -> !p.isCreative() && !p.isSpectator())
        ) {
            double dist = enderman.squaredDistanceTo(player);
            if (dist < closestDist) {
                closestDist = dist;
                closestPlayer = player;
            }
        }

        if (closestPlayer != null) {
            enderman.getWorld().playSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            enderman.setTarget(closestPlayer);
            enderman.setAttacking(true);
        }
    }
}