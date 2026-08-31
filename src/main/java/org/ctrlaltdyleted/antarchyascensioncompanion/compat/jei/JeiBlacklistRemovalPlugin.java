package org.ctrlaltdyleted.antarchyascensioncompanion.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class JeiBlacklistRemovalPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "antarchy_ascension_companion",
                    "jei_blacklist_removal");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        IIngredientManager ingredientManager = runtime.getIngredientManager();
        IEditModeConfig editModeConfig = runtime.getEditModeConfig();

        List<IIngredientType<?>> ingredientTypes =
                List.copyOf(ingredientManager.getRegisteredIngredientTypes());

        for (IIngredientType<?> ingredientType : ingredientTypes) {
            removeBlacklisted(
                    ingredientManager,
                    editModeConfig,
                    ingredientType);
        }
    }

    private static <T> void removeBlacklisted(
            IIngredientManager ingredientManager,
            IEditModeConfig editModeConfig,
            IIngredientType<T> ingredientType) {

        List<T> blacklisted = new ArrayList<>();

        for (
                ITypedIngredient<T> ingredient :
                ingredientManager.getAllTypedIngredients(ingredientType)
        ) {
            if (editModeConfig.isIngredientHiddenUsingConfigFile(ingredient)) {
                blacklisted.add(ingredient.getIngredient());
            }
        }

        if (!blacklisted.isEmpty()) {
            ingredientManager.removeIngredientsAtRuntime(
                    ingredientType,
                    blacklisted);
        }
    }
}
