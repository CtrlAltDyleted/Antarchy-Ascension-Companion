package org.ctrlaltdyleted.antarchyascensioncompanion;

import org.ctrlaltdyleted.antarchyascensioncompanion.content.ModBlocks;
import org.ctrlaltdyleted.antarchyascensioncompanion.content.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AntarchyAscensionCompanion.MODID)
public class AntarchyAscensionCompanion {
    public static final String MODID = "antarchyascension";
    private static final String LEGACY_MODID = "antarchy_ascension_companion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AntarchyAscensionCompanion(IEventBus modEventBus) {
        addLegacyAlias(ModBlocks.BLOCKS, "raw_dense_uranium_block");
        addLegacyAlias(ModBlocks.BLOCKS, "dense_uranium_block");
        addLegacyAlias(ModItems.ITEMS, "dense_uranium_nugget");
        addLegacyAlias(ModItems.ITEMS, "raw_dense_uranium");
        addLegacyAlias(ModItems.ITEMS, "dense_uranium_ingot");
        addLegacyAlias(ModItems.ITEMS, "raw_dense_uranium_block");
        addLegacyAlias(ModItems.ITEMS, "dense_uranium_block");

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabContents);
    }

    private static void addLegacyAlias(DeferredRegister<?> register, String path) {
        register.addAlias(
                ResourceLocation.fromNamespaceAndPath(LEGACY_MODID, path),
                ResourceLocation.fromNamespaceAndPath(MODID, path));
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
