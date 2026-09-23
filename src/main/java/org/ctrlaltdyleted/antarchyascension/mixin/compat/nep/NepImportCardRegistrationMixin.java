package org.ctrlaltdyleted.antarchyascension.mixin.compat.nep;

import appeng.api.upgrades.Upgrades;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Upgrades.class)
public abstract class NepImportCardRegistrationMixin {
    @Inject(
        method = "add(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/level/ItemLike;ILjava/lang/String;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void antarchyascension$excludeNepImportCard(
        ItemLike card, ItemLike host, int maxCount, String group, CallbackInfo ci
    ) {
        if (BuiltInRegistries.ITEM.getKey(card.asItem()).toString().equals("nep:import_card")) {
            ci.cancel();
        }
    }
}
