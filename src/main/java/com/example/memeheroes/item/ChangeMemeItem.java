package com.example.memeheroes.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ChangeMemeItem extends Item {

    public ChangeMemeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // 客户端：检查冷却后打开选梗 GUI（冷却数据 vanilla 已同步到客户端）
            if (!player.getCooldowns().isOnCooldown(this)) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> com.example.memeheroes.client.ClientMemeScreenHandler::openItemScreen);
            }
        }
        // 不消耗物品；冷却在 C2S 包服务端处理时添加（避免 Esc 关屏却被冷却）
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
