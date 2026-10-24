package org.ctrlaltdyleted.antarchyascension.mixin.compat.curios;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import fr.iglee42.createqualityoflife.config.CreateQOLConfigs;
import fr.iglee42.createqualityoflife.registries.QOLDataComponents;
import fr.iglee42.createqualityoflife.utils.ArmorRenderType;
import fr.iglee42.createqualityoflife.utils.PreferredRender;
import fr.iglee42.createqualityoflife.utils.QOLConfigurableItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.ctrlaltdyleted.antarchyascension.compat.curios.CurioEquipmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.mixin.CuriosImplMixinHooks;

// Handles Curios equipment fixes
@Mixin(value = CuriosImplMixinHooks.class, remap = false)
public abstract class CuriosEquipmentMixins {
    @Inject(method = "testCurioPredicates", at = @At("HEAD"), cancellable = true)
    private static void antarchyascension$excludeGeneric(Set<ResourceLocation> predicates,
            SlotResult result, CallbackInfoReturnable<Boolean> cir) {
        if (CurioEquipmentHelper.reject(result.slotContext().identifier(), result.stack())) cir.setReturnValue(false);
    }

    @Inject(method = "isStackValid", at = @At("HEAD"), cancellable = true)
    private static void antarchyascension$excludeGenericStack(SlotContext context,
            ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Checks direct slot placement too
        if (CurioEquipmentHelper.reject(context.identifier(), stack)) cir.setReturnValue(false);
    }

    // Flight continuation and client start
    // Uses the normal flight update
    @Mixin(LivingEntity.class)
    public abstract static class FlightContinuation {
        @ModifyExpressionValue(
                method = "updateFallFlying",
                at = @At(value = "INVOKE", target =
                        "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"),
                require = 1,
                allow = 1)
        private ItemStack antarchyascension$hdpeFlightStack(ItemStack chest) {
            // Selects the equipment used for native flight
            return CurioEquipmentHelper.flightStack(chest, (LivingEntity) (Object) this);
        }
    }

    // Registered only on the client
    @Mixin(targets = "net.minecraft.client.player.LocalPlayer")
    public abstract static class ClientFlightStart {
        @ModifyExpressionValue(
                method = "aiStep",
                at = @At(value = "INVOKE", target =
                        "Lnet/minecraft/client/player/LocalPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"),
                require = 1,
                allow = 1)
        private ItemStack antarchyascension$hdpeClientFlightStack(ItemStack chest) {
            // Selects the equipment used for native flight
            return CurioEquipmentHelper.flightStack(chest, (LivingEntity) (Object) this);
        }
    }

    // Vanilla layer visibility and Ornithopter slot lookup
    @Mixin(targets = "net.minecraft.client.renderer.entity.layers.ElytraLayer")
    public abstract static class Visibility {
        // Uses the stack after Evolved Mekanism checks it
        @org.spongepowered.asm.mixin.injection.ModifyVariable(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
                at = @At("STORE"), ordinal = 0, require = 1, allow = 1)
        private ItemStack antarchyascension$visualEquipment(ItemStack original,
                @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) LivingEntity entity) {
            // Keeps hidden Curios wings from revealing chest wings
            ItemStack selected = CurioEquipmentHelper.renderedElytra(entity);
            return CurioEquipmentHelper.isAntarchyWings(selected) || CurioEquipmentHelper.isOrnithopter(selected)
                    ? ItemStack.EMPTY : selected;
        }

    }

    @Mixin(targets = "net.lulaboludo.createornithopterglider.integration.curios.CuriosLoaded", remap = false)
    public abstract static class GliderSlot {
        @Inject(method = "getOrnithopter", at = @At("RETURN"), cancellable = true)
        private void antarchyascension$dedicatedSlot(Player player, CallbackInfoReturnable<ItemStack> cir) {
            // Finds the glider in its dedicated slot
            cir.setReturnValue(CurioEquipmentHelper.ornithopter(player));
        }
    }

    // Native Antarchy wing abilities
    @Mixin(targets = "com.craisinlord.antarchy.content.item.BrutalflyElytraItem", remap = false)
    public abstract static class BrutalflyEquipment {
        @WrapOperation(method = "isWearingBrutalflyElytra", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
        private static ItemStack antarchyascension$equippedWings(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
            // Uses the selected equipment for wing abilities
            return CurioEquipmentHelper.abilityStack(original.call(entity, slot), entity);
        }
    }

    @Mixin(targets = "com.craisinlord.antarchy.content.item.ManticoreWingsItem", remap = false)
    public abstract static class ManticoreEquipment {
        @WrapOperation(method = "isWearingManticoreWings", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
        private static ItemStack antarchyascension$equippedWings(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
            // Uses the selected equipment for wing abilities
            return CurioEquipmentHelper.abilityStack(original.call(entity, slot), entity);
        }

        @WrapOperation(method = "inventoryTick", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
        private ItemStack antarchyascension$collisionWings(Player player, EquipmentSlot slot, Operation<ItemStack> original) {
            return CurioEquipmentHelper.abilityStack(original.call(player, slot), player);
        }
    }

    @Mixin(targets = "com.craisinlord.antarchy.neoforge.network.BrutalflyElytraNetworking", remap = false)
    public abstract static class ServerFlapEquipment {
        @WrapOperation(method = "lambda$handleFlap$0", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/server/level/ServerPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
        private static ItemStack antarchyascension$flapWings(ServerPlayer player, EquipmentSlot slot, Operation<ItemStack> original) {
            return CurioEquipmentHelper.abilityStack(original.call(player, slot), player);
        }
    }

    // Native wing rendering
    @Mixin(targets = {"com.craisinlord.antarchy.content.client.renderer.BrutalflyElytraLayer",
            "com.craisinlord.antarchy.content.client.renderer.ManticoreWingsLayer"}, remap = false)
    public abstract static class AntarchyWingsRenderingMixin {
        @WrapOperation(method = "render", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/client/player/AbstractClientPlayer;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
        private ItemStack antarchyascension$nativeWingModel(AbstractClientPlayer player, EquipmentSlot slot, Operation<ItemStack> original) {
            // Uses the selected stack with the native wing model
            return CurioEquipmentHelper.renderedElytra(player);
        }

        @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
                at = {
                    @At(value = "INVOKE", target = "Lcom/craisinlord/antarchy/content/item/BrutalflyElytraItem;isWearingBrutalflyElytra(Lnet/minecraft/world/entity/LivingEntity;)Z"),
                    @At(value = "INVOKE", target = "Lcom/craisinlord/antarchy/content/item/ManticoreWingsItem;isWearingManticoreWings(Lnet/minecraft/world/entity/LivingEntity;)Z")
                }, require = 1, allow = 1)
        private boolean antarchyascension$visibleNativeWings(boolean original,
                @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) AbstractClientPlayer player) {
            ItemStack selected = CurioEquipmentHelper.renderedElytra(player);
            // Checks which wing layer is rendering
            String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(selected.getItem()).toString();
            boolean visible = getClass().getName().equals("com.craisinlord.antarchy.content.client.renderer.BrutalflyElytraLayer")
                    ? id.equals("antarchy:brutalfly_elytra") : id.equals("antarchy:manticore_wings");
            return visible;
        }

    }

    // Quark owns the existing hotbar usage/durability indicator.
    @Mixin(targets = "org.violetmoon.quark.content.client.module.UsageTickerModule$Client$TickerElement", remap = false)
    public abstract static class UsageTickerEquipment {
        @WrapOperation(method = "getStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1)
        private ItemStack antarchyascension$curioTicker(Player player, EquipmentSlot slot, Operation<ItemStack> original) {
            ItemStack chest = original.call(player, slot);
            ItemStack wings = slot == EquipmentSlot.CHEST ? CurioEquipmentHelper.equippedElytra(player) : ItemStack.EMPTY;
            return wings.isEmpty() ? chest : wings;
        }
    }
    @Mixin(targets = "org.violetmoon.quark.content.client.module.ElytraIndicatorModule$Client", remap = false)
    public abstract static class ElytraIndicatorEquipment {
        @WrapOperation(method = "hudPre", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1, allow = 1)
        private ItemStack antarchyascension$curioIndicator(Player player, EquipmentSlot slot, Operation<ItemStack> original) {
            // Checks equipped wings regardless of render visibility
            ItemStack wings = CurioEquipmentHelper.equippedElytra(player);
            return wings.isEmpty() ? original.call(player, slot) : wings;
        }
    }

    // Ornithopter needs Unbreaking applied before its damage method
    // Only changes normal gliding damage
    @Mixin(net.minecraft.world.item.ElytraItem.class)
    public abstract static class OrnithopterFlightDurability {
        // Checks whether the glider can fly
        @Inject(method = "canElytraFly", at = @At("RETURN"), cancellable = true, require = 1)
        private void antarchyascension$eternalOrnithopterEligibility(ItemStack stack, LivingEntity entity,
                CallbackInfoReturnable<Boolean> cir) {
            // Allows Eternal gliders to pass the durability check
            if (!cir.getReturnValue() && CurioEquipmentHelper.isEternalOrnithopter(stack)) {
                cir.setReturnValue(true);
            }
        }

        @WrapOperation(method = "elytraFlightTick", at = @At(value = "INVOKE", target =
                "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"), require = 1, allow = 1)
        private void antarchyascension$enchantmentAwareGliding(ItemStack stack, int amount,
                LivingEntity entity, EquipmentSlot slot, Operation<Void> original) {
            if (CurioEquipmentHelper.isOrnithopter(stack)
                    && entity.level() instanceof net.minecraft.server.level.ServerLevel level) {
                // Applies Unbreaking to normal gliding damage
                amount = net.minecraft.world.item.enchantment.EnchantmentHelper.processDurabilityChange(level, stack, amount);
                if (amount == 0) return;
            }
            original.call(stack, amount, entity, slot);
        }
    }
    // Skips the cape draw when wings are visible
    @Mixin(net.minecraft.client.renderer.entity.layers.CapeLayer.class)
    public abstract static class CapeEquipmentVisibility {
        @WrapMethod(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
                require = 1, allow = 1)
        private void antarchyascension$selectedCapeVisibility(PoseStack pose, MultiBufferSource buffers, int light,
                AbstractClientPlayer player, float a, float b, float c, float d, float e, float f,
                Operation<Void> original) {
            // Skips both Marvel and vanilla cape drawing when wings are visible
            ItemStack visual = CurioEquipmentHelper.renderedElytra(player);
            if (visual.is(net.minecraft.world.item.Items.ELYTRA) || CurioEquipmentHelper.isSupportedElytra(visual)
                    || antarchyascension$visibleChestplateWings(visual, player)) {
                return;
            }
            // Keeps normal cape behavior
            original.call(pose, buffers, light, player, a, b, c, d, e, f);
        }

        @Unique
        private boolean antarchyascension$visibleChestplateWings(ItemStack stack, AbstractClientPlayer player) {
            if (stack.isEmpty()) return false;
            return switch (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()) {
                case "oritech:jetpack_elytra", "oritech:jetpack_exo_elytra",
                        "cataclysm:ignitium_elytra_chestplate" -> true;
                case "createqol:shadow_radiance_chestplate", "createqol:refined_radiance_chestplate" -> {
                    ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
                    // Respects QOL's invisible armor setting
                    if (head.getItem() instanceof QOLConfigurableItem helmet
                            && head.getOrDefault(QOLDataComponents.INVISIBLE_ARMOR, false)
                            && helmet.providedEffect(head).equals(MobEffects.INVISIBILITY)
                            && head.getOrDefault(QOLDataComponents.ARMOR_EFFECT, true)) yield false;
                    // Matches QOL's native wing visibility checks
                    yield CreateQOLConfigs.server().equipments.armors.elytraAllowed.get()
                            && Boolean.TRUE.equals(stack.get(QOLDataComponents.BACKTANK_ELYTRA))
                            && stack.getOrDefault(QOLDataComponents.ARMOR_RENDER_TYPE, ArmorRenderType.ALL).shouldRenderAddition()
                            && stack.getOrDefault(QOLDataComponents.PREFERRED_RENDER, PreferredRender.BOTH).shouldRenderElytra();
                }
                default -> false;
            };
        }

        @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), require = 1, allow = 1)
        private boolean antarchyascension$visibleWingsSuppressCape(boolean original,
                @com.llamalad7.mixinextras.sugar.Local(argsOnly = true) AbstractClientPlayer player) {
            // Allows the cape when the Curios wings are hidden
            // Visible wings skip the cape draw
            // Keeps cape suppression separate from the wing texture
            return CurioEquipmentHelper.equippedElytra(player).isEmpty() ? original : false;
        }
    }
    @Mixin(targets = "com.github.L_Ender.cataclysm.client.model.armor.Ignitium_Elytra_chestplate_Model", remap = false)
    public abstract static class IgnitiumWingVisibility {
        @Shadow(remap = false) @Final private net.minecraft.client.model.geom.ModelPart leftWing;
        @Shadow(remap = false) @Final private net.minecraft.client.model.geom.ModelPart rightWing;

        @Inject(method = "withAnimations", at = @At("RETURN"), require = 1, allow = 1)
        private void antarchyascension$selectedArmorWings(LivingEntity entity, CallbackInfoReturnable<?> cir) {
            // Keeps chest wings hidden when a Curios Elytra is selected
            boolean visible = CurioEquipmentHelper.equippedElytra(entity).isEmpty();
            leftWing.visible = visible;
            rightWing.visible = visible;
        }
    }

    // Prevents the chest layer from drawing another glider
    @Mixin(targets = "net.lulaboludo.createornithopterglider.client.renderer.OrnithopterGliderLayer", remap = false)
    public abstract static class OrnithopterChestRendering {
        @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"), require = 1, allow = 1)
        private ItemStack antarchyascension$chestVisualFallback(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
            return CurioEquipmentHelper.equippedElytra(entity).isEmpty() ? original.call(entity, slot) : ItemStack.EMPTY;
        }
    }
}
