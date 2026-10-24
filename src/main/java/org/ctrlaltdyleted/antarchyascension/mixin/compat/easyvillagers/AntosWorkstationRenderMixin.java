package org.ctrlaltdyleted.antarchyascension.mixin.compat.easyvillagers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "de.maxhenkel.easyvillagers.blocks.tileentity.render.TraderRenderer", remap = false)
public abstract class AntosWorkstationRenderMixin {
    @Unique
    private static final ResourceLocation antarchyascension$computer =
            ResourceLocation.fromNamespaceAndPath("antos", "computer");

    @WrapOperation(
            method = "renderBlock(Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderSingleBlock(Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"),
            remap = false)
    private static void antarchyascension$renderComputer(
            BlockRenderDispatcher renderer, BlockState state, PoseStack pose,
            MultiBufferSource buffers, int light, int overlay, Operation<Void> original) {
        // Only replaces the AntOS computer render
        if (!BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(antarchyascension$computer)) {
            original.call(renderer, state, pose, buffers, light, overlay);
            return;
        }

        pose.pushPose();
        try {
            // Centers and scales the computer model
            pose.translate(0.5, 0.5, 0.5);
            pose.scale(2.0F, 2.0F, 2.0F);
            var minecraft = Minecraft.getInstance();
            minecraft.getItemRenderer().renderStatic(new ItemStack(state.getBlock()),
                    ItemDisplayContext.FIXED, light, overlay, pose, buffers, minecraft.level, 0);
        } finally {
            // Restores the render position and scale
            pose.popPose();
        }
    }
}
