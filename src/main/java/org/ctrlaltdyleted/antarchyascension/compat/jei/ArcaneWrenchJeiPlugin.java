package org.ctrlaltdyleted.antarchyascension.compat.jei;

import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static org.ctrlaltdyleted.antarchyascension.AntarchyAscension.MODID;

@JeiPlugin
public final class ArcaneWrenchJeiPlugin implements IModPlugin {
    private static final String ARS_TECHNICA_MOD_ID = "ars_technica";
    private static final String CREATE_MOD_ID = "create";
    private static final ResourceLocation CREATE_WRENCH_ID =
            ResourceLocation.fromNamespaceAndPath(CREATE_MOD_ID, "wrench");
    private static final ResourceLocation ARCANE_WRENCH_COMPONENT_ID =
            ResourceLocation.fromNamespaceAndPath(ARS_TECHNICA_MOD_ID, "runic_wrench");

    private static final String NORMAL_SUBTYPE = "normal";
    private static final String ARCANE_SUBTYPE = "arcane";
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(MODID, "arcane_wrench");
    private static final ISubtypeInterpreter<ItemStack> SUBTYPE_INTERPRETER =
            new ISubtypeInterpreter<>() {
                @Override
                public Object getSubtypeData(ItemStack stack, UidContext context) {
                    return getSubtype(stack);
                }

                @Override
                public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
                    return getSubtype(stack);
                }
            };

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(getCreateWrench(), SUBTYPE_INTERPRETER);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        IIngredientManager ingredientManager = runtime.getIngredientManager();
        // Checks whether JEI already has the Arcane Wrench
        boolean alreadyRegistered = ingredientManager.getAllItemStacks().stream()
                .anyMatch(ArcaneWrenchJeiPlugin::isArcaneWrench);

        if (!alreadyRegistered) {
            ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(createArcaneWrench()));
        }
    }

    private static String getSubtype(ItemStack stack) {
        return isArcaneWrench(stack)
                ? ARCANE_SUBTYPE
                : NORMAL_SUBTYPE;
    }

    private static Item getCreateWrench() {
        return BuiltInRegistries.ITEM.getOptional(CREATE_WRENCH_ID)
                .orElseThrow(() -> new IllegalStateException("Missing Create wrench item"));
    }

    private static boolean isArcaneWrench(ItemStack stack) {
        if (!isCreateWrench(stack)) {
            return false;
        }

        return Boolean.TRUE.equals(stack.get(getArcaneWrenchComponent()));
    }

    private static boolean isCreateWrench(ItemStack stack) {
        return !stack.isEmpty() && stack.is(getCreateWrench());
    }

    private static ItemStack createArcaneWrench() {
        ItemStack stack = new ItemStack(getCreateWrench());
        // Marks the display wrench as runic
        stack.set(getArcaneWrenchComponent(), Boolean.TRUE);
        return stack;
    }

    @SuppressWarnings("unchecked")
    private static DataComponentType<Boolean> getArcaneWrenchComponent() {
        return (DataComponentType<Boolean>) BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(ARCANE_WRENCH_COMPONENT_ID)
                .orElseThrow(() -> new IllegalStateException("Missing Ars Technica runic wrench component"));
    }
}
