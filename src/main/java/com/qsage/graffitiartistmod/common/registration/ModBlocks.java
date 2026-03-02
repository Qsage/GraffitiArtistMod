package com.qsage.graffitiartistmod.common.registration;

import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.block.GraffitiBlock; // Импортируем сам класс блока
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GraffitiArtistMod.MOD_ID);

    public static final RegistryObject<Block> GRAFFITI_BLOCK = BLOCKS.register("graffiti_block",
            () -> new GraffitiBlock(BlockBehaviour.Properties.copy(Blocks.AIR)
                    .strength(0.1f)
                    .noOcclusion()
                    .noCollission()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}