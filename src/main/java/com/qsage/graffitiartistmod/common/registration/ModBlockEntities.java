package com.qsage.graffitiartistmod.common.registration;

import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity; // Импорт
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GraffitiArtistMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<GraffitiBlockEntity>> GRAFFITI_BE =
            BLOCK_ENTITIES.register("graffiti_be", () ->
                    BlockEntityType.Builder.of(
                            GraffitiBlockEntity::new,
                            ModBlocks.GRAFFITI_BLOCK.get() // Ссылка на блок выше
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}