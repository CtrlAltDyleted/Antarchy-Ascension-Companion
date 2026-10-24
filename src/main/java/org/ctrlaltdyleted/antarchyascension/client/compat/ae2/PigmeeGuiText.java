package org.ctrlaltdyleted.antarchyascension.client.compat.ae2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class PigmeeGuiText {
    private PigmeeGuiText() {}

    // Fits the GUI text
    public static Component fitGuiText(Component original, int width) {
        var font = Minecraft.getInstance().font;
        String full = original.getString();
        int available = Math.max(1, width);
        if (font.width(original) <= available) return original;
        String ellipsis = "...";
        // Shortens long names
        String fitted = font.plainSubstrByWidth(full, Math.max(0, available - font.width(ellipsis))) + ellipsis;
        return Component.literal(fitted).withStyle(original.getStyle());
    }

    public static int drawCpuName(GuiGraphics graphics, Font font, Component name,
            int x, int y, int color, boolean shadow, int width) {
        Component fullPigmeeName = Component.translatable("block.ae2lt.pigmee_mentalmath_unit");
        if (name.getString().equals(fullPigmeeName.getString())) {
            name = Component.translatable("gui.antarchyascension.pigmee_cpu.short_name")
                    .withStyle(name.getStyle());
        }
        // Scales the CPU name to fit
        float scale = Math.max(0.75f, Math.min(1, (float) width / Math.max(1, font.width(name))));
        Component fitted = fitGuiText(name, (int) (width / scale));
        graphics.pose().pushPose();
        try {
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(scale, scale, 1);
            return graphics.drawString(font, fitted, 0, 0, color, shadow);
        } finally {
            // Restores the drawing scale
            graphics.pose().popPose();
        }
    }
}
