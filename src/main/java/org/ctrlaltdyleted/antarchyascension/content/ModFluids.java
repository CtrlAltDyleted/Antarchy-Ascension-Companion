package org.ctrlaltdyleted.antarchyascension.content;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static org.ctrlaltdyleted.antarchyascension.AntarchyAscension.MODID;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MODID);
    public static final DeferredHolder<FluidType, FluidType> MOLTEN_DIMENSIONAL_ENERGY_TYPE = FLUID_TYPES.register(
            "molten_dimensional_energy", () -> new FluidType(FluidType.Properties.create()
                    .density(1800).viscosity(3000).temperature(350)
                    .canConvertToSource(false).canHydrate(false).canDrown(false)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MOLTEN_DIMENSIONAL_ENERGY = FLUIDS.register(
            "molten_dimensional_energy", () -> new BaseFlowingFluid.Source(properties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MOLTEN_DIMENSIONAL_ENERGY = FLUIDS.register(
            "flowing_molten_dimensional_energy", () -> new BaseFlowingFluid.Flowing(properties()));
    public static final DeferredBlock<LiquidBlock> MOLTEN_DIMENSIONAL_ENERGY_BLOCK = ModBlocks.BLOCKS.register(
            "molten_dimensional_energy", () -> new DimensionalEnergyBlock());

    private ModFluids() {}

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(MOLTEN_DIMENSIONAL_ENERGY_TYPE,
                MOLTEN_DIMENSIONAL_ENERGY, FLOWING_MOLTEN_DIMENSIONAL_ENERGY)
                .bucket(ModItems.MOLTEN_DIMENSIONAL_ENERGY_BUCKET).block(MOLTEN_DIMENSIONAL_ENERGY_BLOCK)
                .slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(20).explosionResistance(100.0F);
    }

    private static final class DimensionalEnergyBlock extends LiquidBlock {
        private DimensionalEnergyBlock() {
            super(MOLTEN_DIMENSIONAL_ENERGY.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                    .noLootTable().lightLevel(state -> 4));
        }

        @Override
        protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            if (level.isClientSide || !(entity instanceof Player player)
                    || player.getY() >= pos.getY() + state.getFluidState().getHeight(level, pos)) return;
            // Let Wither tick normally instead of refreshing it in every fluid block
            if (!player.hasEffect(MobEffects.WITHER)) {
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
            }
        }
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Client {
        private Client() {}

        @SubscribeEvent
        public static void registerExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return ResourceLocation.fromNamespaceAndPath(MODID, "block/molten_dimensional_energy_still");
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return ResourceLocation.fromNamespaceAndPath(MODID, "block/molten_dimensional_energy_flow");
                }

                @Override
                public int getTintColor() {
                    return 0xEF780B22;
                }
            }, MOLTEN_DIMENSIONAL_ENERGY_TYPE.get());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(new DynamicFluidContainerModel.Colors(), ModItems.MOLTEN_DIMENSIONAL_ENERGY_BUCKET.get());
        }

        @SubscribeEvent
        public static void setup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(MOLTEN_DIMENSIONAL_ENERGY.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(FLOWING_MOLTEN_DIMENSIONAL_ENERGY.get(), RenderType.translucent());
            });
        }
    }
}
