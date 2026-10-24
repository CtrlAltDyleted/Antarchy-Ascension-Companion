package org.ctrlaltdyleted.antarchyascension.client.compat.watut;

public final class WatutCaptureState {
    // Used on the render thread
    // Tracks whether the capture hook ran
    private static long hookCalls;

    private WatutCaptureState() {
    }

    public static long hookCalls() {
        return hookCalls;
    }

    public static void markHookCall() {
        hookCalls++;
    }
}
