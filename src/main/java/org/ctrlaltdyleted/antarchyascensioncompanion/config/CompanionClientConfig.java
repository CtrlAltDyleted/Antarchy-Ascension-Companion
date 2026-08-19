package org.ctrlaltdyleted.antarchyascensioncompanion.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CompanionClientConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue TRIM_FIRST_WORLD_WORKING_SET;
    public static final ModConfigSpec.IntValue FIRST_WORLD_WORKING_SET_TRIM_DELAY_SECONDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("performance");

        TRIM_FIRST_WORLD_WORKING_SET = builder
                .comment(
                        "Windows only. After the first world loads, trim Minecraft's own Windows working set once.",
                        "This does not change the Java heap limit or free live Java objects.",
                        "Pages Minecraft still needs are automatically faulted back in by Windows."
                )
                .define("trimFirstWorldWorkingSet", true);

        FIRST_WORLD_WORKING_SET_TRIM_DELAY_SECONDS = builder
                .comment("Seconds to wait after the first playable world appears before trimming the working set.")
                .defineInRange("firstWorldWorkingSetTrimDelaySeconds", 5, 5, 300);

        builder.pop();
        SPEC = builder.build();
    }

    private CompanionClientConfig() {
    }
}
