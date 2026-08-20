package org.ctrlaltdyleted.antarchyascensioncompanion.compat.jei;

import appeng.api.stacks.GenericStack;
import com.glodblock.github.appflux.client.render.FluxKeyRenderHandler;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverter;
import tamaized.ae2jeiintegration.api.integrations.jei.IngredientConverters;

import java.util.List;

@JeiPlugin
public final class AppliedFluxFeJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID =
        ResourceLocation.fromNamespaceAndPath(
            "antarchy_ascension_companion",
            "applied_flux_fe"
        );

    private static final FluxKey FE_KEY = FluxKey.of(EnergyType.FE);

    private static final IIngredientType<FluxKey> FE_TYPE =
        new IIngredientType<>() {
            @Override
            public Class<? extends FluxKey> getIngredientClass() {
                return FluxKey.class;
            }

            @Override
            public String getUid() {
                return "antarchy_ascension_companion:forge_energy";
            }
        };

    private static final IIngredientHelper<FluxKey> FE_HELPER =
        new IIngredientHelper<>() {
            @Override
            public IIngredientType<FluxKey> getIngredientType() {
                return FE_TYPE;
            }

            @Override
            public String getDisplayName(FluxKey ingredient) {
                return "Forge Energy (FE)";
            }

            @Override
            public String getUniqueId(FluxKey ingredient, UidContext context) {
                return "appflux:fe";
            }

            @Override
            public ResourceLocation getResourceLocation(FluxKey ingredient) {
                return EnergyType.FE.id();
            }

            @Override
            public FluxKey copyIngredient(FluxKey ingredient) {
                return FluxKey.of(EnergyType.FE);
            }

            @Override
            public FluxKey normalizeIngredient(FluxKey ingredient) {
                return FE_KEY;
            }

            @Override
            public boolean isValidIngredient(FluxKey ingredient) {
                return ingredient != null
                    && ingredient.getEnergyType() == EnergyType.FE;
            }

            @Override
            public String getErrorInfo(FluxKey ingredient) {
                if (ingredient == null) {
                    return "null Forge Energy ingredient";
                }

                return "Applied Flux key: " + ingredient.getEnergyType();
            }
        };

    private static final IIngredientRenderer<FluxKey> FE_RENDERER =
        new IIngredientRenderer<>() {
            @Override
            public void render(
                GuiGraphics guiGraphics,
                FluxKey ingredient
            ) {
                FluxKeyRenderHandler.INSTANCE.drawInGui(
                    Minecraft.getInstance(),
                    guiGraphics,
                    0,
                    0,
                    ingredient
                );
            }

            @Override
            public List<Component> getTooltip(
                FluxKey ingredient,
                TooltipFlag tooltipFlag
            ) {
                return List.of(
                    FluxKeyRenderHandler.INSTANCE.getDisplayName(ingredient),
                    Component.literal("Forge Energy (FE)"),
                    Component.literal("Applied Flux")
                );
            }
        };

    private static final IngredientConverter<FluxKey> FE_CONVERTER =
        new IngredientConverter<>() {
            @Override
            public IIngredientType<FluxKey> getIngredientType() {
                return FE_TYPE;
            }

            @Override
            public FluxKey getIngredientFromStack(GenericStack stack) {
                if (
                    stack == null ||
                    !(stack.what() instanceof FluxKey key) ||
                    key.getEnergyType() != EnergyType.FE
                ) {
                    return null;
                }

                return FE_KEY;
            }

            @Override
            public GenericStack getStackFromIngredient(FluxKey ingredient) {
                if (
                    ingredient == null ||
                    ingredient.getEnergyType() != EnergyType.FE
                ) {
                    return null;
                }

                return new GenericStack(FE_KEY, 1L);
            }
        };

    public AppliedFluxFeJeiPlugin() {
        IngredientConverters.register(FE_CONVERTER);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerIngredients(
        IModIngredientRegistration registration
    ) {
        registration.register(
            FE_TYPE,
            List.of(FE_KEY),
            FE_HELPER,
            FE_RENDERER,
            FluxKey.CODEC
        );
    }
}
