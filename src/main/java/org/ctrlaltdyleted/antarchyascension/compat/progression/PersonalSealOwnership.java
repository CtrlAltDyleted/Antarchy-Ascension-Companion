package org.ctrlaltdyleted.antarchyascension.compat.progression;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

@EventBusSubscriber(modid = AntarchyAscension.MODID)
public final class PersonalSealOwnership {
    private static final String OWNER = "antarchyascension_seal_crafter";

    private static boolean isSeal(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
            .equals("nether_remastered:seal_of_the_underworld_item");
    }

    @SubscribeEvent
    public static void crafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && isSeal(event.getCrafting())) {
            CustomData.update(DataComponents.CUSTOM_DATA, event.getCrafting(),
                tag -> tag.putUUID(OWNER, player.getUUID()));
        }
    }

    @SubscribeEvent
    public static void use(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !isSeal(event.getItemStack())) return;
        var tag = event.getItemStack().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.hasUUID(OWNER) || !tag.getUUID(OWNER).equals(player.getUUID())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            player.displayClientMessage(Component.translatable("message.antarchyascension.craft_own_seal"), true);
        }
    }

    private PersonalSealOwnership() {}
}
