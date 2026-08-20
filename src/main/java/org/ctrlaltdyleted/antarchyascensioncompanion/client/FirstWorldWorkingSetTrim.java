package org.ctrlaltdyleted.antarchyascensioncompanion.client;

import java.util.concurrent.TimeUnit;

import org.ctrlaltdyleted.antarchyascensioncompanion.AntarchyAscensionCompanion;
import org.ctrlaltdyleted.antarchyascensioncompanion.config.CompanionClientConfig;
import org.ctrlaltdyleted.antarchyascensioncompanion.platform.windows.WindowsWorkingSetTrimmer;
import org.ctrlaltdyleted.antarchyascensioncompanion.platform.windows.WindowsWorkingSetTrimmer.TrimResult;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = AntarchyAscensionCompanion.MODID, value = Dist.CLIENT)
public final class FirstWorldWorkingSetTrim {
    private static boolean scheduled;
    private static boolean attempted;
    private static long trimAtNanos;

    private FirstWorldWorkingSetTrim() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (attempted || !WindowsWorkingSetTrimmer.isSupported()) {
            return;
        }

        if (!CompanionClientConfig.SPEC.isLoaded()) {
            return;
        }

        if (!CompanionClientConfig.TRIM_FIRST_WORLD_WORKING_SET.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        boolean inPlayableWorld = minecraft.level != null && minecraft.player != null;

        if (!inPlayableWorld) {
            if (scheduled) {
                scheduled = false;
                trimAtNanos = 0L;
            }
            return;
        }

        if (!scheduled) {
            int delaySeconds = CompanionClientConfig.FIRST_WORLD_WORKING_SET_TRIM_DELAY_SECONDS.get();
            trimAtNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(delaySeconds);
            scheduled = true;
            AntarchyAscensionCompanion.LOGGER.info(
                    "First-world Windows working-set trim scheduled in {} seconds",
                    delaySeconds
            );
            return;
        }

        if (System.nanoTime() < trimAtNanos) {
            return;
        }

        attempted = true;
        scheduled = false;

        TrimResult result = WindowsWorkingSetTrimmer.trimCurrentProcess();
        if (result.success()) {
            AntarchyAscensionCompanion.LOGGER.info(
                    "Trimmed Minecraft's Windows working set after the first world load"
            );
        }
        else if (result.supported()) {
            AntarchyAscensionCompanion.LOGGER.warn(
                    "Failed to trim Minecraft's Windows working set after the first world load: {} (Win32 error {})",
                    result.detail(),
                    result.win32Error()
            );
        }
    }
}
