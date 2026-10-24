package org.ctrlaltdyleted.antarchyascension.mixin.compat.luckyblock;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.creoii.luckyblock.neoforge.LuckyBlockNeoForge", remap = false)
public abstract class LuckyBlockNeoForgeClientInitMixin {
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void antarchyascension$initLuckyBlockClient(IEventBus modBus, CallbackInfo ci) {
        // Only loads the client setup on the client
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        try {
            Class<?> client = Class.forName("dev.creoii.luckyblock.client.LuckyBlockClient");
            client.getDeclaredMethod("initClient").invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to initialize Lucky Block client atlas source", e);
        }
    }
}
