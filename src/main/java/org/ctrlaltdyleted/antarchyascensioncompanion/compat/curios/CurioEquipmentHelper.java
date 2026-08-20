package org.ctrlaltdyleted.antarchyascensioncompanion.compat.curios;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public final class CurioEquipmentHelper {
    public static final String JETPACK_SLOT = "jetpack";
    public static final String TANK_SLOT = "filling_fueling_tank";
    public static final String GOGGLES_SLOT = "engineers_goggles";
    public static final String TERMINAL_SLOT = "wireless_terminal";

    public static final TagKey<Item> CREATE_SA_JETPACKS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("create_sa", "jetpack"));

    public static final TagKey<Item> CREATE_SA_FILLABLE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("create_sa", "fillable"));

    public static final TagKey<Item> CREATE_SA_FUELABLE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("create_sa", "fuelable"));

    private CurioEquipmentHelper() {
    }

    public static ItemStack findFirstInSlot(
            LivingEntity entity,
            String slotId,
            Predicate<ItemStack> predicate) {

        ICuriosItemHandler curios =
                entity.getCapability(CuriosCapability.INVENTORY);

        if (curios == null) {
            return ItemStack.EMPTY;
        }

        ICurioStacksHandler handler =
                curios.getStacksHandler(slotId).orElse(null);

        if (handler == null) {
            return ItemStack.EMPTY;
        }

        for (int index = 0; index < handler.getSlots(); index++) {
            ItemStack stack = handler.getStacks().getStackInSlot(index);

            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack findFirstRenderedInSlot(
            LivingEntity entity,
            String slotId,
            Predicate<ItemStack> predicate) {

        ICuriosItemHandler curios =
                entity.getCapability(CuriosCapability.INVENTORY);

        if (curios == null) {
            return ItemStack.EMPTY;
        }

        ICurioStacksHandler handler =
                curios.getStacksHandler(slotId).orElse(null);

        if (handler == null) {
            return ItemStack.EMPTY;
        }

        var renderStates = handler.getRenders();

        for (int index = 0; index < handler.getSlots(); index++) {
            ItemStack stack =
                    handler.getStacks().getStackInSlot(index);

            boolean shouldRender =
                    renderStates.size() > index
                            && renderStates.get(index);

            if (
                    shouldRender
                            && !stack.isEmpty()
                            && predicate.test(stack)
            ) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public static boolean isExactStackInSlot(
            LivingEntity entity,
            String slotId,
            ItemStack wanted) {

        if (wanted.isEmpty()) {
            return false;
        }

        ICuriosItemHandler curios =
                entity.getCapability(CuriosCapability.INVENTORY);

        if (curios == null) {
            return false;
        }

        ICurioStacksHandler handler =
                curios.getStacksHandler(slotId).orElse(null);

        if (handler == null) {
            return false;
        }

        for (int index = 0; index < handler.getSlots(); index++) {
            if (handler.getStacks().getStackInSlot(index) == wanted) {
                return true;
            }
        }

        return false;
    }

    public static void forEachStackInSlot(
            LivingEntity entity,
            String slotId,
            Consumer<ItemStack> action) {

        ICuriosItemHandler curios =
                entity.getCapability(CuriosCapability.INVENTORY);

        if (curios == null) {
            return;
        }

        ICurioStacksHandler handler =
                curios.getStacksHandler(slotId).orElse(null);

        if (handler == null) {
            return;
        }

        for (int index = 0; index < handler.getSlots(); index++) {
            ItemStack stack = handler.getStacks().getStackInSlot(index);

            if (!stack.isEmpty()) {
                action.accept(stack);
            }
        }
    }

    public static ItemStack resolveCreateSaJetpack(
            LivingEntity entity,
            EquipmentSlot requestedSlot) {

        ItemStack normalStack = entity.getItemBySlot(requestedSlot);

        if (requestedSlot != EquipmentSlot.CHEST) {
            return normalStack;
        }

        if (normalStack.is(CREATE_SA_JETPACKS)) {
            return normalStack;
        }

        ItemStack curioStack = findFirstInSlot(
                entity,
                JETPACK_SLOT,
                stack -> stack.is(CREATE_SA_JETPACKS));

        return curioStack.isEmpty() ? normalStack : curioStack;
    }

    public static ItemStack resolveCreateSaTankTarget(
            LivingEntity entity,
            EquipmentSlot requestedSlot) {

        ItemStack normalStack = entity.getItemBySlot(requestedSlot);

        if (requestedSlot != EquipmentSlot.CHEST) {
            return normalStack;
        }

        if (isCreateSaTankTarget(normalStack)) {
            return normalStack;
        }

        ItemStack curioStack = findFirstInSlot(
                entity,
                JETPACK_SLOT,
                CurioEquipmentHelper::isCreateSaTankTarget);

        return curioStack.isEmpty() ? normalStack : curioStack;
    }

    private static boolean isCreateSaTankTarget(ItemStack stack) {
        return stack.is(CREATE_SA_FILLABLE) || stack.is(CREATE_SA_FUELABLE);
    }
}
