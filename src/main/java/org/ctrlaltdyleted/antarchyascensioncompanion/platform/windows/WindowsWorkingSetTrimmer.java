package org.ctrlaltdyleted.antarchyascensioncompanion.platform.windows;

import java.util.Locale;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public final class WindowsWorkingSetTrimmer {
    private static final boolean WINDOWS = System.getProperty("os.name", "")
            .toLowerCase(Locale.ROOT)
            .startsWith("windows");

    private WindowsWorkingSetTrimmer() {
    }

    public static boolean isSupported() {
        return WINDOWS;
    }

    public static TrimResult trimCurrentProcess() {
        if (!WINDOWS) {
            return TrimResult.unsupported();
        }

        try {
            Pointer process = Kernel32.INSTANCE.GetCurrentProcess();
            if (process == null || Pointer.nativeValue(process) == 0L) {
                return TrimResult.failed(0, "GetCurrentProcess returned an invalid handle");
            }

            boolean success = Psapi.INSTANCE.EmptyWorkingSet(process);
            if (!success) {
                return TrimResult.failed(Native.getLastError(), "EmptyWorkingSet returned false");
            }

            return TrimResult.successful();
        }
        catch (Throwable throwable) {
            return TrimResult.failed(0, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
        }
    }

    private interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        Pointer GetCurrentProcess();
    }

    private interface Psapi extends StdCallLibrary {
        Psapi INSTANCE = Native.load("psapi", Psapi.class);

        boolean EmptyWorkingSet(Pointer process);
    }

    public record TrimResult(boolean supported, boolean success, int win32Error, String detail) {
        private static TrimResult successful() {
            return new TrimResult(true, true, 0, "success");
        }

        private static TrimResult unsupported() {
            return new TrimResult(false, false, 0, "unsupported operating system");
        }

        private static TrimResult failed(int win32Error, String detail) {
            return new TrimResult(true, false, win32Error, detail);
        }
    }
}
