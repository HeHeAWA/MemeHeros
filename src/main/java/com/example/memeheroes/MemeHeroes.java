package com.example.memeheroes;

import com.example.memeheroes.entity.ModEntities;
import com.example.memeheroes.item.ModItems;
import com.example.memeheroes.item.ModTabs;
import com.example.memeheroes.network.ModMessages;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MemeHeroes.MOD_ID)
public class MemeHeroes {
    public static final String MOD_ID = "memeheroes";

    public MemeHeroes() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.register(eventBus);
        ModEntities.register(eventBus);
        ModTabs.register(eventBus);
        
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }
}