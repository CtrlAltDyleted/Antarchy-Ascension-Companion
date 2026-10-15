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
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

/** Applies pack policy to JEI ingredients, including tank variants. */
final class PermanentJeiFilters {
    private PermanentJeiFilters() {}

    static void apply(IIngredientManager manager) {
        ResourceLocation resource = ResourceLocation.fromNamespaceAndPath(
                AntarchyAscension.MODID, "jei_filters.json");
        try (var reader = new InputStreamReader(
                Minecraft.getInstance().getResourceManager().open(resource), StandardCharsets.UTF_8)) {
            JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();
            Set<ResourceLocation> items = ids(config.getAsJsonArray("items"));
            Set<ResourceLocation> fluids = ids(config.getAsJsonArray("fluids"));
            Set<ResourceLocation> tanks = ids(config.getAsJsonArray("empty_tanks"));
            for (IIngredientType<?> type : List.copyOf(manager.getRegisteredIngredientTypes())) {
                filter(manager, type, items, fluids, tanks);
            }
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
