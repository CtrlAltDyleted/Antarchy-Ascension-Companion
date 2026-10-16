package org.ctrlaltdyleted.antarchyascension.mixin.compat.visualworkbench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.level.Level;
import org.ctrlaltdyleted.antarchyascension.compat.visualworkbench.VisualWorkbenchMenuSync;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.shadowsoffire.fastbench.util.FastBenchUtil", remap = false)
public abstract class FastWorkbenchVisualSyncMixin {
    @Inject(
        method = "slotChangedCraftingGrid(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Ldev/shadowsoffire/fastbench/util/CraftingInventoryExt;Lnet/minecraft/world/inventory/ResultContainer;)V",
        at = @At("TAIL"),
        remap = false
    )
    private static void antarchyascension$notifyVisualWorkbench(Level level, Player player,
            @Coerce Object grid, ResultContainer result, CallbackInfo ci) {
        if (!level.isClientSide
                && player.containerMenu instanceof VisualWorkbenchMenuSync visuals
                && ((CraftingMenuStateAccessor) player.containerMenu).antarchyascension$craftSlots() == grid
                && ((CraftingMenuStateAccessor) player.containerMenu).antarchyascension$resultSlots() == result) {
            player.containerMenu.broadcastChanges();
            // FastWorkbench's virtual result can already compare empty before its
            // backing result list is cleared. Notify the block even without a slot delta.
            visuals.antarchyascension$syncWorkbenchVisuals();
        }
    }
}
