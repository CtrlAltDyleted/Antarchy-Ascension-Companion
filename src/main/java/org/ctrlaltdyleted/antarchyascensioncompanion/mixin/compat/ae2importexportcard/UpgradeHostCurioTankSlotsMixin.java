package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.ae2importexportcard;

import org.ctrlaltdyleted.antarchyascensioncompanion.compat.ae2importexportcard.Ae2CurioTankBridge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "com.ultramega.ae2importexportcard.item.UpgradeHost",
        remap = false)
public abstract class UpgradeHostCurioTankSlotsMixin {
    @Inject(
            method = "normalizeSelectedInventorySlots",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private static void antarchy$normalize(
            int[] input,
            CallbackInfoReturnable<int[]> cir) {

        if (input != null
                && input.length == Ae2CurioTankBridge.LOGICAL_SIZE) {
            cir.setReturnValue(input);
            return;
        }

        cir.setReturnValue(antarchy$expand(input));
    }

    @Inject(
            method = "getSelectedInventorySlots",
            at = @At("RETURN"),
            cancellable = true,
            remap = false)
    private void antarchy$expandReturned(
            CallbackInfoReturnable<int[]> cir) {

        int[] value = cir.getReturnValue();

        if (value == null
                || value.length != Ae2CurioTankBridge.LOGICAL_SIZE) {
            cir.setReturnValue(antarchy$expand(value));
        }
    }

    @Unique
    private static int[] antarchy$expand(int[] input) {
        int[] result =
                new int[Ae2CurioTankBridge.LOGICAL_SIZE];

        if (input != null) {
            System.arraycopy(
                    input,
                    0,
                    result,
                    0,
                    Math.min(input.length, result.length));
        }

        return result;
    }
}
