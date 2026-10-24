package org.ctrlaltdyleted.antarchyascension.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.ctrlaltdyleted.antarchyascension.AntarchyAscension.MODID;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    // Requires the correct tool for uranium blocks
    public static final DeferredBlock<Block> RAW_DENSE_URANIUM_BLOCK = BLOCKS.register(
            "raw_dense_uranium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> DENSE_URANIUM_BLOCK = BLOCKS.register(
            "dense_uranium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    // Sets up the glass blocks
    public static final DeferredBlock<Block> SILICA_GLASS = BLOCKS.register(
            "silica_glass",
            () -> new TransparentBlock(BlockBehaviour.Properties.of()
                    .strength(0.5F).sound(SoundType.GLASS).noOcclusion()));
    public static final DeferredBlock<Block> REINFORCED_SILICA_GLASS = BLOCKS.register(
            "reinforced_silica_glass",
            () -> new TransparentBlock(BlockBehaviour.Properties.of()
                    .strength(1.5F, 6.0F).sound(SoundType.GLASS).noOcclusion()));

    private ModBlocks() {
    }
}
