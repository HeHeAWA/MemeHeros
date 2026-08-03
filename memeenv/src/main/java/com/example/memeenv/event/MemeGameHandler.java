package com.example.memeenv.event;

import com.example.memeenv.item.ModItems;
import com.example.memeenv.network.ModMessages;
import com.example.memeenv.network.S2COpenMemeScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "memeenv", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MemeGameHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp),
                new S2COpenMemeScreenPacket());
        ensureEssentialItems(sp);
        applyGlobalEffects(sp);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        Player player = event.player;

        if (player.tickCount % 60 == 0) {
            ensureEssentialItems(player);
        }
        if (player.tickCount % 100 == 0) {
            applyGlobalEffects(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player p = event.getEntity();
        if (!p.level().isClientSide) {
            ensureEssentialItems(p);
            applyGlobalEffects(p);
        }
    }

    private static void ensureEssentialItems(Player player) {
        Inventory inv = player.getInventory();
        boolean hasChangeMeme = false;
        boolean hasMenu = false;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(ModItems.CHANGE_MEME.get())) {
                hasChangeMeme = true;
            } else if (stack.is(ModItems.MENU.get())) {
                hasMenu = true;
            }
        }
        if (!hasChangeMeme) {
            giveItem(inv, player, new ItemStack(ModItems.CHANGE_MEME.get()));
        }
        if (!hasMenu) {
            giveItem(inv, player, new ItemStack(ModItems.MENU.get()));
        }
    }

    private static void giveItem(Inventory inv, Player player, ItemStack stack) {
        if (!inv.add(stack)) {
            player.drop(stack, false);
        }
    }

    private static void applyGlobalEffects(Player player) {
        MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);
        if (nv == null || nv.getAmplifier() < 0) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                    MobEffectInstance.INFINITE_DURATION, 0, false, false));
        }
        MobEffectInstance jp = player.getEffect(MobEffects.JUMP);
        if (jp == null || jp.getAmplifier() < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP,
                    MobEffectInstance.INFINITE_DURATION, 7, false, false));
        }
    }
}
