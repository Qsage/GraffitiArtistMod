package com.qsage.graffitiartistmod.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.qsage.graffitiartistmod.common.registration.ModBlockEntities;

public class GraffitiBlockEntity extends BlockEntity {

    // Массив пикселей 128x128 = 16384 байта
    private final byte[] pixels = new byte[128 * 128];

    public GraffitiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAFFITI_BE.get(), pos, state);

        // Временный тест: закрасим центр, чтобы проверить рендер
        // fillTestPattern();
    }

    // --- ЛОГИКА ДАННЫХ ---

    public byte[] getPixels() {
        return this.pixels;
    }

    public void setPixel(int x, int y, byte colorIndex) {
        if (x >= 0 && x < 128 && y >= 0 && y < 128) {
            this.pixels[y * 128 + x] = colorIndex;
            this.setChanged(); // Помечаем, что данные блока изменились

            // Важно: заставляем мир перерисовать блок на клиенте
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    // --- СОХРАНЕНИЕ И ЗАГРУЗКА (NBT) ---

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putByteArray("pixels", pixels);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("pixels")) {
            byte[] saved = tag.getByteArray("pixels");
            // Копируем данные из сохранения в наш массив
            System.arraycopy(saved, 0, pixels, 0, Math.min(saved.length, pixels.length));
        }
    }

    // --- СИНХРОНИЗАЦИЯ С КЛИЕНТОМ ---
    // Эти методы нужны, чтобы клиент узнал о данных в BlockEntity при заходе в мир

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}