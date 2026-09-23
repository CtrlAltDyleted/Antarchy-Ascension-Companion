package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2helpers;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenStyle.class)
public interface ScreenStyleWidgetsAccessor {
    @Accessor("widgets")
    Map<String, WidgetStyle> antarchyascension$widgets();
}
