package org.ctrlaltdyleted.antarchyascension.mixin.compat.curios;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;

// Fixes dedicated-slot shift-click handling in Curios 9.5.1
@Mixin(CuriosContainer.class)
public abstract class CuriosSpecificSlotQuickMoveMixin {
    @WrapOperation(method = "quickMoveStack", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/entity/player/Player;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"),
            require = 1, allow = 1)
    private EquipmentSlot antarchyascension$curiosBeforeArmor(Player player, ItemStack copy,
            Operation<EquipmentSlot> original, Player methodPlayer, int sourceIndex) {
        CuriosContainer menu = (CuriosContainer) (Object) this;
        // Checks Curios before armor
        if (menu.slots.get(sourceIndex) instanceof CurioSlot
                || (sourceIndex >= 9 && sourceIndex < 45
                && !CuriosApi.getItemStackSlots(copy, player.level()).isEmpty())) {
            // Allows Curios handling before armor placement
            return EquipmentSlot.MAINHAND;
        }
        return original.call(player, copy);
    }

    @WrapOperation(
            method = "quickMoveStack",
            at = @At(value = "INVOKE", target =
                    "Ltop/theillusivec4/curios/common/inventory/container/CuriosContainer;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target =
                            "Ltop/theillusivec4/curios/api/CuriosApi;getItemStackSlots(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Ljava/util/Map;"),
                    to = @At(value = "INVOKE", target =
                            "Ltop/theillusivec4/curios/common/inventory/container/CuriosContainer;findAvailableSlot(Lnet/minecraft/world/item/ItemStack;)I")),
            require = 1, allow = 1)
    private boolean antarchyascension$specificBeforeGeneric(CuriosContainer menu, ItemStack stack,
            int start, int end, boolean reverse, Operation<Boolean> original) {
        // Curios slots start at index 46
        if (start != 46 || end != menu.slots.size() || reverse) {
            return original.call(menu, stack, start, end, reverse);
        }
        List<Integer> specific = new ArrayList<>();
        List<Integer> generic = new ArrayList<>();
        for (int i = start; i < end; i++) {
            if (menu.slots.get(i) instanceof CurioSlot slot && slot.mayPlace(stack)) {
                (SlotTypePreset.CURIO.getIdentifier().equals(slot.getIdentifier()) ? generic : specific).add(i);
            }
        }
        boolean moved = false;
        // Tries dedicated slots before generic slots
        for (List<Integer> group : List.of(specific, generic)) {
            for (int index : group) {
                if (stack.isEmpty()) return moved;
                if (menu.slots.get(index).mayPlace(stack)) {
                    moved |= original.call(menu, stack, index, index + 1, false);
                }
            }
        }
        return moved;
    }
}
