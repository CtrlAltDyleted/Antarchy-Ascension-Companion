package org.ctrlaltdyleted.antarchyascension.mixin.compat.neoforge;

import java.util.function.Supplier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SimpleTier.class, remap = false)
public abstract class SimpleTierNullRepairIngredientMixin {
    @Shadow
    @Final
    private Supplier<Ingredient> repairIngredient;

    @Inject(method = "getRepairIngredient", at = @At("HEAD"), cancellable = true)
    private void antarchyascension$nullRepairIngredient(CallbackInfoReturnable<Ingredient> cir) {
        if (this.repairIngredient == null) {
            cir.setReturnValue(Ingredient.EMPTY);
        }
    }
}
