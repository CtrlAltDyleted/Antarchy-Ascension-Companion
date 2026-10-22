package org.ctrlaltdyleted.antarchyascension.content;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.ctrlaltdyleted.antarchyascension.AntarchyAscension.MODID;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> DENSE_URANIUM_NUGGET = ITEMS.registerSimpleItem("dense_uranium_nugget");
    public static final DeferredItem<Item> RAW_DENSE_URANIUM = ITEMS.registerSimpleItem("raw_dense_uranium");
    public static final DeferredItem<Item> DENSE_URANIUM_INGOT = ITEMS.registerSimpleItem("dense_uranium_ingot");
    public static final DeferredItem<Item> POWDERED_SAND = ITEMS.registerSimpleItem("powdered_sand");
    public static final DeferredItem<Item> SILICA_COMPOUND = ITEMS.registerSimpleItem("silica_compound");
    public static final DeferredItem<Item> COMPACTED_SILICA = ITEMS.registerSimpleItem("compacted_silica");
    public static final DeferredItem<Item> CRUDE_SILICON = ITEMS.registerSimpleItem("crude_silicon");
    public static final DeferredItem<Item> PRESSED_SILICON_SUBSTRATE = ITEMS.registerSimpleItem("pressed_silicon_substrate");
    public static final DeferredItem<Item> BASIC_CIRCUIT_PRESS = ITEMS.registerSimpleItem("basic_circuit_press");
    public static final DeferredItem<Item> INCOMPLETE_BLANK_PATTERN = ITEMS.registerSimpleItem("incomplete_blank_pattern");
    public static final DeferredItem<Item> INCOMPLETE_1K_COMPONENT = ITEMS.registerSimpleItem("incomplete_1k_component");
    public static final DeferredItem<BlockItem> RAW_DENSE_URANIUM_BLOCK =
            ITEMS.registerSimpleBlockItem(ModBlocks.RAW_DENSE_URANIUM_BLOCK);
    public static final DeferredItem<BlockItem> DENSE_URANIUM_BLOCK =
            ITEMS.registerSimpleBlockItem(ModBlocks.DENSE_URANIUM_BLOCK);
    public static final DeferredItem<BlockItem> SILICA_GLASS =
            ITEMS.registerSimpleBlockItem(ModBlocks.SILICA_GLASS);
    public static final DeferredItem<BlockItem> REINFORCED_SILICA_GLASS =
            ITEMS.registerSimpleBlockItem(ModBlocks.REINFORCED_SILICA_GLASS);
    public static final DeferredItem<Item> SKYBOUND_SIGIL = ITEMS.registerSimpleItem("skybound_sigil", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GROUNDED_SIGIL = ITEMS.registerSimpleItem("grounded_sigil", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> NETHER_CRYSTAL_SEED = ITEMS.registerSimpleItem("nether_crystal_seed", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> NETHER_CORE = ITEMS.registerSimpleItem("nether_core", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> SKYBOUND_KEY = ITEMS.registerSimpleItem("skybound_key", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> FROZEN_EXANIMATE_ESSENCE = ITEMS.registerSimpleItem("frozen_exanimate_essence");
    public static final DeferredItem<Item> CALCULATION_MICROCHIP = ITEMS.registerSimpleItem("calculation_microchip");
    public static final DeferredItem<Item> INCOMPLETE_CALCULATION_MICROCHIP = ITEMS.registerSimpleItem("incomplete_calculation_microchip");
    public static final DeferredItem<Item> INDUSTRIAL_CORE = ITEMS.registerSimpleItem("industrial_core");
    public static final DeferredItem<Item> KEYSTONE_OF_INDUSTRY = ITEMS.registerSimpleItem("keystone_of_industry", new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> PACK_EMBLEM = ITEMS.registerSimpleItem("pack_emblem");
    private ModItems() {
    }
}
