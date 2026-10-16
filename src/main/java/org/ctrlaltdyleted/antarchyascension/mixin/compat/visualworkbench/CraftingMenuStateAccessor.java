package org.ctrlaltdyleted.antarchyascension.mixin.compat.visualworkbench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CraftingMenu.class)
public interface CraftingMenuStateAccessor {
    @Accessor("craftSlots")
    CraftingContainer antarchyascension$craftSlots();

    @Accessor("resultSlots")
    ResultContainer antarchyascension$resultSlots();

    @Invoker("slotChangedCraftingGrid")
    static void antarchyascension$updateResult(AbstractContainerMenu menu, Level level,
            Player player, CraftingContainer grid, ResultContainer result,
            RecipeHolder<CraftingRecipe> recipe) {
        throw new AssertionError("Mixin invoker was not applied");
    }
}
