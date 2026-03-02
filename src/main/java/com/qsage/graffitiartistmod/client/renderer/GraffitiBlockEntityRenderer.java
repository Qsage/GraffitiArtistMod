package com.qsage.graffitiartistmod.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.block.GraffitiBlock;
import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class GraffitiBlockEntityRenderer implements BlockEntityRenderer<GraffitiBlockEntity> {
    private static final Map<String, DynamicTexture> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> LOCATION_CACHE = new HashMap<>();

    public GraffitiBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(GraffitiBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Direction facing = entity.getBlockState().getValue(GraffitiBlock.FACING);
        ResourceLocation textureLocation = getOrCreateTexture(entity.getBlockPos().toShortString(), entity);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        // ВРАЩЕНИЕ: Используем стандартное вращение для Direction
        poseStack.mulPose(facing.getRotation());

        // СМЕЩЕНИЕ: В системе координат getRotation(),
        // лицо блока — это Z=0.5. Сдвигаем на 0.501, чтобы не было мерцания.
        poseStack.translate(0, 0, 0.501);



        // Отрисовка холста (drawVertex) и рамки...
        VertexConsumer debugConsumer = buffer.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, debugConsumer, -0.5f, -0.5f, 0, 0.5f, 0.5f, 0.001f, 0, 1, 0, 1);

        poseStack.popPose();
    }

    private void drawVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, int light) {
        builder.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(0, 0, 1)
                .endVertex();
    }

    private ResourceLocation getOrCreateTexture(String id, GraffitiBlockEntity entity) {
        String safeId = id.replace(",", "_").replace("-", "_").replace(" ", "_").toLowerCase();

        if (!TEXTURE_CACHE.containsKey(safeId)) {
            DynamicTexture texture = new DynamicTexture(16, 16, true);

            // ИСПРАВЛЕНИЕ ОШИБКИ: Используем современный метод создания ResourceLocation
            // Это решает проблему "Expected 2 arguments"
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(GraffitiArtistMod.MOD_ID, "dynamic/" + safeId);

            texture.getPixels().fillRect(0, 0, 16, 16, 0x00000000);
            texture.upload();

            Minecraft.getInstance().getTextureManager().register(loc, texture);
            TEXTURE_CACHE.put(safeId, texture);
            LOCATION_CACHE.put(safeId, loc);
        }

        DynamicTexture texture = TEXTURE_CACHE.get(safeId);
        if (texture != null && entity.isDirty()) {
            updateTexture(texture, entity.getPixels());
            entity.markClean();
        }

        return LOCATION_CACHE.get(safeId);
    }

    private void updateTexture(DynamicTexture texture, byte[] pixels) {
        NativeImage img = texture.getPixels();
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                byte p = pixels[y * 16 + x];
                if (p != 0) {
                    img.setPixelRGBA(x, y, 0xFFFFFFFF);
                } else {
                    img.setPixelRGBA(x, y, 0x00000000);
                }
            }
        }
        texture.upload();
    }
}