package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2helpers;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.client.gui.style.WidgetStyle;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetContainer.class)
public abstract class PatternProviderUpgradePanelLayoutMixin {
    @Shadow @Final private ScreenStyle style;

    @Inject(method = "populateScreen", at = @At("HEAD"))
    private void antarchyascension$separateImportPanel(
        Consumer<AbstractWidget> addWidget, Rect2i screenBounds, AEBaseScreen<?> screen, CallbackInfo ci
    ) {
        // Checks for the affected Pattern Provider screen
        if (!(screen instanceof PatternProviderScreen<?>)) {
            return;
        }

        Map<String, WidgetStyle> styles = ((ScreenStyleWidgetsAccessor) style).antarchyascension$widgets();
        WidgetStyle imports = styles.get("importupgrades");
        WidgetStyle base = styles.get("upgrades");
        if (imports == null || base == null) {
            return;
        }

        WidgetStyle relocated = new WidgetStyle();
        relocated.setLeft(base.getLeft());
        relocated.setRight(base.getRight());
        PatternProviderMenu menu = (PatternProviderMenu) screen.getMenu();
        int upgradeCount = menu.getSlots(SlotSemantics.UPGRADE).size();
        int baseTop = base.getTop() == null ? 0 : base.getTop();
        // Places the import panel below the upgrade rows
        relocated.setTop(baseTop + 10 + Math.min(8, upgradeCount) * 18 + 8);
        relocated.setWidth(imports.getWidth());
        relocated.setHeight(imports.getHeight());
        relocated.setHideEdge(imports.isHideEdge());
        styles.put("importupgrades", relocated);
    }
}
