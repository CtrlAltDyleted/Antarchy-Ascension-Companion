package org.ctrlaltdyleted.antarchyascensioncompanion;

import org.ctrlaltdyleted.antarchyascensioncompanion.content.ModBlocks;
import org.ctrlaltdyleted.antarchyascensioncompanion.content.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(AntarchyAscensionCompanion.MODID)
public class AntarchyAscensionCompanion {
    public static final String MODID = "antarchy_ascension_companion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AntarchyAscensionCompanion(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabContents);
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.DENSE_URANIUM_NUGGET.get());
            event.accept(ModItems.RAW_DENSE_URANIUM.get());
            event.accept(ModItems.DENSE_URANIUM_INGOT.get());
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.RAW_DENSE_URANIUM_BLOCK.get());
            event.accept(ModItems.DENSE_URANIUM_BLOCK.get());
        }
    }
}
