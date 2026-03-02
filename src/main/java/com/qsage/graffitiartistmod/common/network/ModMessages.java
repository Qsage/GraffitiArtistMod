package com.qsage.graffitiartistmod.common.network;

import com.qsage.graffitiartistmod.GraffitiArtistMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    @SuppressWarnings("removal") // Подавляем предупреждение для версии 1.20.1
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                // В 1.20.1 этот конструктор обязателен для работы
                .named(new ResourceLocation(GraffitiArtistMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Регистрируем наш пакет синхронизации
        net.messageBuilder(PacketSyncGraffiti.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSyncGraffiti::new)
                .encoder(PacketSyncGraffiti::toBytes)
                .consumerMainThread(PacketSyncGraffiti::handle)
                .add();

        // В методе register() класса ModMessages:
        net.messageBuilder(PacketDrawPixel.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketDrawPixel::new)
                .encoder(PacketDrawPixel::toBytes)
                .consumerMainThread(PacketDrawPixel::handle)
                .add();
    }

    // Метод для отправки всем игрокам (например, когда кто-то дорисовал)
    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}