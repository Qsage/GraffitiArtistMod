package com.qsage.graffitiartistmod.common.registration;

import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    // 1. Создаем регистратор для типов Block Entity (сущностей блока)
    // Используем MODID из твоего главного класса
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "graffitiartist");

    // 2. Регистрируем саму сущность GraffitiBlockEntity
    // Привязываем её к конструктору класса и к нашему блоку из ModBlocks
    public static final RegistryObject<BlockEntityType<GraffitiBlockEntity>> GRAFFITI_BE =
            BLOCK_ENTITIES.register("graffiti_be", () ->
                    BlockEntityType.Builder.of(
                            GraffitiBlockEntity::new,
                            GraffitiBlocks.GRAFFITI_BLOCK.get() // <-- Здесь берем блок из ModBlocks
                    ).build(null)
            );

    // Метод для инициализации регистрации в главном классе мода
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}