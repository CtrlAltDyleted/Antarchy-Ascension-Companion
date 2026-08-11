package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.appliedsticks;

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
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips Applied Construction Sticks' server-side raycast when the held item cannot
 * produce a stick job. The target mod otherwise performs the raycast every player tick.
 */
@Pseudo
@Mixin(targets = "com.benbenlaw.appliedsticks.event.ServerEvents", remap = false)
public abstract class AppliedSticksServerEventsMixin {
    @Unique
    private static final TagKey<Item> ANTARCHY_ASCENSION_COMPANION$CONSTRUCTION_STICKS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("constructionstick", "construction_sticks"));

    @Unique
    private static final ResourceLocation ANTARCHY_ASCENSION_COMPANION$WIRELESS_LINK_TARGET =
            ResourceLocation.fromNamespaceAndPath("ae2", "wireless_link_target");

    @Shadow
    @Final
    private static HashMap<Player, Set<BlockPos>> lastSentPositions;

    @Inject(method = "updateStickJob", at = @At("HEAD"), cancellable = true, remap = false)
    private static void antarchyAscensionCompanion$skipUnneededRaycast(
            PlayerTickEvent.Pre event,
            CallbackInfo ci) {
        Player player = event.getEntity();

        if (player.level().isClientSide()) {
            return;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ANTARCHY_ASCENSION_COMPANION$CONSTRUCTION_STICKS)) {
            antarchyAscensionCompanion$clearAndCancel(player, ci);
            return;
        }

        DataComponentType<?> wirelessLinkTarget =
                BuiltInRegistries.DATA_COMPONENT_TYPE.get(ANTARCHY_ASCENSION_COMPANION$WIRELESS_LINK_TARGET);
        if (wirelessLinkTarget == null || !heldItem.has(wirelessLinkTarget)) {
            antarchyAscensionCompanion$clearAndCancel(player, ci);
        }
    }

    @Unique
    private static void antarchyAscensionCompanion$clearAndCancel(Player player, CallbackInfo ci) {
        lastSentPositions.remove(player);
        ci.cancel();
    }
}
