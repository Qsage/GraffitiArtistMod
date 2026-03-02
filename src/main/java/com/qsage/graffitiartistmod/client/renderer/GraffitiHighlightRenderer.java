package com.qsage.graffitiartistmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.block.GraffitiBlock;
import com.qsage.graffitiartistmod.common.item.SprayCanItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.WEST;

@Mod.EventBusSubscriber(modid = GraffitiArtistMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GraffitiHighlightRenderer {

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        // 1. Проверяем, держит ли игрок баллончик
        if (!(mc.player.getMainHandItem().getItem() instanceof SprayCanItem)) return;

        BlockHitResult hit = event.getTarget();
        BlockPos pos = hit.getBlockPos();

        // 2. Проверяем, смотрим ли мы на наш блок граффити
        if (mc.level.getBlockState(pos).getBlock() instanceof GraffitiBlock) {
            // Отменяем стандартную жирную рамку блока
            event.setCanceled(true);

            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();

            // Получаем точные координаты клика относительно угла блока (0.0 - 1.0)
            double x = hit.getLocation().x - pos.getX();
            double y = hit.getLocation().y - pos.getY();
            double z = hit.getLocation().z - pos.getZ();

            // Вычисляем номер пикселя (0-15)
            int px = 0;
            int py = 0;

            Direction side = hit.getDirection();
            // Математика такая же, как при рисовании
            switch (side) {
                case NORTH -> { px = (int)((1.0 - x) * 16); py = (int)((1.0 - y) * 16); }
                case SOUTH -> { px = (int)(x * 16); py = (int)((1.0 - y) * 16); }
                case WEST  -> { px = (int)(z * 16); py = (int)((1.0 - y) * 16); }
                case EAST  -> { px = (int)((1.0 - z) * 16); py = (int)((1.0 - y) * 16); }
            }

            // Рендерим маленькую рамку
            poseStack.pushPose();
            // Сдвигаем PoseStack так, чтобы он соответствовал координатам мира относительно камеры
            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

            drawPixelOutline(poseStack, event.getMultiBufferSource().getBuffer(RenderType.lines()), px, py, side);

            poseStack.popPose();
        }
    }

    private static void drawPixelOutline(PoseStack poseStack, VertexConsumer consumer, int px, int py, Direction side) {
        // Размер одного пикселя в координатах Minecraft
        float size = 1f / 16f;
        float xStart = 0, yStart = 0, zStart = 0;
        float xEnd = 0, yEnd = 0, zEnd = 0;

        // Определяем положение рамки в зависимости от стороны блока
        switch (side) {
            case NORTH, SOUTH -> {
                float z = (side == NORTH) ? -0.001f : 1.001f;
                xStart = (side == NORTH) ? (15 - px) * size : px * size;
                yStart = (15 - py) * size;
                LevelRenderer.renderLineBox(poseStack, consumer, xStart, yStart, z, xStart + size, yStart + size, z, 1f, 1f, 1f, 0.8f);
            }
            case WEST, EAST -> {
                float x = (side == WEST) ? -0.001f : 1.001f;
                zStart = (side == WEST) ? px * size : (15 - px) * size;
                yStart = (15 - py) * size;
                LevelRenderer.renderLineBox(poseStack, consumer, x, yStart, zStart, x, yStart + size, zStart + size, 1f, 1f, 1f, 0.8f);
            }
        }
    }
}