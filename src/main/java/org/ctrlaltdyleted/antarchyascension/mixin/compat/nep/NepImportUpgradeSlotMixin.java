package org.ctrlaltdyleted.antarchyascension.mixin.compat.nep;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AEBaseMenu.class)
public abstract class NepImportUpgradeSlotMixin {
    @Inject(
        method = "addSlot(Lnet/minecraft/world/inventory/Slot;Lappeng/menu/SlotSemantic;)Lnet/minecraft/world/inventory/Slot;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void antarchyascension$excludeNepImportSlot(
        Slot slot, SlotSemantic semantic, CallbackInfoReturnable<Slot> cir
    ) {
        // Only skips NEP's Pattern Provider import slot
        if ((Object) this instanceof PatternProviderMenu && semantic.id().equals("nep:import_upgrade")) {
            // NEP keeps this slot reference without adding it to the menu
            cir.setReturnValue(slot);
        }
    }
}
