package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZoglinEntity.class)
public abstract class ZoglinSpawnMixin extends AnimalEntity implements Monster, Hoglin {

    protected ZoglinSpawnMixin(EntityType<? extends HoglinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (world.getRandom().nextFloat() < 0.35F) {
            this.setBaby(true);
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
}
