package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.biolith;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeSource.class)
public interface BiolithDimensionTypeAccessor {

    @Dynamic("Added to BiomeSource by Biolith MixinBiomeSource")
    @Accessor(value = "biolith$dimensionType", remap = false)
    ResourceKey<DimensionType> antarchyascensioncompanion$getBiolithDimensionType();
}
