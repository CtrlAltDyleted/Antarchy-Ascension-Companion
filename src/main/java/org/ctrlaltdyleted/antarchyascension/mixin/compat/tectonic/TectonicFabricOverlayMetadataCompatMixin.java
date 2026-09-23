package org.ctrlaltdyleted.antarchyascension.mixin.compat.tectonic;

import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractPackResources.class)
public abstract class TectonicFabricOverlayMetadataCompatMixin {
    @Shadow
    public abstract PackLocationInfo location();

    @Inject(method = "getMetadataSection", at = @At("HEAD"), cancellable = true)
    private <T> void antarchyascension$skipTectonicFabricOverlays(
        MetadataSectionSerializer<T> serializer,
        CallbackInfoReturnable<T> cir
    ) {
        if (
            "fabric:overlays".equals(serializer.getMetadataSectionName()) &&
            this.location().id().contains("tectonic")
        ) {
            cir.setReturnValue(null);
        }
    }
}