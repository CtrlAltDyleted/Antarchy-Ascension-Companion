package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.accessor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface AbstractContainerMenuAccessor {
    @Invoker("addSlot")
    Slot antarchy$addSlot(Slot slot);
}
