package org.ctrlaltdyleted.antarchyascension.mixin.compat.deimos;

import java.util.ArrayList;

import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Pseudo
@Mixin(
    targets = "com.mars.deimos.datagen.DeimosRecipeGenerator",
    remap = false
)
public abstract class DeimosGeneratedRecipeStackSizeMixin {
    @Inject(
        method = "createShapedRecipeJson(Ljava/util/ArrayList;Ljava/util/ArrayList;Ljava/lang/String;I)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void antarchyascension$rejectOversizedGeneratedShapedRecipe(
        ArrayList<String> ingredients,
        ArrayList<String> pattern,
        String resultId,
        int resultCount,
        CallbackInfo ci
    ) {
        if (antarchyascension$isOversizedResult(resultId, resultCount, "shaped")) {
            ci.cancel();
        }
    }

    @Inject(
        method = "createShapelessRecipeJson(Ljava/util/ArrayList;Ljava/lang/String;I)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void antarchyascension$rejectOversizedGeneratedShapelessRecipe(
        ArrayList<String> ingredients,
        String resultId,
        int resultCount,
        CallbackInfo ci
    ) {
        if (antarchyascension$isOversizedResult(resultId, resultCount, "shapeless")) {
            ci.cancel();
        }
    }

    @Inject(
        method = "createItemConvertorJson(Ljava/lang/String;Ljava/lang/String;I)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void antarchyascension$rejectOversizedGeneratedItemConverterRecipe(
        String ingredientId,
        String resultId,
        int resultCount,
        CallbackInfo ci
    ) {
        if (antarchyascension$isOversizedResult(resultId, resultCount, "item converter")) {
            ci.cancel();
        }
    }

    @Inject(
        method = "createStoneCuttingJson(Ljava/lang/String;Ljava/lang/String;I)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void antarchyascension$rejectOversizedGeneratedStonecuttingRecipe(
        String ingredientId,
        String resultId,
        int resultCount,
        CallbackInfo ci
    ) {
        if (antarchyascension$isOversizedResult(resultId, resultCount, "stonecutting")) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean antarchyascension$isOversizedResult(
        String resultId,
        int resultCount,
        String recipeKind
    ) {
        if (resultCount <= 1 || resultId == null || resultId.isBlank()) {
            return false;
        }

        ResourceLocation id = ResourceLocation.tryParse(resultId);

        if (id == null) {
            return false;
        }

        Item item = BuiltInRegistries.ITEM.get(id);

        if (item == Items.AIR) {
            return false;
        }

        ItemStack stack = item.getDefaultInstance();
        int maxStackSize = stack.getMaxStackSize();

        if (resultCount <= maxStackSize) {
            return false;
        }

        AntarchyAscension.LOGGER.info(
            "Blocked invalid Deimos-generated {} recipe: result={} count={} maxStackSize={}",
            recipeKind,
            id,
            resultCount,
            maxStackSize
        );

        return true;
    }
}
