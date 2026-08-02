package com.example.memeheroes.entity;

import com.example.memeheroes.MemeHeroes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MemeHeroes.MOD_ID);

    public static final RegistryObject<EntityType<PaoyeTntProjectile>> PAOYE_TNT_PROJECTILE = ENTITIES.register(
            "paoye_tnt_projectile",
            () -> EntityType.Builder.<PaoyeTntProjectile>of(PaoyeTntProjectile::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("paoye_tnt_projectile")
    );
    
    public static final RegistryObject<EntityType<PoisonTntProjectile>> POISON_TNT_PROJECTILE = ENTITIES.register(
            "poison_tnt_projectile",
            () -> EntityType.Builder.<PoisonTntProjectile>of(PoisonTntProjectile::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("poison_tnt_projectile")
    );
    
    public static final RegistryObject<EntityType<StoneProjectile>> STONE_PROJECTILE = ENTITIES.register(
            "stone_projectile",
            () -> EntityType.Builder.<StoneProjectile>of(StoneProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("stone_projectile")
    );
    
    public static final RegistryObject<EntityType<GoldSwordProjectile>> GOLD_SWORD_PROJECTILE = ENTITIES.register(
            "gold_sword_projectile",
            () -> EntityType.Builder.<GoldSwordProjectile>of(GoldSwordProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("gold_sword_projectile")
    );
    
    public static final RegistryObject<EntityType<FallingGoldSword>> FALLING_GOLD_SWORD = ENTITIES.register(
            "falling_gold_sword",
            () -> EntityType.Builder.<FallingGoldSword>of(FallingGoldSword::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("falling_gold_sword")
    );
    
    public static final RegistryObject<EntityType<GoldNuggetProjectile>> GOLD_NUGGET_PROJECTILE = ENTITIES.register(
            "gold_nugget_projectile",
            () -> EntityType.Builder.<GoldNuggetProjectile>of(GoldNuggetProjectile::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("gold_nugget_projectile")
    );
    
    public static final RegistryObject<EntityType<DelayedGoldNugget>> DELAYED_GOLD_NUGGET = ENTITIES.register(
            "delayed_gold_nugget",
            () -> EntityType.Builder.<DelayedGoldNugget>of(DelayedGoldNugget::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("delayed_gold_nugget")
    );
    
    public static final RegistryObject<EntityType<BeerBottleProjectile>> BEER_BOTTLE_PROJECTILE = ENTITIES.register(
            "beer_bottle_projectile",
            () -> EntityType.Builder.<BeerBottleProjectile>of(BeerBottleProjectile::new, MobCategory.MISC)
                    .sized(0.3F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("beer_bottle_projectile")
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}