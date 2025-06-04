package net.thebrewingminer.atmosphericnether.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AquiferSampler.Impl.class)
public class AquiferMixin {

	@Unique
	private int yLevel;

	@Inject(method = "apply", at = @At("HEAD"))
	private void cacheYLevel(DensityFunction.NoisePos pos, double density, CallbackInfoReturnable<BlockState> cir) {
		this.yLevel = pos.blockY();	// Save the y position from DensityFunction.NoisePos to target the comparison (fluidLevel.getBlockState(j).isOf(Blocks.LAVA))
	}

	@Redirect(
		method = "apply(Lnet/minecraft/world/gen/densityfunction/DensityFunction$NoisePos;D)Lnet/minecraft/block/BlockState;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
			ordinal = 0
    	)
    )
	private boolean reEnableAquiferOnLava(BlockState state, Block block){
		final int vanillaAquiferDisabledBelow = -54;							// Vanilla fills all non-solid blocks underneath this y-level with lava.
		if (yLevel >= vanillaAquiferDisabledBelow && block == Blocks.LAVA) {	// If the position is above y = -54 and the fluid there is lava...
			return false;														// Tell the game it isn't lava, so it continues to aquifer logic.
		}
		return state.isOf(block);		// If logic falls through, it wasn't lava and/or was below y = -54. Check the fluid as usual.
	}
}
