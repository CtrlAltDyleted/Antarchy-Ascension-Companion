package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.integratedmekanism;

import java.lang.reflect.Array;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(
        targets = "org.cyclops.integratedmekanism.capability.recipehandler.MekanismCapabilityLoader",
        remap = false
)
public abstract class IntegratedMekanismFactoryTierMixin {
    @Coerce
    @Redirect(
            method = "addRecipeHandler",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tier/FactoryTier;values()[Lmekanism/common/tier/FactoryTier;",
                    remap = false
            ),
            require = 0,
            remap = false
    )
    private static Object[] antarchyascensioncompanion$useBaseFactoryTiers() {
        try {
            Class<?> tierClass = Class.forName("mekanism.common.tier.FactoryTier");
            Object[] values = (Object[]) tierClass.getMethod("values").invoke(null);
            Object[] filtered = (Object[]) Array.newInstance(tierClass, values.length);

            int count = 0;

            for (Object value : values) {
                if (!(value instanceof Enum<?> tier)) {
                    continue;
                }

                switch (tier.name()) {
                    case "BASIC", "ADVANCED", "ELITE", "ULTIMATE" ->
                            filtered[count++] = value;
                    default -> {
                    }
                }
            }

            if (count != 4) {
                throw new IllegalStateException(
                        "Expected 4 base Mekanism factory tiers, found " + count
                );
            }

            Object[] result = (Object[]) Array.newInstance(tierClass, count);
            System.arraycopy(filtered, 0, result, 0, count);

            return result;
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Could not resolve Mekanism factory tiers",
                    exception
            );
        }
    }
}
