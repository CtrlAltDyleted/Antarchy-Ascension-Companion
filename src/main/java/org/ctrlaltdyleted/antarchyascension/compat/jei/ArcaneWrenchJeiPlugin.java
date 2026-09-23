package org.ctrlaltdyleted.antarchyascension.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static org.ctrlaltdyleted.antarchyascension.AntarchyAscension.MODID;

@JeiPlugin
public final class ArcaneWrenchJeiPlugin implements IModPlugin {
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
        if (!ArcaneWrenchCompat.isAvailable()) {
            return;
        }

        ArcaneWrenchCompat.getCreateWrench().ifPresent(wrench ->
                registration.registerSubtypeInterpreter(
                        wrench,
                        SUBTYPE_INTERPRETER));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        if (!ArcaneWrenchCompat.isAvailable()) {
            return;
        }

        IIngredientManager ingredientManager = runtime.getIngredientManager();
        boolean alreadyRegistered = ingredientManager.getAllItemStacks().stream()
                .anyMatch(ArcaneWrenchCompat::isArcaneWrench);

        if (!alreadyRegistered) {
            ArcaneWrenchCompat.createArcaneWrench().ifPresent(stack ->
                    ingredientManager.addIngredientsAtRuntime(
                            VanillaTypes.ITEM_STACK,
                            List.of(stack)));
        }
    }

    private static String getSubtype(ItemStack stack) {
        return ArcaneWrenchCompat.isArcaneWrench(stack)
                ? ARCANE_SUBTYPE
                : NORMAL_SUBTYPE;
    }
}
