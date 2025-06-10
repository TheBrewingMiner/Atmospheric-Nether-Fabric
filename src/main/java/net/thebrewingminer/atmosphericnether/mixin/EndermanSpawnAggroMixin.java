package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.thebrewingminer.atmosphericnether.custom.entity.IDisturbedBiomeFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public abstract class EndermanSpawnAggroMixin extends MobEntity implements IDisturbedBiomeFlag {

    public EndermanSpawnAggroMixin(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Unique
    private boolean spawnedInDisturbedBiome = false;

    @Override
    public boolean isSpawnedInDisturbedBiome() {
        return spawnedInDisturbedBiome;
    }

    @Override
    public void setSpawnedInDisturbedBiome(boolean value) {
        this.spawnedInDisturbedBiome = value;
    }

    @Unique
    private int aggroCooldown = 0;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeAggroData(NbtCompound nbt, CallbackInfo ci) {

        nbt.putBoolean("SpawnedInDisturbedBiome", this.spawnedInDisturbedBiome);
        nbt.putInt("AggroCooldown", this.aggroCooldown);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readAggroData(NbtCompound nbt, CallbackInfo ci) {

        if (nbt.contains("SpawnedInDisturbedBiome")) {
            this.spawnedInDisturbedBiome = nbt.getBoolean("SpawnedInDisturbedBiome").orElse(false);
        }

        if (nbt.contains("AggroCooldown")) {
            this.aggroCooldown = nbt.getInt("AggroCooldown").orElse(0);
        }
    }

    @Inject(method = "mobTick", at = @At("TAIL"))
    private void endermanAggroHandler(CallbackInfo ci) {
        if (this.getWorld().isClient) return;
        EndermanEntity enderman = (EndermanEntity) (Object) this;

        if (!this.spawnedInDisturbedBiome) return;

        final int cooldownCount = 180;
        if (enderman.getTarget() instanceof PlayerEntity) {
            this.aggroCooldown = cooldownCount;
            return;
        }

        if (this.aggroCooldown > 0) {
            this.aggroCooldown--;
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
            ServerWorld serverWorld = (ServerWorld) enderman.getWorld();
            serverWorld.playSoundFromEntity(null, enderman, SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F);
            serverWorld.playSoundFromEntity(null, enderman, SoundEvents.ENTITY_ENDERMAN_SCREAM, this.getSoundCategory(), 1.0F, 1.0F);
            enderman.setTarget(closestPlayer);
            enderman.setAttacking(true);
        }
    }
}