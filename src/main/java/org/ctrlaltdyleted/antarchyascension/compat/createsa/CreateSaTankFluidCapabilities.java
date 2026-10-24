package org.ctrlaltdyleted.antarchyascension.compat.createsa;

import java.lang.reflect.Constructor;

import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

@SuppressWarnings("removal")
@EventBusSubscriber(
        modid = AntarchyAscension.MODID,
        bus = EventBusSubscriber.Bus.MOD)
public final class CreateSaTankFluidCapabilities {
    private static final String HANDLER_CLASS =
            "net.mcreator.createstuffadditions.item.CustomFluidHandlerItemStack";

    private CreateSaTankFluidCapabilities() {
    }

    @SubscribeEvent
    public static void register(
            RegisterCapabilitiesEvent event) {


        // Finds the native tank handler
        Constructor<?> constructor =
                findConstructor();

        // Sets tank sizes in millibuckets
        registerTank(
                event,
                constructor,
                "small_filling_tank",
                8_000,
                FluidTags.WATER);

        registerTank(
                event,
                constructor,
                "medium_filling_tank",
                16_000,
                FluidTags.WATER);

        registerTank(
                event,
                constructor,
                "large_filling_tank",
                32_000,
                FluidTags.WATER);

        registerTank(
                event,
                constructor,
                "small_fueling_tank",
                8_000,
                FluidTags.LAVA);

        registerTank(
                event,
                constructor,
                "medium_fueling_tank",
                16_000,
                FluidTags.LAVA);

        registerTank(
                event,
                constructor,
                "large_fueling_tank",
                32_000,
                FluidTags.LAVA);

    }

    private static void registerTank(
            RegisterCapabilitiesEvent event,
            Constructor<?> constructor,
            String path,
            int capacity,
            TagKey<Fluid> fluidTag) {

        ResourceLocation id =
                ResourceLocation.fromNamespaceAndPath(
                        "create_sa",
                        path);

        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            throw new IllegalStateException(
                    "Missing Create SA item: " + id);
        }

        Item item =
                BuiltInRegistries.ITEM.get(id);

        // Adds fluid handling to each tank
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, context) ->
                        makeHandler(
                                constructor,
                                stack,
                                capacity,
                                fluidTag),
                item);
    }

    private static Constructor<?> findConstructor() {
        try {
            Class<?> type =
                    Class.forName(HANDLER_CLASS);

            return type.getConstructor(
                    ItemStack.class,
                    int.class,
                    TagKey.class);
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Could not resolve Create SA tank fluid handler",
                    e);
        }
    }

    private static IFluidHandlerItem makeHandler(
            Constructor<?> constructor,
            ItemStack stack,
            int capacity,
            TagKey<Fluid> fluidTag) {

        try {
            return (IFluidHandlerItem) constructor.newInstance(
                    stack,
                    capacity,
                    fluidTag);
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Could not create Create SA tank fluid handler",
                    e);
        }
    }
}
