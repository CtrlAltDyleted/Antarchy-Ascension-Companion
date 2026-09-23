package org.ctrlaltdyleted.antarchyascension.mixin;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerForcedPoseCompatMixin {

    @Unique
    private static final String antarchyascension$HELIUM_CLIENT =
            "it.hurts.shatterbyte.reliquified_artifacts.items.charm.HeliumFlamingoItem$HeliumFlamingoClientEvent";

    @Unique
    private static final String antarchyascension$HELIUM_COMMON =
            "it.hurts.shatterbyte.reliquified_artifacts.items.charm.HeliumFlamingoItem$CommonEvent";

    @Unique
    private static final StackWalker antarchyascension$STACK_WALKER =
            StackWalker.getInstance();

    @Unique
    private boolean antarchyascension$heliumOwnsPose;

    @Inject(
            method = "setForcedPose",
            at = @At("HEAD"),
            cancellable = true
    )
    private void antarchyascension$protectForcedPose(Pose pose, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        boolean fromHeliumFlamingo = antarchyascension$STACK_WALKER.walk(frames ->
                frames
                        .limit(12)
                        .map(StackWalker.StackFrame::getClassName)
                        .anyMatch(name ->
                                name.equals(antarchyascension$HELIUM_CLIENT)
                                        || name.equals(antarchyascension$HELIUM_COMMON)
                        )
        );

        if (!fromHeliumFlamingo) {
            antarchyascension$heliumOwnsPose = false;
            return;
        }

        Pose currentPose = player.getForcedPose();

        if (pose == Pose.SWIMMING) {
            if (currentPose == null || antarchyascension$heliumOwnsPose) {
                antarchyascension$heliumOwnsPose = true;
                return;
            }

            ci.cancel();
            return;
        }

        if (pose == null) {
            if (antarchyascension$heliumOwnsPose) {
                antarchyascension$heliumOwnsPose = false;
                return;
            }

            if (currentPose != null) {
                ci.cancel();
            }
        }
    }
}
