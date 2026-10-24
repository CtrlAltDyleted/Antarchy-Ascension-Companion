package org.ctrlaltdyleted.antarchyascension.compat.curios;

import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = AntarchyAscension.MODID)
public final class CurioEquipmentTickHandler {
    private CurioEquipmentTickHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player equipmentPlayer = event.getEntity();
        ItemStack wings = CurioEquipmentHelper.equippedElytra(equipmentPlayer);
        // Runs the Manticore wing effect on the server
        if (!equipmentPlayer.level().isClientSide && equipmentPlayer.isFallFlying()
                && CurioEquipmentHelper.isManticoreWings(wings)) {
            wings.getItem().inventoryTick(wings, equipmentPlayer.level(), equipmentPlayer, -1, false);
        }

        Player player = event.getEntity();

        ItemStack jetpack = CurioEquipmentHelper.findFirstInSlot(
                player,
                CurioEquipmentHelper.JETPACK_SLOT,
                stack -> stack.is(CurioEquipmentHelper.CREATE_SA_JETPACKS));

        // Ticks the equipped jetpack
        if (!jetpack.isEmpty()) {
            jetpack.getItem().inventoryTick(
                    jetpack,
                    player.level(),
                    player,
                    -1,
                    false);
        }

        // Ticks the filling and fueling tanks
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
