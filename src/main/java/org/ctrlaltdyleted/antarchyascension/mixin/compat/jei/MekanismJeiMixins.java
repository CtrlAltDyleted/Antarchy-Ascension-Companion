package org.ctrlaltdyleted.antarchyascension.mixin.compat.jei;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "mekanism.client.recipe_viewer.jei.MekJeiWidget", remap = false)
public abstract class MekanismJeiMixins {
    @WrapOperation(
            method = "mouseClicked(DDI)Z",
            at = @At(value = "INVOKE", target =
                    "Lmekanism/client/gui/element/GuiElement;mouseClicked(DDI)Z")
    )
    private boolean antarchyascension$leaveGaugeClicksToJei(
            @Coerce Object element, double mouseX, double mouseY, int button,
            Operation<Boolean> original) {
        if ((button == 0 || button == 1) && antarchyascension$isTankGauge(element)) return false;
        return original.call(element, mouseX, mouseY, button);
    }

    @WrapOperation(
            method = "mouseReleased(DDI)Z",
            at = @At(value = "INVOKE", target =
                    "Lmekanism/client/gui/element/GuiElement;mouseReleased(DDI)Z")
    )
    private boolean antarchyascension$leaveGaugeReleasesToJei(
            @Coerce Object element, double mouseX, double mouseY, int button,
            Operation<Boolean> original) {
        if ((button == 0 || button == 1) && antarchyascension$isTankGauge(element)) return false;
        return original.call(element, mouseX, mouseY, button);
    }

    @Unique
    private boolean antarchyascension$isTankGauge(Object element) {
        // Matches fluid and chemical gauges
        for (Class<?> type = element.getClass(); type != null; type = type.getSuperclass()) {
            if (type.getName().equals("mekanism.client.gui.element.gauge.GuiTankGauge")) return true;
        }
        return false;
    }
}
