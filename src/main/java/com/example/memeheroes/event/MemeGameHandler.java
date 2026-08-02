package com.example.memeheroes.event;

import com.example.memeheroes.item.ModItems;
import com.example.memeheroes.network.ModMessages;
import com.example.memeheroes.network.S2COpenMemeScreenPacket;
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

@Mod.EventBusSubscriber(modid = "memeheroes", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MemeGameHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        // 每次登录都弹出选梗 GUI
        ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp),
                new S2COpenMemeScreenPacket());
        // 立即补一次必备道具（换梗道具 + 菜单）
        ensureEssentialItems(sp);
        // 立即施加全局效果
        applyGlobalEffects(sp);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        Player player = event.player;

        // 每 3 秒检查必备道具
        if (player.tickCount % 60 == 0) {
            ensureEssentialItems(player);
        }
        // 每 5 秒刷新全局效果（夜视 + 跳跃提升8），duration 300 保证不闪烁
        if (player.tickCount % 100 == 0) {
            applyGlobalEffects(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player p = event.getEntity();
        if (!p.level().isClientSide) {
            ensureEssentialItems(p);
            // 复活后立即拥有全局效果
            applyGlobalEffects(p);
        }
    }

    private static void ensureEssentialItems(Player player) {
        Inventory inv = player.getInventory();
        boolean hasChangeMeme = false;
        boolean hasMenu = false;
        // 遍历全部槽位（主背包 + 盔甲 + 副手）
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
        // 夜视（仅当不存在或剩余时间不足时刷新，避免覆盖更高等级）
        if (!player.hasEffect(MobEffects.NIGHT_VISION)
                || player.getEffect(MobEffects.NIGHT_VISION).getDuration() < 200) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
        // 跳跃提升8（amplifier 7 = level 8）
        if (!player.hasEffect(MobEffects.JUMP)
                || player.getEffect(MobEffects.JUMP).getDuration() < 200) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 7, false, false));
        }
    }
}
