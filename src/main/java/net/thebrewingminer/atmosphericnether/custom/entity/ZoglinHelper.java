package net.thebrewingminer.atmosphericnether.custom.entity;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class ZoglinHelper {
    public static boolean canSpawn(WorldAccess world, BlockPos pos) {
        return !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
    }
}
