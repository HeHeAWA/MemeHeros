package com.example.memeenv.item;

import com.example.memeenv.MemeEnv;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 环境 MOD 的创造栏：显示换梗道具与菜单道具。
 * （梗技能物品由 memeheroes 的创造栏提供。）
 */
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MemeEnv.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MEME_ENV_TAB =
            CREATIVE_MODE_TABS.register("meme_env", () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.literal("梗明星环境"))
                    .icon(() -> new ItemStack(ModItems.CHANGE_MEME.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CHANGE_MEME.get());
                        output.accept(ModItems.MENU.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
