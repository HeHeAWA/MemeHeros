package com.example.memeheroes;

import com.example.memeenv.api.MemeBridge;
import com.example.memeheroes.entity.ModEntities;
import com.example.memeheroes.item.ModItems;
import com.example.memeheroes.item.ModTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod(MemeHeroes.MOD_ID)
public class MemeHeroes {
    public static final String MOD_ID = "memeheroes";

    public MemeHeroes() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModEntities.register(eventBus);
        ModTabs.register(eventBus);

        // 在构造函数中即可注册，MemeBridge 存的是 Supplier<Item>，延迟 resolve。
        registerMemes();

        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    private final void register(int id, String nameId, Supplier<Item>... items) {
        MemeBridge.register(id, nameId, MOD_ID, items);
    }

    private void registerMemes() {
        register(1, "paoye", ModItems.PAOYE_TNT, ModItems.POISON_TNT);
        register(2, "netizen", ModItems.STONE, ModItems.FEATHER_SPEED);
        register(3, "shachang", ModItems.GOLD_SWORD, ModItems.RAIN_GOLD_SWORD);
        register(4, "hunter", ModItems.HUNTER, ModItems.CHARGE_HUNTER, () -> Items.SPYGLASS);
        register(5, "jiege", ModItems.BEER, ModItems.JIECHU_SEAL);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }
}
