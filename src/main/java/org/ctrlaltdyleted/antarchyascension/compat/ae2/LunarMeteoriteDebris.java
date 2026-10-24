package org.ctrlaltdyleted.antarchyascension.compat.ae2;

import appeng.worldgen.meteorite.CraterType;
import appeng.worldgen.meteorite.PlacedMeteoriteSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LunarMeteoriteDebris {
    private static final ResourceLocation MOON = ResourceLocation.fromNamespaceAndPath("ad_astra", "moon");
    private static final ResourceLocation MOON_SAND = ResourceLocation.fromNamespaceAndPath("ad_astra", "moon_sand");
    private static final ResourceLocation MOON_STONE = ResourceLocation.fromNamespaceAndPath("ad_astra", "moon_stone");
    private static final ResourceLocation MOON_COBBLESTONE = ResourceLocation.fromNamespaceAndPath("ad_astra", "moon_cobblestone");

    private LunarMeteoriteDebris() {
    }

    public static PlacedMeteoriteSettings withoutCraterFluids(LevelAccessor level, PlacedMeteoriteSettings settings) {
        if (!isMoon(level)) {
            return settings;
        }

        // Removes lunar crater fluids
        CraterType type = settings.getCraterType();
        if (type != CraterType.LAVA && type != CraterType.WATER && !settings.isCraterLake()) {
            return settings;
        }

        return new PlacedMeteoriteSettings(
                settings.getPos(),
                settings.getMeteoriteRadius(),
                type == CraterType.LAVA || type == CraterType.WATER ? CraterType.NORMAL : type,
                settings.getFallout(),
                settings.isPureCrater(),
                false);
    }

    public static boolean place(LevelAccessor level, BlockPos pos, BlockState state, int flags) {
        if (!isMoon(level)) {
            return level.setBlock(pos, state, flags);
        }

        // Skips plants and dripstone
        if (isOverworldVegetationOrDripstone(state)) {
            return false;
        }

        // Uses lunar blocks for crater debris
        Block block = state.getBlock();
        if (block == Blocks.GRAVEL || block == Blocks.DIRT) {
            state = moonBlock(MOON_SAND);
        } else if (block == Blocks.STONE) {
            state = moonBlock(MOON_STONE);
        } else if (block == Blocks.COBBLESTONE) {
            state = moonBlock(MOON_COBBLESTONE);
        }

        return level.setBlock(pos, state, flags);
    }

    private static boolean isMoon(LevelAccessor level) {
        if (level instanceof WorldGenLevel worldGenLevel) {
            return worldGenLevel.getLevel().dimension().location().equals(MOON);
        }
        return level instanceof Level actualLevel && actualLevel.dimension().location().equals(MOON);
    }

    private static BlockState moonBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(id).defaultBlockState();
    }

    private static boolean isOverworldVegetationOrDripstone(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS_BLOCK
                || block == Blocks.DRIPSTONE_BLOCK
                || block == Blocks.POINTED_DRIPSTONE
                || block instanceof BushBlock
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.LOGS)
                || block == Blocks.VINE
                || block == Blocks.GLOW_LICHEN
                || block == Blocks.MOSS_CARPET
                || block == Blocks.MOSS_BLOCK;
    }
}
