package org.ctrlaltdyleted.antarchyascension.compat.jei;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IEditModeConfig;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

import static org.ctrlaltdyleted.antarchyascension.AntarchyAscension.MODID;

@JeiPlugin
public final class JeiBlacklistRemovalPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MODID,
                    "jei_blacklist_removal");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        IIngredientManager ingredientManager = runtime.getIngredientManager();
        apply(ingredientManager);
        applyRecipes(runtime.getRecipeManager());
        IEditModeConfig editModeConfig = runtime.getEditModeConfig();

        // Copies the ingredient types before removing entries
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

        // Collects hidden entries before removing them
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

    private static void apply(IIngredientManager manager) {
        try {
            JsonObject config = readFilters();
            Set<ResourceLocation> items = ids(config.getAsJsonArray("items"));
            Set<ResourceLocation> fluids = ids(config.getAsJsonArray("fluids"));
            Set<ResourceLocation> tanks = ids(config.getAsJsonArray("empty_tanks"));
            for (IIngredientType<?> type : List.copyOf(manager.getRegisteredIngredientTypes())) {
                filter(manager, type, items, fluids, tanks);
            }
            // Adds the empty tank entries back
            List<ItemStack> defaults = new ArrayList<>();
            for (ResourceLocation id : tanks) {
                if (BuiltInRegistries.ITEM.containsKey(id) && !items.contains(id)) {
                    ItemStack stack = BuiltInRegistries.ITEM.get(id).getDefaultInstance();
                    if (!stack.isEmpty()) defaults.add(stack);
                }
            }
            manager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, defaults);
        } catch (IOException | RuntimeException exception) {
            AntarchyAscension.LOGGER.error("Could not apply permanent JEI filters", exception);
        }
    }

    private static void applyRecipes(IRecipeManager manager) {
        try {
            JsonArray filters = readFilters().getAsJsonArray("recipes");
            if (filters == null) return;
            for (var filter : filters) {
                JsonObject entry = filter.getAsJsonObject();
                ResourceLocation category = ResourceLocation.parse(entry.get("category").getAsString());
                ResourceLocation recipe = ResourceLocation.parse(entry.get("id").getAsString());
                manager.getRecipeType(category).ifPresent(type -> hideRecipe(manager, type, recipe));
            }
        } catch (IOException | RuntimeException exception) {
            AntarchyAscension.LOGGER.error("Could not apply permanent JEI recipe filters", exception);
        }
    }

    private static JsonObject readFilters() throws IOException {
        ResourceLocation resource = ResourceLocation.fromNamespaceAndPath(MODID, "jei_filters.json");
        try (var reader = new InputStreamReader(
                Minecraft.getInstance().getResourceManager().open(resource), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static <T> void hideRecipe(IRecipeManager manager, RecipeType<T> type, ResourceLocation id) {
        // Hides the matching JEI recipe
        List<T> hidden = manager.createRecipeLookup(type).includeHidden().get()
                .filter(recipe -> recipe instanceof RecipeHolder<?> holder && holder.id().equals(id)).toList();
        if (!hidden.isEmpty()) manager.hideRecipes(type, hidden);
    }

    private static Set<ResourceLocation> ids(JsonArray values) {
        Set<ResourceLocation> result = new HashSet<>();
        for (var value : values) result.add(ResourceLocation.parse(value.getAsString()));
        return result;
    }

    private static <T> void filter(IIngredientManager manager, IIngredientType<T> type,
            Set<ResourceLocation> items, Set<ResourceLocation> fluids, Set<ResourceLocation> tanks) {
        List<T> removed = new ArrayList<>();
        for (var typed : manager.getAllTypedIngredients(type)) {
            T ingredient = typed.getIngredient();
            if (ingredient instanceof ItemStack stack) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (items.contains(id) || tanks.contains(id)) removed.add(ingredient);
            } else if (ingredient instanceof FluidStack stack
                    && fluids.contains(BuiltInRegistries.FLUID.getKey(stack.getFluid()))) {
                removed.add(ingredient);
            }
        }
        if (!removed.isEmpty()) manager.removeIngredientsAtRuntime(type, removed);
    }
}
