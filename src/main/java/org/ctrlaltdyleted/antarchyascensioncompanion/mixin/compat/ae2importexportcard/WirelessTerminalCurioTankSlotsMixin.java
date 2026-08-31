package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.ae2importexportcard;

import org.ctrlaltdyleted.antarchyascensioncompanion.compat.ae2importexportcard.Ae2CurioTankBridge;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
        targets = "appeng.items.tools.powered.WirelessTerminalItem",
        priority = 1100,
        remap = false)
public abstract class WirelessTerminalCurioTankSlotsMixin {
    @Unique
    private static final ThreadLocal<Integer> antarchyascensioncompanion$logicalSlot =
            ThreadLocal.withInitial(() -> -1);

    @Dynamic("Added by AE2 Import Export Card")
    @Inject(
            method = "ae2importExportCard$tickUpgradeCard",
            at = @At("HEAD"),
            require = 0,
            remap = false)
    private void antarchyascensioncompanion$begin(CallbackInfo ci) {
        antarchyascensioncompanion$logicalSlot.remove();
    }

    @Dynamic("Added by AE2 Import Export Card")
    @Redirect(
            method = "ae2importExportCard$tickUpgradeCard",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;getItem(I)Lnet/minecraft/world/item/ItemStack;"),
            require = 0,
            remap = false)
    private ItemStack antarchyascensioncompanion$getStack(
            Inventory inventory,
            int slot) {

        antarchyascensioncompanion$logicalSlot.set(slot);

        if (Ae2CurioTankBridge.isTankSlot(slot)) {
            return Ae2CurioTankBridge.getStack(
                    inventory.player,
                    slot);
        }

        return inventory.getItem(slot);
    }

    @Dynamic("Added by AE2 Import Export Card")
    @Redirect(
            method = {
                    "ae2importExportCard$importFluidFromItem",
                    "ae2importExportCard$exportFluidToPlayerSlot"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;setItem(ILnet/minecraft/world/item/ItemStack;)V"),
            require = 0,
            remap = false)
    private void antarchyascensioncompanion$writeBack(
            Inventory inventory,
            int slot,
            ItemStack stack) {

        if (Ae2CurioTankBridge.isTankSlot(slot)) {
            Ae2CurioTankBridge.setStack(
                    inventory.player,
                    slot,
                    stack);
            return;
        }

        inventory.setItem(slot, stack);
    }

    @Dynamic("Added by AE2 Import Export Card")
    @Inject(
            method = "ae2importExportCard$importItem",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false)
    private void antarchyascensioncompanion$blockTankImport(
            CallbackInfo ci) {

        if (Ae2CurioTankBridge.isTankSlot(
                antarchyascensioncompanion$logicalSlot.get())) {
            ci.cancel();
        }
    }

    @Dynamic("Added by AE2 Import Export Card")
    @Inject(
            method = "ae2importExportCard$exportItemToPlayerSlot",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false)
    private void antarchyascensioncompanion$blockTankExport(
            CallbackInfo ci) {

        if (Ae2CurioTankBridge.isTankSlot(
                antarchyascensioncompanion$logicalSlot.get())) {
            ci.cancel();
        }
    }

    @Dynamic("Added by AE2 Import Export Card")
    @Inject(
            method = "ae2importExportCard$tickUpgradeCard",
            at = @At("RETURN"),
            require = 0,
            remap = false)
    private void antarchyascensioncompanion$end(CallbackInfo ci) {
        antarchyascensioncompanion$logicalSlot.remove();
    }
}
