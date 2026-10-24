package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2;

import org.ctrlaltdyleted.antarchyascension.compat.ae2.LunarMeteoriteDebris;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import appeng.worldgen.meteorite.MeteoritePlacer;
import appeng.worldgen.meteorite.PlacedMeteoriteSettings;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

// Changes crater debris on the Moon
@Mixin(targets = "appeng.worldgen.meteorite.MeteoriteBlockPutter", remap = false)
abstract class MeteoriteBlockPutterMixin {
    @Redirect(
            method = "put",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean antarchyascension$adaptLunarDebris(LevelAccessor level, BlockPos pos, BlockState state, int flags) {
        // Applies lunar debris rules to block placement
        return LunarMeteoriteDebris.place(level, pos, state, flags);
    }
}

// Changes blocks copied during crater decay
@Mixin(targets = "appeng.worldgen.meteorite.MeteoritePlacer", remap = false)
abstract class MeteoritePlacerMixin {
    // Handles blocks copied during crater decay
    @Redirect(
            method = "decay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean antarchyascension$adaptCopiedLunarDebris(LevelAccessor level, BlockPos pos, BlockState state, int flags) {
        // Applies lunar debris rules to copied blocks
        return LunarMeteoriteDebris.place(level, pos, state, flags);
    }
}

// Removes fluids from lunar craters
@Mixin(targets = "appeng.worldgen.meteorite.MeteoriteStructurePiece", remap = false)
abstract class MeteoriteStructurePieceMixin {
    @Redirect(
            method = "postProcess",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/worldgen/meteorite/MeteoritePlacer;place(Lnet/minecraft/world/level/LevelAccessor;Lappeng/worldgen/meteorite/PlacedMeteoriteSettings;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;)V"))
    private void antarchyascension$placeDryLunarMeteorite(
            LevelAccessor level, PlacedMeteoriteSettings settings, BoundingBox bounds, RandomSource random) {
        // Removes crater fluids before placing lunar meteorites
        MeteoritePlacer.place(level, LunarMeteoriteDebris.withoutCraterFluids(level, settings), bounds, random);
    }
}
