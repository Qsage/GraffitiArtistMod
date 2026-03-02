package com.qsage.graffitiartistmod.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qsage.graffitiartist.common.blockentity.GraffitiBlockEntity;
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

    // Кеш для текстур, чтобы не создавать их каждый кадр (это убьет FPS)
    private static final Map<String, DynamicTexture> TEXTURE_CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> LOCATION_CACHE = new HashMap<>();

    public GraffitiBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // Конструктор необходим для регистрации
    }

    @Override
    public void render(GraffitiBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // 1. Получаем уникальный ID для этого блока (по его позиции)
        String id = entity.getBlockPos().toShortString();

        // 2. Достаем или создаем динамическую текстуру 128x128
        ResourceLocation textureLocation = getOrCreateTexture(id, entity.getPixels());

        poseStack.pushPose();

        // 3. Позиционирование: центрируем и выносим на грань блока
        poseStack.translate(0.5f, 0.5f, 0.5f);
        // Тут можно добавить поворот в зависимости от того, куда смотрит блок
        // poseStack.mulPose(Axis.YP.rotationDegrees(180f));

        // Сдвигаем на 0.5 (край блока) + 0.005 (отступ для исключения мерцания/Z-Fighting)
        poseStack.translate(0, 0, 0.505f);

        // 4. Отрисовка
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(textureLocation));
        Matrix4f matrix = poseStack.last().pose();

        // Рисуем квадрат (Quad) из 4-х вершин
        // Параметры: матрица, x, y, z, цвет(r,g,b,a), координаты текстуры(u,v), свет
        drawVertex(vertexConsumer, matrix, -0.5f, -0.5f, 0, 0, 1, packedLight);
        drawVertex(vertexConsumer, matrix, 0.5f, -0.5f, 0, 1, 1, packedLight);
        drawVertex(vertexConsumer, matrix, 0.5f, 0.5f, 0, 1, 0, packedLight);
        drawVertex(vertexConsumer, matrix, -0.5f, 0.5f, 0, 0, 0, packedLight);

        poseStack.popPose();
    }

    private void drawVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, int light) {
        builder.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(655360) // Стандартное наложение
                .uv2(light)
                .normal(0, 0, 1)
                .endVertex();
    }

    private ResourceLocation getOrCreateTexture(String id, byte[] pixelData) {
        if (!TEXTURE_CACHE.containsKey(id)) {
            // Создаем новую текстуру 128x128
            DynamicTexture texture = new DynamicTexture(128, 128, true);
            ResourceLocation location = new ResourceLocation("graffitiartist", "dynamic/" + id.toLowerCase().replace(" ", "_"));

            Minecraft.getInstance().getTextureManager().register(location, texture);
            TEXTURE_CACHE.put(id, texture);
            LOCATION_CACHE.put(id, location);
        }

        DynamicTexture texture = TEXTURE_CACHE.get(id);
        updateTextureContent(texture, pixelData);

        return LOCATION_CACHE.get(id);
    }

    private void updateTextureContent(DynamicTexture texture, byte[] pixels) {
        NativeImage image = texture.getPixels();
        if (image == null) return;

        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                int colorIndex = pixels[y * 128 + x] & 0xFF;
                // ВАЖНО: NativeImage использует формат ABGR (не ARGB!)
                int color = (colorIndex == 0) ? 0x00000000 : 0xFFFFFFFF; // Пока просто Ч/Б для теста
                image.setPixelRGBA(x, y, color);
            }
        }
        texture.upload(); // Отправляем данные в видеокарту
    }
}