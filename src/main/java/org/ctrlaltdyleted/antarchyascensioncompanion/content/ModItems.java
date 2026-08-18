package org.ctrlaltdyleted.antarchyascensioncompanion.content;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.ctrlaltdyleted.antarchyascensioncompanion.AntarchyAscensionCompanion.MODID;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> DENSE_URANIUM_NUGGET = ITEMS.registerSimpleItem("dense_uranium_nugget");
    public static final DeferredItem<Item> RAW_DENSE_URANIUM = ITEMS.registerSimpleItem("raw_dense_uranium");
    public static final DeferredItem<Item> DENSE_URANIUM_INGOT = ITEMS.registerSimpleItem("dense_uranium_ingot");

    public static final DeferredItem<BlockItem> RAW_DENSE_URANIUM_BLOCK =
            ITEMS.registerSimpleBlockItem(ModBlocks.RAW_DENSE_URANIUM_BLOCK);
    public static final DeferredItem<BlockItem> DENSE_URANIUM_BLOCK =
            ITEMS.registerSimpleBlockItem(ModBlocks.DENSE_URANIUM_BLOCK);

    private ModItems() {
    }
}
