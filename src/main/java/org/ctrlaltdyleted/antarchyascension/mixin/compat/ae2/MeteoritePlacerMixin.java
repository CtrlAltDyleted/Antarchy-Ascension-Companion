package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2;

import org.ctrlaltdyleted.antarchyascension.compat.ae2.LunarMeteoriteDebris;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "appeng.worldgen.meteorite.MeteoritePlacer", remap = false)
public abstract class MeteoritePlacerMixin {
    // Decay can copy the block directly above a crater cell without using MeteoriteBlockPutter.
    @Redirect(
            method = "decay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean antarchyascension$adaptCopiedLunarDebris(LevelAccessor level, BlockPos pos, BlockState state, int flags) {
        return LunarMeteoriteDebris.place(level, pos, state, flags);
    }
}
