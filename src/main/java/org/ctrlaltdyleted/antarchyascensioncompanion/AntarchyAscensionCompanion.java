package org.ctrlaltdyleted.antarchyascensioncompanion;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(AntarchyAscensionCompanion.MODID)
public class AntarchyAscensionCompanion {
    public static final String MODID = "antarchy_ascension_companion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AntarchyAscensionCompanion(IEventBus modEventBus, ModContainer modContainer) {
    }
}
