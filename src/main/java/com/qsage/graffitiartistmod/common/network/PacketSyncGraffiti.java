package com.qsage.graffitiartistmod.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
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
        context.enqueueWork(() -> {
            // Здесь будет логика обновления данных на клиенте
            // Мы найдем BlockEntity по координатам 'pos' и заменим в нем 'pixels'
        });
        return true;
    }
}