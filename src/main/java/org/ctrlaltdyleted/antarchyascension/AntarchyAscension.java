package org.ctrlaltdyleted.antarchyascension;
import org.ctrlaltdyleted.antarchyascension.content.ModBlocks;
import org.ctrlaltdyleted.antarchyascension.content.ModItems;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(AntarchyAscension.MODID)
public class AntarchyAscension {
    public static final String MODID = "antarchyascension";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AntarchyAscension(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabContents);
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.DENSE_URANIUM_NUGGET.get());
            event.accept(ModItems.RAW_DENSE_URANIUM.get());
            event.accept(ModItems.DENSE_URANIUM_INGOT.get());
            event.accept(ModItems.POWDERED_SAND.get());
            event.accept(ModItems.SILICA_COMPOUND.get());
            event.accept(ModItems.COMPACTED_SILICA.get());
            event.accept(ModItems.CRUDE_SILICON.get());
            event.accept(ModItems.PRESSED_SILICON_SUBSTRATE.get());
            event.accept(ModItems.CALCULATION_CIRCUIT.get());
            event.accept(ModItems.SKYBOUND_KEY.get());
            event.accept(ModItems.FROZEN_EXANIMATE_ESSENCE.get());
            event.accept(ModItems.SKYBOUND_SIGIL.get());
            event.accept(ModItems.GROUNDED_SIGIL.get());
            event.accept(ModItems.NETHER_CRYSTAL_SEED.get());
            event.accept(ModItems.NETHER_CORE.get());
            event.accept(ModItems.INDUSTRIAL_CORE.get());
            event.accept(ModItems.KEYSTONE_OF_INDUSTRY.get());
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.RAW_DENSE_URANIUM_BLOCK.get());
            event.accept(ModItems.DENSE_URANIUM_BLOCK.get());
            event.accept(ModItems.SILICA_GLASS.get());
            event.accept(ModItems.REINFORCED_SILICA_GLASS.get());
        }
    }
}
