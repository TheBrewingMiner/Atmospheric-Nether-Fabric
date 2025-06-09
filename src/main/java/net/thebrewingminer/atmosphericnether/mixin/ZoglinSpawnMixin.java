package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ZoglinEntity.class)
public abstract class ZoglinSpawnMixin extends HostileEntity implements Monster, Hoglin {

    protected ZoglinSpawnMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        if (world.getRandom().nextFloat() < 0.35F) {
            this.setBaby(true);
        }

        return super.initialize(world, difficulty, spawnReason, entityData);
    }
}
