package com.example.memeheroes.item;

import com.example.memeheroes.MemeHeroes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 本体 MOD 的创造栏：只显示 10 个梗技能物品。
 * 环境 MOD（memeenv）的换梗/菜单道具由 memeenv 自己的创造栏提供，
 * 这样 memeheroes 可完全独立运行，不引用任何 memeenv 的类。
 */
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
                        output.accept(ModItems.WATERMELON.get());
                        output.accept(ModItems.BIG_WATERMELON.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
