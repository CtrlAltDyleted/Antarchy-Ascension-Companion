package org.ctrlaltdyleted.antarchyascension.mixin.compat.ae2helpers;

import appeng.menu.me.crafting.CraftConfirmMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftConfirmMenu.class)
public abstract class CraftConfirmCancelWatcherMixin {
    @Inject(method = "goBack", at = @At("HEAD"))
    private void antarchyascension$cancelPendingAutoInsert(CallbackInfo ci) {
        // Clears pending auto-insert when cancelling a craft
        if (!((CraftConfirmMenu) (Object) this).isClientSide()) {
            return;
        }

        try {
            Class<?> watcherClass = Class.forName("rearth.ae2helpers.client.AutoCraftingWatcher");
            Object watcher = watcherClass.getField("INSTANCE").get(null);
            watcherClass.getMethod("clear").invoke(watcher);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not clear AE2 Helpers pending recipe insertion", exception);
        }
    }
}
