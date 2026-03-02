package com.qsage.graffitiartistmod.common.network.packet;

import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncGraffiti {
    private final BlockPos pos;
    private final byte[] pixels;

    public PacketSyncGraffiti(BlockPos pos, byte[] pixels) {
        this.pos = pos;
        this.pixels = pixels;
    }

    // Чтение из буфера (декодирование)
    public PacketSyncGraffiti(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.pixels = buf.readByteArray();
    }

    // Запись в буфер (кодирование)
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeByteArray(this.pixels);
    }

    // Обработка полученного пакета
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        // Внутри PacketSyncGraffiti
        context.enqueueWork(() -> {
            // МЫ НА КЛИЕНТЕ
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            if (be instanceof GraffitiBlockEntity graffiti) {
                // Копируем пришедшие пиксели в клиентский BlockEntity
                System.arraycopy(pixels, 0, graffiti.getPixels(), 0, pixels.length);
                graffiti.markDirtyForRenderer(); // Создай такой метод, который ставит dirty = true
            }
        });
        return true;
    }
}