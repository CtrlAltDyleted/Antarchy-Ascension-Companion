package org.ctrlaltdyleted.antarchyascension.mixin.compat.visualworkbench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.level.Level;
import org.ctrlaltdyleted.antarchyascension.compat.visualworkbench.VisualWorkbenchMenuSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.shadowsoffire.fastbench.util.FastBenchUtil", remap = false)
public abstract class FastWorkbenchVisualSyncMixin {
    @Inject(
        method = "slotChangedCraftingGrid(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Ldev/shadowsoffire/fastbench/util/CraftingInventoryExt;Lnet/minecraft/world/inventory/ResultContainer;)V",
        at = @At("TAIL"),
        remap = false
    )
    private static void antarchyascension$notifyVisualWorkbench(Level level, Player player,
            @Coerce Object grid, ResultContainer result, CallbackInfo ci) {
        // Updates the display for the matching open workbench
        if (!level.isClientSide
                && player.containerMenu instanceof VisualWorkbenchMenuSync visuals
                && ((CraftingMenuStateAccessor) player.containerMenu).antarchyascension$craftSlots() == grid
                && ((CraftingMenuStateAccessor) player.containerMenu).antarchyascension$resultSlots() == result) {
            player.containerMenu.broadcastChanges();
            // Updates the block display even without a slot change
            
            visuals.antarchyascension$syncWorkbenchVisuals();
        }
    }
}
