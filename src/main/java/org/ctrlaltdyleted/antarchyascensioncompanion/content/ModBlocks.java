package org.ctrlaltdyleted.antarchyascensioncompanion.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import static org.ctrlaltdyleted.antarchyascensioncompanion.AntarchyAscensionCompanion.MODID;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

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

    private ModBlocks() {
    }
}
