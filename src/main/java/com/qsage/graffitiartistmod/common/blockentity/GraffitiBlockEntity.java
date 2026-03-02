package com.qsage.graffitiartistmod.common.blockentity;

import com.qsage.graffitiartistmod.common.registration.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GraffitiBlockEntity extends BlockEntity {
    private final byte[] pixels = new byte[256]; // 16x16
    private boolean dirty = true;

    public void markDirtyForRenderer() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markClean() {
        this.dirty = false;
    }

    public GraffitiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAFFITI_BE.get(), pos, state);
    }

    public void setPixel(int x, int y, byte color) {
        System.out.println("Pixel set at: " + x + ", " + y); // Если этого нет в консоли - пакет не дошел
        if (x >= 0 && x < 16 && y >= 0 && y < 16) {
            this.pixels[y * 16 + x] = color;
            this.setChanged();
            this.dirty = true;
        }
    }

    public byte[] getPixels() { return pixels; }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putByteArray("Pixels", pixels);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Pixels")) {
            byte[] saved = tag.getByteArray("Pixels");
            System.arraycopy(saved, 0, pixels, 0, Math.min(saved.length, pixels.length));
        }
    }


    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata(); // Сохраняем данные для отправки клиенту
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // Именно этот метод заставляет клиент "увидеть" правильный FACING
        return ClientboundBlockEntityDataPacket.create(this);
    }
}