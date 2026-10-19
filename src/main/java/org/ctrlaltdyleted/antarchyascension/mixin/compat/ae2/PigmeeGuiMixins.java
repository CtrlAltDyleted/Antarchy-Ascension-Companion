package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.CPUSelectionList;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

// Pigmee/AE2 GUI compatibility fixes
public final class PigmeeGuiMixins {
    private PigmeeGuiMixins() {}

    // Crafting confirmation screens
    @Pseudo
    @Mixin(targets = {"appeng.client.gui.me.crafting.CraftConfirmScreen",
            "com.moakiee.ae2lt.client.AE2LtCraftConfirmScreen"}, remap = false)
    public static abstract class CraftConfirmLabels {
        @Shadow(aliases = "selectCpu") @Final private Button selectCPU;
        @Unique private Component antarchyascension$fullCpuLabel;

        @Inject(method = "getNextCpuButtonLabel", at = @At("RETURN"), cancellable = true)
        private void antarchyascension$fitCpuLabel(CallbackInfoReturnable<Component> cir) {
            antarchyascension$fullCpuLabel = cir.getReturnValue();
            int width = selectCPU == null ? 170 : selectCPU.getWidth() - 10;
            cir.setReturnValue(antarchyascension$fit(antarchyascension$fullCpuLabel, width));
        }

        @Inject(method = "updateBeforeRender", at = @At("RETURN"))
        private void antarchyascension$fullNameTooltip(CallbackInfo ci) {
            if (antarchyascension$fullCpuLabel != null) {
                selectCPU.setTooltip(Tooltip.create(antarchyascension$fullCpuLabel));
            }
        }
        // Display helpers are merged into the target class by Mixin.
        @Unique
        private static Component antarchyascension$fit(Component original, int width) {
            var font = Minecraft.getInstance().font;
            String full = original.getString();
            int available = Math.max(1, width);
            if (font.width(original) <= available) return original;
            String ellipsis = "...";
            String fitted = font.plainSubstrByWidth(full, Math.max(0, available - font.width(ellipsis))) + ellipsis;
            return Component.literal(fitted).withStyle(original.getStyle());
        }
    }

    // CPU selection list
    @Mixin(value = CPUSelectionList.class, remap = false)
    public static abstract class CpuSelectionLabels {
        @Shadow @Final private Blitter buttonBg;

        // Change only the name draw call
        @Redirect(method = "drawBackgroundLayer", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"))
        private int antarchyascension$fitCpuRow(GuiGraphics graphics, Font font, Component name,
                int x, int y, int color, boolean shadow) {
            int width = (int) ((buttonBg.getSrcWidth() - 6) / 0.666f);
            return antarchyascension$drawCpuName(graphics, font, name, x, y, color, shadow, width);
        }

        // Display helpers are merged into the target class by Mixin.
        @Unique
        private static Component antarchyascension$fit(Component original, int width) {
            var font = Minecraft.getInstance().font;
            String full = original.getString();
            int available = Math.max(1, width);
            if (font.width(original) <= available) return original;
            String ellipsis = "...";
            String fitted = font.plainSubstrByWidth(full, Math.max(0, available - font.width(ellipsis))) + ellipsis;
            return Component.literal(fitted).withStyle(original.getStyle());
        }

        @Unique
        private static int antarchyascension$drawCpuName(GuiGraphics graphics, Font font, Component name,
                int x, int y, int color, boolean shadow, int width) {
            Component fullPigmeeName = Component.translatable("block.ae2lt.pigmee_mentalmath_unit");
            if (name.getString().equals(fullPigmeeName.getString())) {
                name = Component.translatable("gui.antarchyascension.pigmee_cpu.short_name")
                        .withStyle(name.getStyle());
            }
            float scale = Math.max(0.75f, Math.min(1, (float) width / Math.max(1, font.width(name))));
            Component fitted = antarchyascension$fit(name, (int) (width / scale));
            graphics.pose().pushPose();
            try {
                graphics.pose().translate(x, y, 0);
                graphics.pose().scale(scale, scale, 1);
                return graphics.drawString(font, fitted, 0, 0, color, shadow);
            } finally {
                graphics.pose().popPose();
            }
        }
    }

    // Synthesis Station title and crafting-plan text
    @Mixin(value = AEBaseScreen.class, remap = false)
    public static abstract class CraftPlanHeader extends AbstractContainerScreen<AEBaseMenu> {
        protected CraftPlanHeader(AEBaseMenu menu, Inventory inventory, Component title) {
            super(menu, inventory, title);
        }

        @ModifyVariable(method = "setTextContent", at = @At("HEAD"), argsOnly = true, ordinal = 0)
        private Component antarchyascension$fitHeader(Component text, String id, Component original) {
            String screen = getClass().getName();
            if (screen.equals("com.moakiee.ae2lt.client.PigmeeSynthesisStationScreen")
                    && id.equals("dialog_title")) {
                // Retain the two-line title
                return Component.translatable("gui.antarchyascension.pigmee_station.title");
            }
            if ((screen.equals("appeng.client.gui.me.crafting.CraftConfirmScreen")
                    || screen.equals("com.moakiee.ae2lt.client.AE2LtCraftConfirmScreen"))
                    && (id.equals("dialog_title") || id.equals("cpu_status") || id.equals("bytes_used"))) {
                return antarchyascension$fit(text, imageWidth - (id.equals("dialog_title") ? 16 : 40));
            }
            return text;
        }
        // Display helpers are merged into the target class by Mixin.
        @Unique
        private static Component antarchyascension$fit(Component original, int width) {
            var font = Minecraft.getInstance().font;
            String full = original.getString();
            int available = Math.max(1, width);
            if (font.width(original) <= available) return original;
            String ellipsis = "...";
            String fitted = font.plainSubstrByWidth(full, Math.max(0, available - font.width(ellipsis))) + ellipsis;
            return Component.literal(fitted).withStyle(original.getStyle());
        }
    }
}
