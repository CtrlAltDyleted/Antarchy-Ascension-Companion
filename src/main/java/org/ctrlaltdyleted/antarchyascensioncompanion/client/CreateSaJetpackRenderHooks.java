package org.ctrlaltdyleted.antarchyascensioncompanion.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.ctrlaltdyleted.antarchyascensioncompanion.AntarchyAscensionCompanion;
import org.ctrlaltdyleted.antarchyascensioncompanion.compat.curios.CurioEquipmentHelper;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(
        modid = AntarchyAscensionCompanion.MODID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD
)
public final class CreateSaJetpackRenderHooks {
    private static final Map<
            RenderLayerParent<?, ?>,
            Map<String, RendererInvoker>
            > RENDERERS = new WeakHashMap<>();

    private static final Map<String, ArmorModelInvoker> ARMOR_MODELS =
            new HashMap<>();

    private CreateSaJetpackRenderHooks() {
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        if (!ModList.get().isLoaded("create_sa")) {
            return;
        }

        for (var skinModel : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skinModel);

            if (renderer != null) {
                renderer.addLayer(new CurioJetpackLayer(renderer));
            }
        }
    }

    private static final class CurioJetpackLayer
            extends RenderLayer<
                    AbstractClientPlayer,
                    PlayerModel<AbstractClientPlayer>
                    > {

        private final RenderLayerParent<
                AbstractClientPlayer,
                PlayerModel<AbstractClientPlayer>
                > parent;

        private CurioJetpackLayer(
                RenderLayerParent<
                        AbstractClientPlayer,
                        PlayerModel<AbstractClientPlayer>
                        > parent) {

            super(parent);
            this.parent = parent;
        }

        @Override
        public void render(
                PoseStack poseStack,
                MultiBufferSource bufferSource,
                int packedLight,
                AbstractClientPlayer player,
                float limbSwing,
                float limbSwingAmount,
                float partialTick,
                float ageInTicks,
                float netHeadYaw,
                float headPitch) {

            if (player == null || player.isInvisible()) {
                return;
            }

            ItemStack jetpack =
                    CurioEquipmentHelper.findFirstRenderedInSlot(
                            player,
                            CurioEquipmentHelper.JETPACK_SLOT,
                            stack -> stack.is(
                                    CurioEquipmentHelper.CREATE_SA_JETPACKS));

            if (jetpack.isEmpty()) {
                return;
            }

            renderJetpack(
                    poseStack,
                    bufferSource,
                    packedLight,
                    player,
                    limbSwing,
                    limbSwingAmount,
                    partialTick,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    jetpack);
        }

        private void renderJetpack(
                PoseStack poseStack,
                MultiBufferSource bufferSource,
                int packedLight,
                AbstractClientPlayer player,
                float limbSwing,
                float limbSwingAmount,
                float partialTick,
                float ageInTicks,
                float netHeadYaw,
                float headPitch,
                ItemStack jetpack) {

            JetpackVisualSpec spec = getVisualSpec(jetpack);

            if (spec == null) {
                return;
            }

            ArmorModelInvoker armorModel =
                    getOrCreateArmorModel(spec);

            if (armorModel != null) {
                armorModel.render(
                        this.getParentModel(),
                        poseStack,
                        bufferSource,
                        packedLight,
                        player,
                        limbSwing,
                        limbSwingAmount,
                        ageInTicks,
                        netHeadYaw,
                        headPitch);
            }

            RendererInvoker renderer =
                    getOrCreateRenderer(
                            parent,
                            spec.rendererClassName());

            if (renderer == null) {
                return;
            }

            ItemStack realChest =
                    player.getItemBySlot(EquipmentSlot.CHEST);

            try {
                // Create SA only renders its extra jetpack machinery when
                // it sees the jetpack in the vanilla chest slot.
                //
                // This runs inside the normal player render-layer pipeline,
                // so the PoseStack is already positioned correctly.
                player.setItemSlot(
                        EquipmentSlot.CHEST,
                        jetpack);

                renderer.render(
                        poseStack,
                        bufferSource,
                        packedLight,
                        player,
                        limbSwing,
                        limbSwingAmount,
                        partialTick,
                        ageInTicks,
                        netHeadYaw,
                        headPitch);
            }
            finally {
                player.setItemSlot(
                        EquipmentSlot.CHEST,
                        realChest);
            }
        }
    }

    private static JetpackVisualSpec getVisualSpec(
            ItemStack stack) {

        ResourceLocation id =
                BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (id == null) {
            return null;
        }

        return switch (id.toString()) {
            case "create_sa:andesite_jetpack_chestplate" ->
                    new JetpackVisualSpec(
                            "net.mcreator.createstuffadditions.client.model.Modelandesite_jetpack",
                            ResourceLocation.fromNamespaceAndPath(
                                    "create_sa",
                                    "textures/entities/andesite_jetpack/andesite_jetpack_1.png"),
                            "net.mcreator.createstuffadditions.client.renderer.jetpack.AndesiteJetpackArmorRenderer"
                    );

            case "create_sa:copper_jetpack_chestplate" ->
                    new JetpackVisualSpec(
                            "net.mcreator.createstuffadditions.client.model.Modelcopper_jetpack",
                            ResourceLocation.fromNamespaceAndPath(
                                    "create_sa",
                                    "textures/entities/copper_jetpack.png"),
                            "net.mcreator.createstuffadditions.client.renderer.jetpack.CopperJetpackArmorRenderer"
                    );

            case "create_sa:brass_jetpack_chestplate" ->
                    new JetpackVisualSpec(
                            "net.mcreator.createstuffadditions.client.model.Modelbrass_jetpack",
                            ResourceLocation.fromNamespaceAndPath(
                                    "create_sa",
                                    "textures/entities/brass_jetpack.png"),
                            "net.mcreator.createstuffadditions.client.renderer.jetpack.BrassJetpackArmorRenderer"
                    );

            case "create_sa:netherite_jetpack_chestplate" ->
                    new JetpackVisualSpec(
                            "net.mcreator.createstuffadditions.client.model.Modelnetherite_jetpack",
                            ResourceLocation.fromNamespaceAndPath(
                                    "create_sa",
                                    "textures/entities/netherite_jetpack.png"),
                            "net.mcreator.createstuffadditions.client.renderer.jetpack.NetheriteJetpackArmorRenderer"
                    );

            default -> null;
        };
    }

    private static ArmorModelInvoker getOrCreateArmorModel(
            JetpackVisualSpec spec) {

        synchronized (ARMOR_MODELS) {
            ArmorModelInvoker cached =
                    ARMOR_MODELS.get(spec.modelClassName());

            if (cached != null) {
                return cached;
            }

            try {
                Class<?> modelClass =
                        Class.forName(spec.modelClassName());

                Field layerField =
                        modelClass.getField("LAYER_LOCATION");

                ModelLayerLocation layerLocation =
                        (ModelLayerLocation) layerField.get(null);

                ModelPart bakedRoot =
                        Minecraft.getInstance()
                                .getEntityModels()
                                .bakeLayer(layerLocation);

                Object model =
                        modelClass
                                .getConstructor(ModelPart.class)
                                .newInstance(bakedRoot);

                Method setupAnim =
                        modelClass.getMethod(
                                "setupAnim",
                                Entity.class,
                                float.class,
                                float.class,
                                float.class,
                                float.class,
                                float.class);

                Method renderToBuffer =
                        modelClass.getMethod(
                                "renderToBuffer",
                                PoseStack.class,
                                com.mojang.blaze3d.vertex.VertexConsumer.class,
                                int.class,
                                int.class,
                                int.class);

                ArmorModelInvoker created =
                        new ArmorModelInvoker(
                                model,
                                setupAnim,
                                renderToBuffer,
                                modelClass.getField("body"),
                                modelClass.getField("rightarm"),
                                modelClass.getField("leftarm"),
                                spec.texture());

                ARMOR_MODELS.put(
                        spec.modelClassName(),
                        created);

                return created;
            }
            catch (ReflectiveOperationException | LinkageError ignored) {
                return null;
            }
        }
    }

    private static RendererInvoker getOrCreateRenderer(
            RenderLayerParent<?, ?> parent,
            String className) {

        synchronized (RENDERERS) {
            try {
                Map<String, RendererInvoker> rendererMap =
                        RENDERERS.computeIfAbsent(
                                parent,
                                ignored -> new HashMap<>());

                RendererInvoker cached =
                        rendererMap.get(className);

                if (cached != null) {
                    return cached;
                }

                Class<?> rendererClass =
                        Class.forName(className);

                Constructor<?> constructor =
                        rendererClass.getConstructor(
                                RenderLayerParent.class);

                Object renderer =
                        constructor.newInstance(parent);

                Method renderMethod =
                        rendererClass.getMethod(
                                "render",
                                PoseStack.class,
                                MultiBufferSource.class,
                                int.class,
                                LivingEntity.class,
                                float.class,
                                float.class,
                                float.class,
                                float.class,
                                float.class,
                                float.class);

                RendererInvoker created =
                        new RendererInvoker(
                                renderer,
                                renderMethod);

                rendererMap.put(
                        className,
                        created);

                return created;
            }
            catch (ReflectiveOperationException | LinkageError ignored) {
                return null;
            }
        }
    }
    private record JetpackVisualSpec(
            String modelClassName,
            ResourceLocation texture,
            String rendererClassName) {
    }

    private record RendererInvoker(
            Object renderer,
            Method renderMethod) {

        private void render(
                PoseStack poseStack,
                MultiBufferSource bufferSource,
                int packedLight,
                LivingEntity entity,
                float limbSwing,
                float limbSwingAmount,
                float partialTick,
                float ageInTicks,
                float netHeadYaw,
                float headPitch) {

            try {
                renderMethod.invoke(
                        renderer,
                        poseStack,
                        bufferSource,
                        packedLight,
                        entity,
                        limbSwing,
                        limbSwingAmount,
                        partialTick,
                        ageInTicks,
                        netHeadYaw,
                        headPitch);
            }
            catch (ReflectiveOperationException | LinkageError ignored) {
            }
        }
    }

    private record ArmorModelInvoker(
            Object model,
            Method setupAnim,
            Method renderToBuffer,
            Field bodyField,
            Field rightArmField,
            Field leftArmField,
            ResourceLocation texture) {

        private void render(
                PlayerModel<AbstractClientPlayer> parentModel,
                PoseStack poseStack,
                MultiBufferSource bufferSource,
                int packedLight,
                AbstractClientPlayer player,
                float limbSwing,
                float limbSwingAmount,
                float ageInTicks,
                float netHeadYaw,
                float headPitch) {

            try {
                setupAnim.invoke(
                        model,
                        player,
                        limbSwing,
                        limbSwingAmount,
                        ageInTicks,
                        netHeadYaw,
                        headPitch);

                copyPart(
                        parentModel.body,
                        (ModelPart) bodyField.get(model));

                copyPart(
                        parentModel.rightArm,
                        (ModelPart) rightArmField.get(model));

                copyPart(
                        parentModel.leftArm,
                        (ModelPart) leftArmField.get(model));

                var vertexConsumer =
                        bufferSource.getBuffer(
                                RenderType.entityCutoutNoCull(texture));

                renderToBuffer.invoke(
                        model,
                        poseStack,
                        vertexConsumer,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        0xFFFFFFFF);
            }
            catch (ReflectiveOperationException | LinkageError ignored) {
            }
        }
    }

    private static void copyPart(
            ModelPart source,
            ModelPart target) {

        target.visible = source.visible;

        target.x = source.x;
        target.y = source.y;
        target.z = source.z;

        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}
