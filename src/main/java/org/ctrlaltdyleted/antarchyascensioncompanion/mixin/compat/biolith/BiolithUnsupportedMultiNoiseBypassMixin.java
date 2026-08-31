package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.biolith;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 901)
public abstract class BiolithUnsupportedMultiNoiseBypassMixin {

    @Shadow
    protected abstract Climate.ParameterList<Holder<Biome>> parameters();

    @Inject(
        method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
        at = @At("HEAD"),
        cancellable = true,
        order = 900
    )
    private void antarchyascensioncompanion$bypassUnsupportedBiolithLookup(
        int x,
        int y,
        int z,
        Climate.Sampler sampler,
        CallbackInfoReturnable<Holder<Biome>> cir
    ) {
        ResourceKey<DimensionType> dimensionType =
            ((BiolithDimensionTypeAccessor) (Object) this)
                .antarchyascensioncompanion$getBiolithDimensionType();

        if (BuiltinDimensionTypes.OVERWORLD.equals(dimensionType)
            || BuiltinDimensionTypes.NETHER.equals(dimensionType)) {
            return;
        }

        cir.setReturnValue(
            this.parameters().findValueBruteForce(
                sampler.sample(x, y, z)
            )
        );
    }
}
