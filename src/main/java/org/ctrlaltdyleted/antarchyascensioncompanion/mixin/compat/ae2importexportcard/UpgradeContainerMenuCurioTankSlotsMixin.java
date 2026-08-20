package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.ae2importexportcard;

import java.lang.reflect.Constructor;

import org.ctrlaltdyleted.antarchyascensioncompanion.compat.ae2importexportcard.Ae2CurioTankBridge;
import org.ctrlaltdyleted.antarchyascensioncompanion.compat.ae2importexportcard.CurioTankSlotContainer;
import org.ctrlaltdyleted.antarchyascensioncompanion.mixin.accessor.AbstractContainerMenuAccessor;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
        targets = "com.ultramega.ae2importexportcard.container.UpgradeContainerMenu",
        remap = false)
public abstract class UpgradeContainerMenuCurioTankSlotsMixin {
    @Unique
    private static final String ANTARCHY_CARD_PLAYER_SLOT =
            "com.ultramega.ae2importexportcard.container.CardPlayerSlot";

    @Inject(
            method = "createCardPlayerInventorySlots",
            at = @At("TAIL"),
            remap = false)
    private void antarchy$addTankSlots(
            Inventory inventory,
            CallbackInfo ci) {

        CurioTankSlotContainer container =
                new CurioTankSlotContainer(inventory.player);

        AbstractContainerMenuAccessor menu =
                (AbstractContainerMenuAccessor) (Object) this;

        menu.antarchy$addSlot(
                antarchy$newCardSlot(
                        container,
                        Ae2CurioTankBridge.FIRST_LOGICAL_SLOT,
                        2,
                        34));

        menu.antarchy$addSlot(
                antarchy$newCardSlot(
                        container,
                        Ae2CurioTankBridge.SECOND_LOGICAL_SLOT,
                        2,
                        52));
    }

    @Unique
    private static Slot antarchy$newCardSlot(
            Container container,
            int index,
            int x,
            int y) {

        try {
            Class<?> type =
                    Class.forName(ANTARCHY_CARD_PLAYER_SLOT);

            Constructor<?> constructor =
                    type.getConstructor(
                            Container.class,
                            int.class,
                            int.class,
                            int.class);

            return (Slot) constructor.newInstance(
                    container,
                    index,
                    x,
                    y);
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Could not create AE2 Import Export Card tank slot",
                    e);
        }
    }
}
