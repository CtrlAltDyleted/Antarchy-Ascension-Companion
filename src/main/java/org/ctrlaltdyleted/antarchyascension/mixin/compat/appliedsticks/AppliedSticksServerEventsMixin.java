package org.ctrlaltdyleted.antarchyascension.mixin.compat.appliedsticks;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.benbenlaw.appliedsticks.event.ServerEvents", remap = false)
public abstract class AppliedSticksServerEventsMixin {
    @Unique
    private static final TagKey<Item> antarchyascension$CONSTRUCTION_STICKS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("constructionstick", "construction_sticks"));

    @Unique
    private static final ResourceLocation antarchyascension$WIRELESS_LINK_TARGET =
            ResourceLocation.fromNamespaceAndPath("ae2", "wireless_link_target");

    @Shadow
    @Final
    private static HashMap<Player, Set<BlockPos>> lastSentPositions;

    @Inject(method = "updateStickJob", at = @At("HEAD"), cancellable = true, remap = false)
    private static void antarchyascension$skipUnneededRaycast(
            PlayerTickEvent.Pre event,
            CallbackInfo ci) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        // Only continues for a linked construction stick
        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(antarchyascension$CONSTRUCTION_STICKS)) {
            antarchyascension$clearAndCancel(player, ci);
            return;
        }

        DataComponentType<?> wirelessLinkTarget =
                BuiltInRegistries.DATA_COMPONENT_TYPE.get(antarchyascension$WIRELESS_LINK_TARGET);
        if (wirelessLinkTarget == null || !heldItem.has(wirelessLinkTarget)) {
            antarchyascension$clearAndCancel(player, ci);
        }
    }

    @Unique
    private static void antarchyascension$clearAndCancel(Player player, CallbackInfo ci) {
        // Clears the old preview when the job stops
        lastSentPositions.remove(player);
        ci.cancel();
    }
}
