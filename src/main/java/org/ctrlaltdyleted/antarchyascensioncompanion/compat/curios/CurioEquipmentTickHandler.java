package org.ctrlaltdyleted.antarchyascensioncompanion.compat.curios;

import org.ctrlaltdyleted.antarchyascensioncompanion.AntarchyAscensionCompanion;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AntarchyAscensionCompanion.MODID)
public final class CurioEquipmentTickHandler {
    private CurioEquipmentTickHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!ModList.get().isLoaded("create_sa")) {
            return;
        }

        Player player = event.getEntity();

        ItemStack jetpack = CurioEquipmentHelper.findFirstInSlot(
                player,
                CurioEquipmentHelper.JETPACK_SLOT,
                stack -> stack.is(CurioEquipmentHelper.CREATE_SA_JETPACKS));

        if (!jetpack.isEmpty()) {
            jetpack.getItem().inventoryTick(
                    jetpack,
                    player.level(),
                    player,
                    -1,
                    false);
        }

        CurioEquipmentHelper.forEachStackInSlot(
                player,
                CurioEquipmentHelper.TANK_SLOT,
                stack -> stack.getItem().inventoryTick(
                        stack,
                        player.level(),
                        player,
                        -1,
                        false));
    }
}
