package com.qsage.graffitiartistmod.common.network.packet;

import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import com.qsage.graffitiartistmod.common.network.ModMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDrawPixel {
    private final BlockPos pos; // Позиция блока-холста
    private final int x;        // Координата X пикселя (0-15)
    private final int y;        // Координата Y пикселя (0-15)
    private final byte color;   // Цвет (индекс в палитре)

    public PacketDrawPixel(BlockPos pos, int x, int y, byte color) {
        this.pos = pos;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    // Декодер: читает данные из "сетевого потока"
    public PacketDrawPixel(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.color = buf.readByte();
    }

    // Энкодер: превращает данные в байты для отправки по сети
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeByte(color);
    }

    // Обработчик: что делать, когда пакет пришел на сервер
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        // Внутри PacketDrawPixel
        context.enqueueWork(() -> {
            ServerLevel level = context.getSender().serverLevel();
            if (level.getBlockEntity(pos) instanceof GraffitiBlockEntity be) {
                be.setPixel(x, y, color);
                // ВАЖНО: Отправляем пакет синхронизации обратно клиентам!
                ModMessages.sendToClients(new PacketSyncGraffiti(pos, be.getPixels()));
            }
        });
        return true;
    }
}