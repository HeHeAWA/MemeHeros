package com.example.memeheroes.item;

import com.example.memeheroes.MemeHeroes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MemeHeroes.MOD_ID);
    
    public static final RegistryObject<CreativeModeTab> MEME_HEROES_TAB = 
            CREATIVE_MODE_TABS.register("meme_heroes", () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.literal("梗明星大乱斗"))
                    .icon(() -> new ItemStack(ModItems.PAOYE_TNT.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.PAOYE_TNT.get());
                        output.accept(ModItems.POISON_TNT.get());
                        output.accept(ModItems.STONE.get());
                        output.accept(ModItems.FEATHER_SPEED.get());
                        output.accept(ModItems.GOLD_SWORD.get());
                        output.accept(ModItems.RAIN_GOLD_SWORD.get());
                        output.accept(ModItems.HUNTER.get());
                        output.accept(ModItems.CHARGE_HUNTER.get());
                        output.accept(ModItems.BEER.get());
                        output.accept(ModItems.JIECHU_SEAL.get());
                        output.accept(ModItems.CHANGE_MEME.get());
                        output.accept(ModItems.MENU.get());
                    })
                    .build());
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}