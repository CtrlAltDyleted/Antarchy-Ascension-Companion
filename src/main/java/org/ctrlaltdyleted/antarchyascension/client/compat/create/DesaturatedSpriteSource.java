package org.ctrlaltdyleted.antarchyascension.client.compat.create;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import org.ctrlaltdyleted.antarchyascension.AntarchyAscension;

// Makes grayscale sprites for unfinished items
@EventBusSubscriber(modid = AntarchyAscension.MODID, value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD)
public record DesaturatedSpriteSource(ResourceLocation resource, ResourceLocation sprite)
        implements SpriteSource {
    public static final MapCodec<DesaturatedSpriteSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("resource").forGetter(DesaturatedSpriteSource::resource),
                    ResourceLocation.CODEC.fieldOf("sprite").forGetter(DesaturatedSpriteSource::sprite)
            ).apply(instance, DesaturatedSpriteSource::new));
    private static final SpriteSourceType TYPE = new SpriteSourceType(CODEC);

    @SubscribeEvent
    public static void register(RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(AntarchyAscension.MODID, "desaturated"), TYPE);
    }

    @Override
    public void run(ResourceManager manager, Output output) {
        var path = TEXTURE_ID_CONVERTER.idToFile(resource);
        manager.getResource(path).ifPresentOrElse(parent -> output.add(sprite, loader -> {
            // Loads a separate copy of the texture
            var contents = loader.loadSprite(sprite, parent);
            if (contents != null) {
                contents.getOriginalImage().applyToAllPixels(DesaturatedSpriteSource::grayscale);
            }
            return contents;
        }), () -> AntarchyAscension.LOGGER.error("Missing unfinished-item parent texture: {}", path));
    }

    private static int grayscale(int abgr) {
        // Reads the pixel colors
        int red = abgr & 255;
        int green = (abgr >>> 8) & 255;
        int blue = (abgr >>> 16) & 255;
        // Keeps the shading and transparency
        // Calculates the grayscale brightness
        int gray = (54 * red + 183 * green + 19 * blue + 128) >>> 8;
        return (abgr & 0xFF000000) | (gray << 16) | (gray << 8) | gray;
    }

    @Override
    public SpriteSourceType type() {
        return TYPE;
    }
}
