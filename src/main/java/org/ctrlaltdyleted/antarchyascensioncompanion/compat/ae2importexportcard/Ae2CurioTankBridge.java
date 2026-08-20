package org.ctrlaltdyleted.antarchyascensioncompanion.compat.ae2importexportcard;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public final class Ae2CurioTankBridge {
    public static final String SLOT_ID = "filling_fueling_tank";

    public static final int FIRST_LOGICAL_SLOT = 40;
    public static final int SECOND_LOGICAL_SLOT = 41;
    public static final int LOGICAL_SIZE = 42;

    private Ae2CurioTankBridge() {
    }

    public static boolean isTankSlot(int slot) {
        return slot == FIRST_LOGICAL_SLOT
                || slot == SECOND_LOGICAL_SLOT;
    }

    public static ItemStack getStack(
            Player player,
            int logicalSlot) {

        if (!isTankSlot(logicalSlot)) {
            return ItemStack.EMPTY;
        }

        ICuriosItemHandler curios =
                player.getCapability(CuriosCapability.INVENTORY);

        if (curios == null) {
            return ItemStack.EMPTY;
        }

        ICurioStacksHandler handler =
                curios.getStacksHandler(SLOT_ID).orElse(null);

        if (handler == null) {
            return ItemStack.EMPTY;
        }

        int index = logicalSlot - FIRST_LOGICAL_SLOT;

        if (index < 0 || index >= handler.getSlots()) {
            return ItemStack.EMPTY;
        }

        return handler.getStacks().getStackInSlot(index);
    }

    public static boolean setStack(
            Player player,
            int logicalSlot,
            ItemStack stack) {

        if (!isTankSlot(logicalSlot)) {
            return false;
        }

        ICuriosItemHandler curios =
                player.getCapability(CuriosCapability.INVENTORY);

        if (curios == null) {
            return false;
        }

        ICurioStacksHandler handler =
                curios.getStacksHandler(SLOT_ID).orElse(null);

        if (handler == null) {
            return false;
        }

        int index = logicalSlot - FIRST_LOGICAL_SLOT;

        if (index < 0 || index >= handler.getSlots()) {
            return false;
        }

        handler.getStacks().setStackInSlot(index, stack);
        return true;
    }
}
