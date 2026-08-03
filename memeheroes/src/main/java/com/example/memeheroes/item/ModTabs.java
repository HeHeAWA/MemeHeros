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
                    .icon(() -> new ItemStack(com.example.memeheroes.item.ModItems.PAOYE_TNT.get()))
                    .displayItems((parameters, output) -> {
                        // 本体 MOD 的 10 个技能物品
                        output.accept(com.example.memeheroes.item.ModItems.PAOYE_TNT.get());
                        output.accept(com.example.memeheroes.item.ModItems.POISON_TNT.get());
                        output.accept(com.example.memeheroes.item.ModItems.STONE.get());
                        output.accept(com.example.memeheroes.item.ModItems.FEATHER_SPEED.get());
                        output.accept(com.example.memeheroes.item.ModItems.GOLD_SWORD.get());
                        output.accept(com.example.memeheroes.item.ModItems.RAIN_GOLD_SWORD.get());
                        output.accept(com.example.memeheroes.item.ModItems.HUNTER.get());
                        output.accept(com.example.memeheroes.item.ModItems.CHARGE_HUNTER.get());
                        output.accept(com.example.memeheroes.item.ModItems.BEER.get());
                        output.accept(com.example.memeheroes.item.ModItems.JIECHU_SEAL.get());
                        // 环境 MOD 的必备道具（换梗 + 菜单）
                        output.accept(com.example.memeenv.item.ModItems.CHANGE_MEME.get());
                        output.accept(com.example.memeenv.item.ModItems.MENU.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
