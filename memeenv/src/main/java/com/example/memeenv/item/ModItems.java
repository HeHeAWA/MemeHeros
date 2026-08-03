package com.example.memeenv.item;

import com.example.memeenv.MemeEnv;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MemeEnv.MOD_ID);

    public static final RegistryObject<Item> CHANGE_MEME = ITEMS.register("change_meme",
            () -> new ChangeMemeItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MENU = ITEMS.register("menu",
            () -> new MenuItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
