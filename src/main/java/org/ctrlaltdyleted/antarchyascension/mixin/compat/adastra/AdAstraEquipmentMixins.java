package org.ctrlaltdyleted.antarchyascension.mixin.compat.adastra;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Handles Ad Astra equipment fixes
public final class AdAstraEquipmentMixins {
    private AdAstraEquipmentMixins() {}

    // Native creative oxygen storage
    @Mixin(targets = "ad_astra_giselle_addon.common.item.CreativeOxygenCanItem", remap = false)
    public abstract static class CreativeOxygenCanMixin {
        @Inject(method = "getFluids", at = @At("RETURN"))
        private void antarchyascension$infiniteOxygen(CallbackInfoReturnable<Object> cir) {
            Object storage = cir.getReturnValue();
            try {
                Object slot = storage.getClass().getMethod("get", int.class).invoke(storage, 0);
                Object resource = slot.getClass().getMethod("getResource").invoke(slot);
                // Only fills a blank creative tank
                if (!(boolean) resource.getClass().getMethod("isBlank").invoke(resource)) return;
                Class<?> fluidResource = Class.forName("earth.terrarium.common_storage_lib.resources.fluid.FluidResource");
                Fluid oxygen = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("ad_astra", "oxygen"));
                if (oxygen == net.minecraft.world.level.material.Fluids.EMPTY) return;
                Object oxygenResource = fluidResource.getMethod("of", Fluid.class).invoke(null, oxygen);
                // Sets oxygen as the creative tank's fluid
                Object snapshot = fluidResource.getMethod("toStack", long.class).invoke(oxygenResource, 1L);
                // Keeps the creative oxygen supply unlimited
                Method read = slot.getClass().getMethod("readSnapshot",
                        Class.forName("earth.terrarium.common_storage_lib.resources.ResourceStack"));
                read.invoke(slot, snapshot);
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Cannot initialize Giselle creative oxygen storage", exception);
            }
        }
    }

    // Regular Space Helmet transparency
    @Mixin(HumanoidArmorLayer.class)
    public abstract static class SpaceHelmetTransparencyMixin {
        @ModifyExpressionValue(method = "renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;ILnet/minecraft/resources/ResourceLocation;)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"), require = 1)
        private RenderType antarchyascension$helmetAlpha(RenderType original,
                @Local(argsOnly = true) Model model, @Local(argsOnly = true) ResourceLocation texture) {
            // Only changes the normal space helmet
            if (!texture.toString().equals("ad_astra:textures/entity/armor/space_suit.png")
                    || !model.getClass().getName().equals("earth.terrarium.adastra.client.models.armor.SpaceSuitModel")) {
                return original;
            }
            try {
                Field slot = model.getClass().getDeclaredField("slot");
                slot.setAccessible(true);
                // Allows the helmet visor to be transparent
                return slot.get(model) == EquipmentSlot.HEAD ? RenderType.entityTranslucent(texture) : original;
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Cannot resolve Ad Astra helmet render slot", exception);
            }
        }
    }
    // Splits the tank text into two centered lines
    @Mixin(targets = "ad_astra_giselle_addon.client.overlay.OxygenCanOverlay", remap = false)
    public abstract static class OxygenHudLayout {
        @org.spongepowered.asm.mixin.Shadow public static net.minecraft.network.chat.Component getRatioText(double ratio) { throw new AssertionError(); }

        // Handles both tank HUD drawing paths
        @org.spongepowered.asm.mixin.injection.Redirect(method = "renderOxygenCanTank", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"), require = 1, allow = 1)
        private static int antarchyascension$standaloneTankLines(net.minecraft.client.gui.GuiGraphics graphics,
                net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text, int x, int y, int color,
                @Local(argsOnly = true) double ratio, @Local(ordinal = 3) int barX) {
            return antarchyascension$drawTankLines(graphics, font, text, barX + 31, y, color, ratio);
        }

        @org.spongepowered.asm.mixin.injection.Redirect(method = "lambda$renderHud$3", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"), require = 1, allow = 1)
        private static int antarchyascension$suitTankLines(net.minecraft.client.gui.GuiGraphics graphics,
                net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text, int x, int y, int color,
                @Local(argsOnly = true) double ratio, @Local(ordinal = 3) int unclampedX) {
            int center = unclampedX + font.width(text) / 2;
            return antarchyascension$drawTankLines(graphics, font, text, center, y, color, ratio);
        }

        @org.spongepowered.asm.mixin.Unique
        private static int antarchyascension$drawTankLines(net.minecraft.client.gui.GuiGraphics graphics,
                net.minecraft.client.gui.Font font, net.minecraft.network.chat.Component text,
                int center, int y, int color, double ratio) {
            var label = net.minecraft.network.chat.Component.literal("Oxygen Tank:").withStyle(text.getStyle());
            // Centers the label and value separately
            var value = getRatioText(ratio).copy().withStyle(text.getStyle());
            graphics.drawString(font, label, center - font.width(label) / 2, y, color);
            return graphics.drawString(font, value, center - font.width(value) / 2, y + font.lineHeight + 1, color);
        }

    }
}
