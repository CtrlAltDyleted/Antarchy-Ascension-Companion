package org.ctrlaltdyleted.antarchyascension.compat.endrem;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.world.level.block.entity.BlockEntity;

public final class EndRemPortalFrameEyeCompat {
    private static final String FRAME_ENTITY_CLASS = "com.teamremastered.endrem.block.AncientPortalFrameEntity";

    private EndRemPortalFrameEyeCompat() {
    }

    public static boolean setEye(BlockEntity entity, String eye) {
        if (entity == null || !entity.getClass().getName().equals(FRAME_ENTITY_CLASS)) {
            return false;
        }

        try {
            entity.getClass().getMethod("setEye", String.class).invoke(entity, eye);
            entity.setChanged();
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Cannot update End Remastered portal-frame eye", exception);
        }
    }
}
