package org.ctrlaltdyleted.antarchyascension.client.compat.quark;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

@EventBusSubscriber(modid = AntarchyAscension.MODID, value = Dist.CLIENT)
public final class QuarkHudCleanup {
    private static ArmorShift pending;

    public interface ArmorShift {
        void restoreArmorShift();
    }

    public static void track(ArmorShift shift) {
        pending = shift;
    }

    public static void clear(ArmorShift shift) {
        if (pending == shift) pending = null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void canceledArmor(RenderGuiLayerEvent.Pre event) {
        // Canceled layers never fire Quark's normal Post cleanup
        if (event.isCanceled() && event.getName().equals(VanillaGuiLayers.ARMOR_LEVEL) && pending != null) {
            pending.restoreArmorShift();
            pending = null;
        }
    }
}
