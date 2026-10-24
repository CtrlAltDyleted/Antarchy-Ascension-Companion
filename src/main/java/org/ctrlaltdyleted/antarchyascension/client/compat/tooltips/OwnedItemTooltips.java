package org.ctrlaltdyleted.antarchyascension.client.compat.tooltips;

import java.util.Map;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

@EventBusSubscriber(modid = AntarchyAscension.MODID, value = Dist.CLIENT)
public final class OwnedItemTooltips {
    private static final Set<String> JETPACKS = Set.of("andesite_jetpack_chestplate",
            "copper_jetpack_chestplate", "brass_jetpack_chestplate", "netherite_jetpack_chestplate");
    private static final ResourceKey<Enchantment> ABOVE_THE_CLOUDS = ResourceKey.create(
            Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("create_sa", "above_the_clouds"));
    private static final Map<String, String> JETPACK_HINTS = Map.of(
            "item_desc.jetpack.spaceinair", "tooltip.antarchyascension.jetpack.air",
            "item_desc.jetpack.spacesneak", "tooltip.antarchyascension.jetpack.sneak",
            "item_desc.jetpack.spacewater", "tooltip.antarchyascension.jetpack.water");

    private OwnedItemTooltips() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltip(ItemTooltipEvent event) {
        var id = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        if (id.toString().equals("antarchy:raw_uranium_scrap")) {
            event.getToolTip().add(Component.translatable("tooltip.antarchyascension.uranium_scrap")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (org.ctrlaltdyleted.antarchyascension.compat.curios.CurioEquipmentHelper.isOxygenTank(event.getItemStack())) {
            event.getToolTip().add(Component.translatable("tooltip.antarchyascension.oxygen_tank.location")
                    .withStyle(ChatFormatting.GRAY));
        }
        // Skips unsupported items
        if (!id.getNamespace().equals("create_sa") || !JETPACKS.contains(id.getPath())) return;
        var level = Minecraft.getInstance().level;
        boolean unlimited = level != null && level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ABOVE_THE_CLOUDS)
                .map(holder -> event.getItemStack().getEnchantmentLevel(holder) > 0)
                .orElse(false);
        // Removes color codes before matching the altitude text
        String altitudeTemplate = ChatFormatting.stripFormatting(
                Component.translatable("item_desc.jetpack.desc2").getString()).strip();
        int placeholder = altitudeTemplate.indexOf('%');
        Pattern altitude = placeholder < 0 ? null : Pattern.compile(
                Pattern.quote(altitudeTemplate.substring(0, placeholder)) + "-?\\d+" +
                Pattern.quote(altitudeTemplate.substring(placeholder + 1)));
        // Gets the assigned flight key
        var flight = Arrays.stream(Minecraft.getInstance().options.keyMappings)
                .filter(mapping -> mapping.getName().equals("key.create_sa.flying"))
                .findFirst();
        Component key = flight.filter(mapping -> !mapping.isUnbound())
                .map(mapping -> mapping.getTranslatedKeyMessage())
                .orElseGet(() -> Component.translatable("tooltip.antarchyascension.unbound"));
        // Updates the matching tooltip lines
        for (int i = 0; i < event.getToolTip().size(); i++) {
            String line = event.getToolTip().get(i).getString();
            if (unlimited && altitude != null && altitude.matcher(
                    ChatFormatting.stripFormatting(line).strip()).matches()) {
                event.getToolTip().set(i, Component.translatable(
                        "tooltip.antarchyascension.create_sa.jetpack.unlimited_altitude")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                        .withStyle(event.getToolTip().get(i).getStyle()));
                continue;
            }
            for (var hint : JETPACK_HINTS.entrySet()) {
                if (line.equals(Component.translatable(hint.getKey()).getString())) {
                    event.getToolTip().set(i, Component.translatable(hint.getValue(), key)
                            .withStyle(ChatFormatting.GRAY));
                    break;
                }
            }
        }
    }

}
