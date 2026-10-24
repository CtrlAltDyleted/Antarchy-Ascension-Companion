package org.ctrlaltdyleted.antarchyascension.mixin.compat.visualworkbench;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.ctrlaltdyleted.antarchyascension.compat.visualworkbench.VisualWorkbenchMenuSync;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "fuzs.visualworkbench.world.inventory.VisualCraftingMenu", remap = false)
public abstract class VisualCraftingMenuCloseMixin implements VisualWorkbenchMenuSync {
    @Shadow @Final
    private Container blockEntity;

    @Override
    public void antarchyascension$syncWorkbenchVisuals() {
        // Marks the workbench display for updating
        blockEntity.setChanged();
    }

    @Inject(method = "removed(Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"), remap = false)
    private void antarchyascension$syncFinalWorkbenchState(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide) {
            // Updates the recipe when the menu closes
            CraftingMenuStateAccessor state = (CraftingMenuStateAccessor) this;
            CraftingMenuStateAccessor.antarchyascension$updateResult(
                (AbstractContainerMenu) (Object) this, player.level(), player,
                state.antarchyascension$craftSlots(), state.antarchyascension$resultSlots(), null
            );
            // Keeps the ingredients on the table
            // Marks the workbench display for updating
            blockEntity.setChanged();
        }
    }
}
