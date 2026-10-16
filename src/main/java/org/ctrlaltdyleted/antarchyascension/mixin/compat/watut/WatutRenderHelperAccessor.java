package org.ctrlaltdyleted.antarchyascension.mixin.compat.watut;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "com.corosus.watut.client.screen.RenderHelper", remap = false)
public interface WatutRenderHelperAccessor {
    @Accessor("performingOwnRender")
    static boolean antarchyascension$isCapturing() {
        throw new AssertionError("WATUT accessor was not applied");
    }

    @Invoker("renderWithTooltipEnd")
    static void antarchyascension$capture(GuiGraphics graphics, int mouseX, int mouseY,
            float partialTick) {
        throw new AssertionError("WATUT invoker was not applied");
    }
}
