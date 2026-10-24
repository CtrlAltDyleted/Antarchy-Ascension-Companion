package org.ctrlaltdyleted.antarchyascension.compat.arsnouveau;

import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = AntarchyAscension.MODID)
public final class ArsNouveauLoginRewardsCompat {
    private static final String antarchyascension$STARBUNCLE_PLUSH_REWARD = "an_plush";

    private ArsNouveauLoginRewardsCompat() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void antarchyascension$disableStarbunclePlushReward(
            PlayerEvent.PlayerLoggedInEvent event) {

        // Marks the plush reward as already received
        CompoundTag persistentData = event.getEntity().getPersistentData();
        CompoundTag persistedPlayerData = persistentData.getCompound(Player.PERSISTED_NBT_TAG);
        persistedPlayerData.putBoolean(antarchyascension$STARBUNCLE_PLUSH_REWARD, true);
        persistentData.put(Player.PERSISTED_NBT_TAG, persistedPlayerData);
    }
}
