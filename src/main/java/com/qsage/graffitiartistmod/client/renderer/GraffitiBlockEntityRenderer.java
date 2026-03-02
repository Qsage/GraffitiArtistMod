package com.qsage.graffitiartistmod.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import java.util.HashMap;
import java.util.Map;

public class GraffitiBlockEntityRenderer implements BlockEntityRenderer<GraffitiBlockEntity> {
    private static final Map<String, DynamicTexture> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> LOCATION_CACHE = new HashMap<>();

    public GraffitiBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(GraffitiBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        String id = entity.getBlockPos().toShortString();
        ResourceLocation textureLocation = getOrCreateTexture(id, entity);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        // Сдвиг вперед на 0.06 (толщина холста + отступ), чтобы не мерцало
        poseStack.translate(0, 0, 0.45);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(textureLocation));
        Matrix4f matrix = poseStack.last().pose();

        drawVertex(vertexConsumer, matrix, -0.5f, -0.5f, 0, 0, 1, packedLight);
        drawVertex(vertexConsumer, matrix, 0.5f, -0.5f, 0, 1, 1, packedLight);
        drawVertex(vertexConsumer, matrix, 0.5f, 0.5f, 0, 1, 0, packedLight);
        drawVertex(vertexConsumer, matrix, -0.5f, 0.5f, 0, 0, 0, packedLight);

        poseStack.popPose();
    }

    private void drawVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, int light) {
        builder.vertex(matrix, x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(655360).uv2(light).normal(0, 0, 1).endVertex();
    }

    @SuppressWarnings("removal")
    private ResourceLocation getOrCreateTexture(String id, GraffitiBlockEntity entity) {
        if (!TEXTURE_CACHE.containsKey(id)) {
            DynamicTexture texture = new DynamicTexture(16, 16, true);
            ResourceLocation loc = new ResourceLocation("graffitiartistmod", "dynamic/" + id.replace(", ", "_"));
            Minecraft.getInstance().getTextureManager().register(loc, texture);
            TEXTURE_CACHE.put(id, texture);
            LOCATION_CACHE.put(id, loc);
        }

        if (entity.isDirty()) {
            updateTexture(TEXTURE_CACHE.get(id), entity.getPixels());
            entity.markClean();
        }
        return LOCATION_CACHE.get(id);
    }

    private void updateTexture(DynamicTexture texture, byte[] pixels) {
        NativeImage img = texture.getPixels();
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                byte p = pixels[y * 16 + x];
                // 0 - прозрачный, 1 - белый (для теста)
                img.setPixelRGBA(x, y, p == 0 ? 0x00000000 : 0xFFFFFFFF);
            }
        }
        texture.upload();
    }
}