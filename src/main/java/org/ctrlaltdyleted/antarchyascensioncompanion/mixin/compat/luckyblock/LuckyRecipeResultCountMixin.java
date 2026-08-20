package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.luckyblock;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
    targets = "dev.creoii.luckyblock.recipe.LuckyRecipe",
    remap = false
)
public abstract class LuckyRecipeResultCountMixin {
    @Inject(
        method = "craft(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    )
    private void antarchy$forceSingleLuckModifierResult(
        CraftingInput input,
        HolderLookup.Provider registries,
        CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack result = cir.getReturnValue();

        if (
            result == null ||
            result.isEmpty() ||
            result.getCount() == 1
        ) {
            return;
        }

        ItemStack fixed = result.copy();
        fixed.setCount(1);
        cir.setReturnValue(fixed);
    }
}
