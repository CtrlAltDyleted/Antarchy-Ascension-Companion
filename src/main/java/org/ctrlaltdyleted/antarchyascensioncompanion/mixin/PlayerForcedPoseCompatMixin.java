package org.ctrlaltdyleted.antarchyascensioncompanion.mixin;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Player.class)
public abstract class PlayerForcedPoseCompatMixin {

    @Unique
    private static final String ANTARCHY$HELIUM_CLIENT =
            "it.hurts.shatterbyte.reliquified_artifacts.items.charm.HeliumFlamingoItem$HeliumFlamingoClientEvent";

    @Unique
    private static final String ANTARCHY$HELIUM_COMMON =
            "it.hurts.shatterbyte.reliquified_artifacts.items.charm.HeliumFlamingoItem$CommonEvent";

    @Unique
    private static final StackWalker ANTARCHY$STACK_WALKER =
            StackWalker.getInstance();

    @Unique
    private static final Set<UUID> ANTARCHY$CLIENT_HELIUM_POSES =
            ConcurrentHashMap.newKeySet();

    @Unique
    private static final Set<UUID> ANTARCHY$SERVER_HELIUM_POSES =
            ConcurrentHashMap.newKeySet();

    @Inject(
            method = "setForcedPose",
            at = @At("HEAD"),
            cancellable = true
    )
    private void antarchy$protectForcedPose(Pose pose, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        Set<UUID> heliumPoses = player.level().isClientSide()
                ? ANTARCHY$CLIENT_HELIUM_POSES
                : ANTARCHY$SERVER_HELIUM_POSES;

        UUID playerId = player.getUUID();

        boolean fromHeliumFlamingo = ANTARCHY$STACK_WALKER.walk(frames ->
                frames
                        .limit(12)
                        .map(StackWalker.StackFrame::getClassName)
                        .anyMatch(name ->
                                name.equals(ANTARCHY$HELIUM_CLIENT)
                                        || name.equals(ANTARCHY$HELIUM_COMMON)
                        )
        );

        if (!fromHeliumFlamingo) {
            heliumPoses.remove(playerId);
            return;
        }

        Pose currentPose = player.getForcedPose();
        boolean heliumOwnsPose = heliumPoses.contains(playerId);

        if (pose == Pose.SWIMMING) {
            if (currentPose == null || heliumOwnsPose) {
                heliumPoses.add(playerId);
                return;
            }

            ci.cancel();
            return;
        }

        if (pose == null) {
            if (heliumOwnsPose) {
                heliumPoses.remove(playerId);
                return;
            }

            if (currentPose != null) {
                ci.cancel();
            }
        }
    }
}
