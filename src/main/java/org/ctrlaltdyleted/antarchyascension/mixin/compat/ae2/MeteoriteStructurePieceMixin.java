package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2;

import appeng.worldgen.meteorite.MeteoritePlacer;
import appeng.worldgen.meteorite.PlacedMeteoriteSettings;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.ctrlaltdyleted.antarchyascension.compat.ae2.LunarMeteoriteDebris;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "appeng.worldgen.meteorite.MeteoriteStructurePiece", remap = false)
public abstract class MeteoriteStructurePieceMixin {
    @Redirect(
            method = "postProcess",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/worldgen/meteorite/MeteoritePlacer;place(Lnet/minecraft/world/level/LevelAccessor;Lappeng/worldgen/meteorite/PlacedMeteoriteSettings;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/util/RandomSource;)V"))
    private void antarchyascension$placeDryLunarMeteorite(
            LevelAccessor level, PlacedMeteoriteSettings settings, BoundingBox bounds, RandomSource random) {
        MeteoritePlacer.place(level, LunarMeteoriteDebris.withoutCraterFluids(level, settings), bounds, random);
    }
}
