package org.ctrlaltdyleted.antarchyascension.mixin.compat.visualworkbench;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.ctrlaltdyleted.antarchyascension.compat.visualworkbench.VisualWorkbenchMenuSync;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "fuzs.visualworkbench.world.inventory.VisualCraftingMenu", remap = false)
public abstract class VisualCraftingMenuCloseMixin implements VisualWorkbenchMenuSync {
    @Shadow @Final
    private Container blockEntity;

    @Override
    public void antarchyascension$syncWorkbenchVisuals() {
        blockEntity.setChanged();
    }

    @Inject(method = "removed(Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"), remap = false)
    private void antarchyascension$syncFinalWorkbenchState(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide) {
            // FastWorkbench can defer the final recipe update past menu closure.
            CraftingMenuStateAccessor state = (CraftingMenuStateAccessor) this;
            CraftingMenuStateAccessor.antarchyascension$updateResult(
                (AbstractContainerMenu) (Object) this, player.level(), player,
                state.antarchyascension$craftSlots(), state.antarchyascension$resultSlots(), null
            );
            // Send the final inventory/result state without removing retained ingredients.
            blockEntity.setChanged();
        }
    }
}
