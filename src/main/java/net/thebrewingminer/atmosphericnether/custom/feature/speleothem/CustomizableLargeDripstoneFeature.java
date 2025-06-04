package net.thebrewingminer.atmosphericnether.custom.feature.speleothem;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.CaveSurface;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CustomizableLargeDripstoneFeature extends Feature<CustomizableLargeDripstoneConfiguration> {

    public CustomizableLargeDripstoneFeature(Codec<CustomizableLargeDripstoneConfiguration> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<CustomizableLargeDripstoneConfiguration> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        CustomizableLargeDripstoneConfiguration config = context.getConfig();
        Random random = context.getRandom();

        BlockState blockToPlace = config.block.getBlockState(random, blockPos);
        TagKey<Block> replaceable = config.baseTag;

        if (!CustomizableLargeDripstoneUtils.canGenerate(structureWorldAccess, blockPos)) {
            return false;
        } else {
            Optional<CaveSurface> optional = CaveSurface.create(structureWorldAccess, blockPos, config.floorToCeilingSearchRange, CustomizableLargeDripstoneUtils::canGenerate, (state) -> CustomizableLargeDripstoneUtils.canReplaceOrLava(state, blockToPlace, replaceable));
            if (optional.isPresent() && optional.get() instanceof CaveSurface.Bounded bounded) {
                if (bounded.getHeight() < 4) {
                    return false;
                } else {
                    int i = (int)((float)bounded.getHeight() * config.maxColumnRadiusToCaveHeightRatio);
                    int j = MathHelper.clamp(i, config.columnRadius.getMin(), config.columnRadius.getMax());
                    int k = MathHelper.nextBetween(random, config.columnRadius.getMin(), j);
                    CustomizableLargeDripstoneFeature.CustomizableDripstoneGenerator CustomizableDripstoneGenerator = createGenerator(blockPos.withY(bounded.getCeiling() - 1), false, random, k, config.stalactiteBluntness, config.heightScale);
                    CustomizableLargeDripstoneFeature.CustomizableDripstoneGenerator CustomizableDripstoneGenerator2 = createGenerator(blockPos.withY(bounded.getFloor() + 1), true, random, k, config.stalagmiteBluntness, config.heightScale);
                    CustomizableLargeDripstoneFeature.WindModifier windModifier;
                    if (CustomizableDripstoneGenerator.generateWind(config) && CustomizableDripstoneGenerator2.generateWind(config)) {
                        windModifier = new CustomizableLargeDripstoneFeature.WindModifier(blockPos.getY(), random, config.windSpeed);
                    } else {
                        windModifier = CustomizableLargeDripstoneFeature.WindModifier.create();
                    }

                    boolean bl = CustomizableDripstoneGenerator.canGenerate(structureWorldAccess, windModifier);
                    boolean bl2 = CustomizableDripstoneGenerator2.canGenerate(structureWorldAccess, windModifier);
                    if (bl) {
                        CustomizableDripstoneGenerator.generate(structureWorldAccess, random, windModifier, config);
                    }

                    if (bl2) {
                        CustomizableDripstoneGenerator2.generate(structureWorldAccess, random, windModifier, config);
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static CustomizableLargeDripstoneFeature.CustomizableDripstoneGenerator createGenerator(BlockPos pos, boolean isStalagmite, Random random, int scale, FloatProvider bluntness, FloatProvider heightScale) {
        return new CustomizableLargeDripstoneFeature.CustomizableDripstoneGenerator(pos, isStalagmite, scale, bluntness.get(random), heightScale.get(random));
    }

    static final class CustomizableDripstoneGenerator {
        private BlockPos pos;
        private final boolean isStalagmite;
        private int scale;
        private final double bluntness;
        private final double heightScale;

        CustomizableDripstoneGenerator(BlockPos pos, boolean isStalagmite, int scale, double bluntness, double heightScale) {
            this.pos = pos;
            this.isStalagmite = isStalagmite;
            this.scale = scale;
            this.bluntness = bluntness;
            this.heightScale = heightScale;
        }

        private int getBaseScale() {
            return this.scale(0.0F);
        }

        private int getBottomY() {
            return this.isStalagmite ? this.pos.getY() : this.pos.getY() - this.getBaseScale();
        }

        private int getTopY() {
            return !this.isStalagmite ? this.pos.getY() : this.pos.getY() + this.getBaseScale();
        }

        boolean canGenerate(StructureWorldAccess world, CustomizableLargeDripstoneFeature.WindModifier wind) {
            while(this.scale > 1) {
                BlockPos.Mutable mutable = this.pos.mutableCopy();
                int i = Math.min(10, this.getBaseScale());

                for(int j = 0; j < i; ++j) {
                    if (world.getBlockState(mutable).isOf(Blocks.LAVA)) {
                        return false;
                    }

                    if (CustomizableLargeDripstoneUtils.canGenerateBase(world, wind.modify(mutable), this.scale)) {
                        this.pos = mutable;
                        return true;
                    }

                    mutable.move(this.isStalagmite ? Direction.DOWN : Direction.UP);
                }

                this.scale /= 2;
            }

            return false;
        }

        private int scale(float height) {
            return (int)CustomizableLargeDripstoneUtils.scaleHeightFromRadius(height, this.scale, this.heightScale, this.bluntness);
        }

        void generate(StructureWorldAccess world, Random random, CustomizableLargeDripstoneFeature.WindModifier wind, CustomizableLargeDripstoneConfiguration config) {
            for(int i = -this.scale; i <= this.scale; ++i) {
                for(int j = -this.scale; j <= this.scale; ++j) {
                    float f = MathHelper.sqrt((float)(i * i + j * j));
                    if (!(f > (float)this.scale)) {
                        int k = this.scale(f);
                        if (k > 0) {
                            if ((double)random.nextFloat() < 0.2) {
                                k = (int)((float)k * MathHelper.nextBetween(random, 0.8F, 1.0F));
                            }

                            BlockPos.Mutable mutable = this.pos.add(i, 0, j).mutableCopy();
                            boolean bl = false;
                            int l = this.isStalagmite ? world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, mutable.getX(), mutable.getZ()) : Integer.MAX_VALUE;

                            for(int m = 0; m < k && mutable.getY() < l; ++m) {
                                BlockPos blockPos = wind.modify(mutable);
                                if (CustomizableLargeDripstoneUtils.canGenerateOrLava(world, blockPos)) {
                                    bl = true;
                                    Block block = config.block.getBlockState(random, blockPos).getBlock();
                                    world.setBlockState(blockPos, block.getDefaultState(), 2);
                                } else if (bl && world.getBlockState(blockPos).isIn(config.baseTag)) {
                                    break;
                                }

                                mutable.move(this.isStalagmite ? Direction.UP : Direction.DOWN);
                            }
                        }
                    }
                }
            }

        }

        boolean generateWind(CustomizableLargeDripstoneConfiguration config) {
            return this.scale >= config.minRadiusForWind && this.bluntness >= (double)config.minBluntnessForWind;
        }
    }

    private static final class WindModifier {
        private final int y;
        @Nullable
        private final Vec3d wind;

        WindModifier(int y, Random random, FloatProvider wind) {
            this.y = y;
            float f = wind.get(random);
            float g = MathHelper.nextBetween(random, 0.0F, 3.1415927F);
            this.wind = new Vec3d(MathHelper.cos(g) * f, 0.0, MathHelper.sin(g) * f);
        }

        private WindModifier() {
            this.y = 0;
            this.wind = null;
        }

        static CustomizableLargeDripstoneFeature.WindModifier create() {
            return new CustomizableLargeDripstoneFeature.WindModifier();
        }

        BlockPos modify(BlockPos pos) {
            if (this.wind == null) {
                return pos;
            } else {
                int i = this.y - pos.getY();
                Vec3d vec3d = this.wind.multiply(i);
                return pos.add(vec3d.x, 0.0, vec3d.z);
            }
        }
    }
}
