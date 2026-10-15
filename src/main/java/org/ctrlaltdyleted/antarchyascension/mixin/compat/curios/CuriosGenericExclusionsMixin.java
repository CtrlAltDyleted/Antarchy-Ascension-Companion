package org.ctrlaltdyleted.antarchyascension.mixin.compat.curios;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.mixin.CuriosImplMixinHooks;

/** Curios 9.5.1 lacks a generic_exclusions predicate. */
@Mixin(value = CuriosImplMixinHooks.class, remap = false)
public abstract class CuriosGenericExclusionsMixin {
    @Unique
    private static final TagKey<Item> antarchyascension$exclusions = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath("curios", "generic_exclusions"));

    @Inject(method = "testCurioPredicates", at = @At("HEAD"), cancellable = true)
    private static void antarchyascension$excludeGeneric(Set<ResourceLocation> predicates,
            SlotResult result, CallbackInfoReturnable<Boolean> cir) {
        if ("curio".equals(result.slotContext().identifier())
                && result.stack().is(antarchyascension$exclusions)) cir.setReturnValue(false);
    }

    // Direct equipment checks bypass testCurioPredicates in 9.5.1.
    @Inject(method = "isStackValid", at = @At("HEAD"), cancellable = true)
    private static void antarchyascension$excludeGenericStack(SlotContext context,
            ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if ("curio".equals(context.identifier())
                && stack.is(antarchyascension$exclusions)) cir.setReturnValue(false);
    }
}
