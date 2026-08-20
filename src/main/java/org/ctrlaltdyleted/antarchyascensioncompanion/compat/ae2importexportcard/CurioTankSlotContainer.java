package org.ctrlaltdyleted.antarchyascensioncompanion.compat.ae2importexportcard;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CurioTankSlotContainer implements Container {
    private final Player player;

    public CurioTankSlotContainer(Player player) {
        this.player = player;
    }

    @Override
    public int getContainerSize() {
        return Ae2CurioTankBridge.LOGICAL_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return getItem(Ae2CurioTankBridge.FIRST_LOGICAL_SLOT).isEmpty()
                && getItem(Ae2CurioTankBridge.SECOND_LOGICAL_SLOT).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return Ae2CurioTankBridge.getStack(player, slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return player == this.player;
    }

    @Override
    public void clearContent() {
    }
}
