package org.ctrlaltdyleted.antarchyascension.mixin.compat.createsa;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.ctrlaltdyleted.antarchyascension.client.compat.create_sa.CreateSaJetpackRenderHooks;

import org.ctrlaltdyleted.antarchyascension.compat.curios.CurioEquipmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Iterables;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(
        targets = {
                "net.mcreator.createstuffadditions.item.AndesiteJetpackItem$Chestplate",
                "net.mcreator.createstuffadditions.item.CopperJetpackItem$Chestplate",
                "net.mcreator.createstuffadditions.item.BrassJetpackItem$Chestplate",
                "net.mcreator.createstuffadditions.item.NetheriteJetpackItem$Chestplate"
        },
        remap = false
)
public abstract class CreateSaJetpackInventoryTickMixin {
    @Redirect(
            method = "inventoryTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Iterables;contains(Ljava/lang/Iterable;Ljava/lang/Object;)Z"
            ),
            require = 1
    )
    private boolean antarchyascension$treatDedicatedJetpackAsWorn(
            Iterable<?> armorSlots,
            Object wanted,
            ItemStack stack,
            Level level,
            Entity entity,
            int slot,
            boolean selected) {

        // Checks armor first, then the dedicated jetpack slot
        if (Iterables.contains(armorSlots, wanted)) {
            return true;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        return CurioEquipmentHelper.isExactStackInSlot(
                livingEntity,
                CurioEquipmentHelper.JETPACK_SLOT,
                stack) && CurioEquipmentHelper.resolveCreateSaJetpack(
                        livingEntity, EquipmentSlot.CHEST) == stack;
    }

    @Mixin(PlayerRenderer.class)
    public abstract static class FirstPersonHands {
        @WrapOperation(
                method = {"renderRightHand", "renderLeftHand"},
                at = @At(value = "INVOKE", target =
                        "Lnet/neoforged/neoforge/client/ClientHooks;renderSpecificFirstPersonArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/entity/HumanoidArm;)Z"),
                require = 2,
                allow = 2)
        private boolean antarchyascension$renderVisibleJetpackHand(PoseStack poseStack,
                MultiBufferSource buffers, int light, AbstractClientPlayer player,
                HumanoidArm arm, Operation<Boolean> original) {
            // The event gives chest equipment first choice, including LOWEST priority listeners
            return original.call(poseStack, buffers, light, player, arm)
                    || CreateSaJetpackRenderHooks.renderCurioHand(poseStack, buffers, light, player, arm);
        }
    }

    @Mixin(targets = "net.mcreator.createstuffadditions.client.renderer.jetpack.NetheriteJetpackFirstPersonRenderer",
            remap = false)
    public abstract static class NativeHandRenderer {
        @ModifyExpressionValue(
                method = "onRenderPlayerHand",
                at = @At(value = "FIELD", target =
                        "Lnet/mcreator/createstuffadditions/client/renderer/jetpack/NetheriteJetpackFirstPersonRenderer;rendererActive:Z"),
                require = 1,
                allow = 1)
        private static boolean antarchyascension$allowCurioHand(boolean active) {
            return active || CreateSaJetpackRenderHooks.isRenderingCurioHand();
        }
    }
}
