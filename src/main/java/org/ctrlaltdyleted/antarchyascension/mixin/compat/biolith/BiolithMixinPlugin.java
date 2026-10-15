package org.ctrlaltdyleted.antarchyascension.mixin.compat.biolith;

import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Skips the Biolith accessor when Biolith is absent because @Accessor has no
 * optional-injection equivalent to require = 0.
 */
public final class BiolithMixinPlugin implements IMixinConfigPlugin {
    private static final String ACCESSOR_MIXIN =
            "org.ctrlaltdyleted.antarchyascension.mixin.compat.biolith.BiolithDimensionTypeAccessor";

    private boolean antarchyascension$biolithLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        antarchyascension$biolithLoaded =
                FMLLoader.getLoadingModList().getModFileById("biolith") != null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (ACCESSOR_MIXIN.equals(mixinClassName)) {
            return antarchyascension$biolithLoaded;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass,
            String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass,
            String mixinClassName, IMixinInfo mixinInfo) {
    }
}
