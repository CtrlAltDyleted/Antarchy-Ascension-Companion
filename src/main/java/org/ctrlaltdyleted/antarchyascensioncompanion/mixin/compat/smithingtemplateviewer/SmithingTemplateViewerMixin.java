package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.smithingtemplateviewer;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(
    targets = "com.buuz135.smithingtemplateviewer.jei.JEIPlugin",
    remap = false
)
public abstract class SmithingTemplateViewerMixin {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(
        method = "registerRecipes",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"
        ),
        remap = false
    )
    private Stream antarchy$filterUnsafeSmithingTrimRecipes(
        Stream stream,
        Predicate originalPredicate
    ) {
        return stream.filter(value -> {

            if (!originalPredicate.test(value)) {
                return false;
            }

            if (!(value instanceof SmithingTrimRecipe recipe)) {
                return false;
            }

            return antarchy$isSafeForSmithingTemplateViewer(recipe);
        });
    }

    private static boolean antarchy$isSafeForSmithingTemplateViewer(
        SmithingTrimRecipe recipe
    ) {
        try {
            Ingredient template = antarchy$getIngredient(recipe, "template");
            Ingredient base = antarchy$getIngredient(recipe, "base");
            Ingredient addition = antarchy$getIngredient(recipe, "addition");

            if (template == null || base == null || addition == null) {
                return false;
            }

            ItemStack[] templateItems = template.getItems();
            ItemStack[] baseItems = base.getItems();
            ItemStack[] additionItems = addition.getItems();

            if (templateItems.length == 0 || additionItems.length == 0) {
                return false;
            }

            boolean[] hasArmorForWrapperSlot = new boolean[4];

            for (ItemStack stack : baseItems) {
                if (!(stack.getItem() instanceof ArmorItem armorItem)) {
                    continue;
                }

                int wrapperIndex =
                    3 - armorItem.getType().getSlot().getIndex();

                if (wrapperIndex >= 0 && wrapperIndex < 4) {
                    hasArmorForWrapperSlot[wrapperIndex] = true;
                }
            }

            return hasArmorForWrapperSlot[0]
                && hasArmorForWrapperSlot[1]
                && hasArmorForWrapperSlot[2]
                && hasArmorForWrapperSlot[3];

        } catch (ReflectiveOperationException | RuntimeException ignored) {

            return false;
        }
    }

    private static Ingredient antarchy$getIngredient(
        SmithingTrimRecipe recipe,
        String fieldName
    ) throws ReflectiveOperationException {

        Field field =
            SmithingTrimRecipe.class.getDeclaredField(fieldName);

        if (!field.canAccess(recipe)) {
            field.trySetAccessible();
        }

        Object value = field.get(recipe);

        if (value instanceof Ingredient ingredient) {
            return ingredient;
        }

        return null;
    }
}