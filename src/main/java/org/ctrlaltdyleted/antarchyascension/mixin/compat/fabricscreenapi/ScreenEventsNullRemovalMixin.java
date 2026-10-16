package org.ctrlaltdyleted.antarchyascension.mixin.compat.fabricscreenapi;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.fabricmc.fabric.api.client.screen.v1.ScreenEvents", remap = false)
public abstract class ScreenEventsNullRemovalMixin {
    @Unique
    private static Event<ScreenEvents.Remove> antarchyascension$nullRemovalEvent;

    @Inject(
        method = "remove(Lnet/minecraft/client/gui/screens/Screen;)Lnet/fabricmc/fabric/api/event/Event;",
        at = @At("HEAD"),
        cancellable = true,
        require = 1,
        allow = 1,
        remap = false
    )
    private static void antarchyascension$ignoreNullRemoval(
            Screen screen, CallbackInfoReturnable<Event<ScreenEvents.Remove>> callback) {
        if (screen == null) {
            // Callers immediately invoke the returned event, so null is not a safe return value.
            if (antarchyascension$nullRemovalEvent == null) {
                antarchyascension$nullRemovalEvent = EventFactory.createArrayBacked(
                        ScreenEvents.Remove.class, listeners -> ignored -> {});
            }
            callback.setReturnValue(antarchyascension$nullRemovalEvent);
        }
    }
}
