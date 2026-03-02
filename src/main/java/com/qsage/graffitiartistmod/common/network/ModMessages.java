package com.qsage.graffitiartistmod.common.network;

import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.network.packet.PacketDrawPixel;
import com.qsage.graffitiartistmod.common.network.packet.PacketSyncGraffiti;
import net.minecraft.resources.ResourceLocation;
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

    @SuppressWarnings("removal")
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(GraffitiArtistMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Пакет от Сервера к Клиенту (Синхронизация всего холста)
        net.messageBuilder(PacketSyncGraffiti.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSyncGraffiti::new)
                .encoder(PacketSyncGraffiti::toBytes)
                .consumerMainThread(PacketSyncGraffiti::handle)
                .add();

        // Пакет от Клиента к Серверу (Рисование пикселя)
        // ВАЖНО: используем NetworkDirection.PLAY_TO_SERVER
        net.messageBuilder(PacketDrawPixel.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketDrawPixel::new)
                .encoder(PacketDrawPixel::toBytes)
                .consumerMainThread(PacketDrawPixel::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClients(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}