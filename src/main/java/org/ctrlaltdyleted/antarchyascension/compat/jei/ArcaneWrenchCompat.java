package org.ctrlaltdyleted.antarchyascension.compat.jei;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

import java.util.Optional;

public final class ArcaneWrenchCompat {
    private static final String ARS_TECHNICA_MOD_ID = "ars_technica";
    private static final String CREATE_MOD_ID = "create";
    private static final ResourceLocation CREATE_WRENCH_ID =
            ResourceLocation.fromNamespaceAndPath(CREATE_MOD_ID, "wrench");
    private static final ResourceLocation ARCANE_WRENCH_COMPONENT_ID =
            ResourceLocation.fromNamespaceAndPath(ARS_TECHNICA_MOD_ID, "runic_wrench");

    private ArcaneWrenchCompat() {
    }

    public static boolean isAvailable() {
        return ModList.get().isLoaded(ARS_TECHNICA_MOD_ID)
                && ModList.get().isLoaded(CREATE_MOD_ID)
                && getCreateWrench().isPresent()
                && getArcaneWrenchComponent().isPresent();
    }

    public static Optional<Item> getCreateWrench() {
        return BuiltInRegistries.ITEM.getOptional(CREATE_WRENCH_ID)
                .filter(item -> item != Items.AIR);
    }

    public static boolean isArcaneWrench(ItemStack stack) {
        if (!isCreateWrench(stack)) {
            return false;
        }

        return getArcaneWrenchComponent()
                .map(component -> Boolean.TRUE.equals(stack.get(component)))
                .orElse(false);
    }

    public static boolean isCreateWrench(ItemStack stack) {
        return !stack.isEmpty() && stack.is(getCreateWrench().orElse(Items.AIR));
    }

    public static Optional<ItemStack> createArcaneWrench() {
        Optional<Item> wrench = getCreateWrench();
        Optional<DataComponentType<Boolean>> component = getArcaneWrenchComponent();
        if (wrench.isEmpty() || component.isEmpty()) {
            return Optional.empty();
        }

        ItemStack stack = new ItemStack(wrench.get());
        stack.set(component.get(), Boolean.TRUE);
        return Optional.of(stack);
    }

    @SuppressWarnings("unchecked")
    private static Optional<DataComponentType<Boolean>> getArcaneWrenchComponent() {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(ARCANE_WRENCH_COMPONENT_ID)
                .map(component -> (DataComponentType<Boolean>) component);
    }
}
