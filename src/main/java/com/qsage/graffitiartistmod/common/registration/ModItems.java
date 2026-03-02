package com.qsage.graffitiartistmod.common.registration;

import com.qsage.graffitiartistmod.GraffitiArtistMod;
import com.qsage.graffitiartistmod.common.item.SprayCanItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GraffitiArtistMod.MOD_ID);

    // Сам баллончик
    public static final RegistryObject<Item> SPRAY_CAN = ITEMS.register("spray_can",
            () -> new SprayCanItem(new Item.Properties().stacksTo(1)));

    // Предмет блока холста (если он нужен в инвентаре)
    public static final RegistryObject<Item> GRAFFITI_BLOCK_ITEM = ITEMS.register("graffiti_block",
            () -> new BlockItem(ModBlocks.GRAFFITI_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}