package org.ctrlaltdyleted.antarchyascensioncompanion.mixin.compat.bettercombat;

import com.craisinlord.morph.api.MorphApi;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractClientPlayer.class, priority = 900)
public abstract class BetterCombatMorphAnimationMixin {
    @Unique
    private boolean antarchy$wasNonPlayerMorph;

    @Dynamic("Added to AbstractClientPlayer by Better Combat")
    @Inject(
        method = "playAttackAnimation(Ljava/lang/String;Lnet/bettercombat/logic/AnimatedHand;FF)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void antarchy$suppressAttackAnimationWhileMorphed(
        String animation,
        AnimatedHand hand,
        float length,
        float upswing,
        CallbackInfo ci
    ) {
        if (antarchy$isNonPlayerMorph()) {
            ci.cancel();
        }
    }

    @Dynamic("Added to AbstractClientPlayer by Better Combat")
    @Inject(
        method = "updateAnimationsOnTick()V",
        at = @At("HEAD"),
        remap = false
    )
    private void antarchy$stopRunningAttackAnimationOnMorph(
        CallbackInfo ci
    ) {
        boolean morphed = antarchy$isNonPlayerMorph();

        if (morphed && !antarchy$wasNonPlayerMorph) {
            ((PlayerAttackAnimatable) (Object) this)
                .stopAttackAnimation(0.0F);
        }

        antarchy$wasNonPlayerMorph = morphed;
    }

    @Unique
    private boolean antarchy$isNonPlayerMorph() {
        Player player = (Player) (Object) this;

        LivingEntity activeMorph =
            MorphApi.getApiImpl().getActiveMorphEntity(player);

        return activeMorph != null
            && !(activeMorph instanceof Player);
    }
}
