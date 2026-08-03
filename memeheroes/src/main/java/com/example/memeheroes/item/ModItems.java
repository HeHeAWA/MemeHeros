package com.example.memeheroes.item;

import com.example.memeheroes.MemeHeroes;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 本体 MOD 只注册梗技能物品。
 * 换梗道具 (CHANGE_MEME) 和 菜单 (MENU) 由环境 MOD memeenv 注册。
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MemeHeroes.MOD_ID);

    public static final RegistryObject<Item> PAOYE_TNT = ITEMS.register("paoye_tnt",
            () -> new PaoyeTntItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> POISON_TNT = ITEMS.register("poison_tnt",
            () -> new PoisonTntItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> STONE = ITEMS.register("stone",
            () -> new StoneItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> FEATHER_SPEED = ITEMS.register("feather_speed",
            () -> new FeatherSpeedItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> GOLD_SWORD = ITEMS.register("gold_sword",
            () -> new GoldSwordItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RAIN_GOLD_SWORD = ITEMS.register("rain_gold_sword",
            () -> new RainGoldSwordItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HUNTER = ITEMS.register("hunter",
            () -> new HunterItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CHARGE_HUNTER = ITEMS.register("charge_hunter",
            () -> new ChargeHunterItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BEER = ITEMS.register("beer",
            () -> new BeerItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> JIECHU_SEAL = ITEMS.register("jiechu_seal",
            () -> new JiechuSealItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
