package com.example.memeheroes.client;

import com.example.memeheroes.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "memeheroes", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.PAOYE_TNT_PROJECTILE.get(), PaoyeTntRenderer::new);
        event.registerEntityRenderer(ModEntities.POISON_TNT_PROJECTILE.get(), PoisonTntRenderer::new);
        event.registerEntityRenderer(ModEntities.STONE_PROJECTILE.get(), StoneRenderer::new);
        event.registerEntityRenderer(ModEntities.GOLD_SWORD_PROJECTILE.get(), GoldSwordRenderer::new);
        event.registerEntityRenderer(ModEntities.FALLING_GOLD_SWORD.get(), FallingGoldSwordRenderer::new);
        event.registerEntityRenderer(ModEntities.GOLD_NUGGET_PROJECTILE.get(), GoldNuggetRenderer::new);
        event.registerEntityRenderer(ModEntities.DELAYED_GOLD_NUGGET.get(), DelayedGoldNuggetRenderer::new);
        event.registerEntityRenderer(ModEntities.BEER_BOTTLE_PROJECTILE.get(), BeerBottleRenderer::new);
    }
}