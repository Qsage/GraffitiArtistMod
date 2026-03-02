package com.qsage.graffitiartistmod;

import com.mojang.logging.LogUtils;
import com.qsage.graffitiartistmod.client.renderer.GraffitiBlockEntityRenderer;
import com.qsage.graffitiartistmod.common.network.ModMessages;
import com.qsage.graffitiartistmod.common.registration.ModBlockEntities;
import com.qsage.graffitiartistmod.common.registration.ModBlocks;
import com.qsage.graffitiartistmod.common.registration.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(GraffitiArtistMod.MOD_ID)
public class GraffitiArtistMod {
    public static final String MOD_ID = "graffitiartistmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    // ВОТ ЗДЕСЬ ДОЛЖНО БЫТЬ ОБЪЯВЛЕНИЕ:
    // 1. Реестр для вкладок (мы его уже добавили ранее)
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> GRAFFITI_TAB = CREATIVE_MODE_TABS.register("graffiti_tab", () -> CreativeModeTab.builder()
            .icon(() -> ModBlocks.GRAFFITI_BLOCK.get().asItem().getDefaultInstance())
            .title(net.minecraft.network.chat.Component.translatable("itemGroup.graffiti_tab"))
            .displayItems((parameters, output) -> {
                // Использование .get() здесь безопасно, так как метод вызывается только при открытии инвентаря
                // Но важно убедиться, что у блока ЕСТЬ соответствующий Item
                output.accept(ModBlocks.GRAFFITI_BLOCK.get());

                // Если у тебя зарегистрирован баллончик в ModItems:
                output.accept(ModItems.SPRAY_CAN.get());
            }).build());

    @SuppressWarnings("removal")
    public GraffitiArtistMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // ПОРЯДОК ОЧЕНЬ ВАЖЕН:
        ModBlocks.register(modEventBus);        // 1. Сначала блоки
        ModItems.register(modEventBus);         // 2. Потом предметы
        ModBlockEntities.register(modEventBus);  // 3. Потом сущности блоков
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        ModMessages.register(); // Убедись, что сетевые сообщения тоже тут

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerRenderers);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Привязываем наш кастомный рендерер к сущности блока
        event.registerBlockEntityRenderer(ModBlockEntities.GRAFFITI_BE.get(),
                GraffitiBlockEntityRenderer::new);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Graffiti Artist: Common Setup");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SPRAY_CAN.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Graffiti Artist: Server Started");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Graffiti Artist: Client Setup");
        }

        // ДОБАВЬ ЭТОТ МЕТОД ДЛЯ РЕНДЕРА:
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.GRAFFITI_BE.get(), GraffitiBlockEntityRenderer::new);
        }
    }


}