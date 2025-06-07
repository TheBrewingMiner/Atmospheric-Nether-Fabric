package net.thebrewingminer.atmosphericnether.custom.feature.speleothem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.enums.Thickness;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;

import java.util.function.Consumer;

public class CustomizableLargeDripstoneUtils {

    public CustomizableLargeDripstoneUtils() {}

    protected static double scaleHeightFromRadius(double radius, double scale, double heightScale, double bluntness) {
        if (radius < bluntness) {
            radius = bluntness;
        }

        double d = 0.384;
        double e = radius / scale * 0.384;
        double f = 0.75 * Math.pow(e, 1.3333333333333333);
        double g = Math.pow(e, 0.6666666666666666);
        double h = 0.3333333333333333 * Math.log(e);
        double i = heightScale * (f - g - h);
        i = Math.max(i, 0.0);
        return i / 0.384 * scale;
    }

    protected static boolean canGenerateBase(StructureWorldAccess world, BlockPos pos, int height) {
        if (canGenerateOrLava(world, pos)) {
            return false;
        } else {
            float f = 6.0F;
            float g = 6.0F / (float)height;

            for(float h = 0.0F; h < 6.2831855F; h += g) {
                int i = (int)(MathHelper.cos(h) * (float)height);
                int j = (int)(MathHelper.sin(h) * (float)height);
                if (canGenerateOrLava(world, pos.add(i, 0, j))) {
                    return false;
                }
            }

            return true;
        }
    }

    protected static boolean canGenerate(WorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, CustomizableLargeDripstoneUtils::canGenerate);
    }

    protected static boolean canGenerateOrLava(WorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, CustomizableLargeDripstoneUtils::canGenerateOrLava);
    }

    protected static void getDripstoneThickness(Direction direction, int height, boolean merge, Consumer<BlockState> callback) {
        if (height >= 3) {
            callback.accept(getState(direction, Thickness.BASE));

            for(int i = 0; i < height - 3; ++i) {
                callback.accept(getState(direction, Thickness.MIDDLE));
            }
        }

        if (height >= 2) {
            callback.accept(getState(direction, Thickness.FRUSTUM));
        }

        if (height >= 1) {
            callback.accept(getState(direction, merge ? Thickness.TIP_MERGE : Thickness.TIP));
        }

    }

    protected static void generatePointedDripstone(WorldAccess world, BlockPos pos, Direction direction, int height, boolean merge, TagKey<Block> replaceable) {
        if (canReplace(world.getBlockState(pos.offset(direction.getOpposite())), replaceable)) {
            BlockPos.Mutable mutable = pos.mutableCopy();
            getDripstoneThickness(direction, height, merge, (state) -> {
                if (state.isOf(Blocks.POINTED_DRIPSTONE)) {
                    state = state.with(PointedDripstoneBlock.WATERLOGGED, world.isWater(mutable));
                }

                world.setBlockState(mutable, state, 2);
                mutable.move(direction);
            });
        }
    }

    protected static boolean generateDripstoneBlock(WorldAccess world, BlockPos pos, BlockState blockToPlace, TagKey<Block> replaceable) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(replaceable)) {
            world.setBlockState(pos, blockToPlace, 2);
            return true;
        } else {
            return false;
        }
    }

    private static BlockState getState(Direction direction, Thickness thickness) {
        return Blocks.POINTED_DRIPSTONE.getDefaultState().with(PointedDripstoneBlock.VERTICAL_DIRECTION, direction).with(PointedDripstoneBlock.THICKNESS, thickness);
    }

    public static boolean canReplaceOrLava(BlockState state, TagKey<Block> replaceable) {
        return canReplace(state,replaceable) || state.isOf(Blocks.LAVA);
    }

    public static boolean canReplace(BlockState state, TagKey<Block> replaceable) {
        return state.isOf(Blocks.DRIPSTONE_BLOCK) || state.isIn(replaceable);
    }

    public static boolean canGenerate(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER);
    }

    public static boolean cannotGenerate(BlockState state) {
        return !state.isAir() && !state.isOf(Blocks.WATER);
    }

    public static boolean canGenerateOrLava(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA);
    }
}
