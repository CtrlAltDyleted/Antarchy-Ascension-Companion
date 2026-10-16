package org.ctrlaltdyleted.antarchyascension.client.compat.watut;

public final class WatutCaptureState {
    // Read and written only on the render thread, including nested GUI renders.
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
