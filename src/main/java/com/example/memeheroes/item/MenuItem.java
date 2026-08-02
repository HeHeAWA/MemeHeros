package com.example.memeheroes.item;

import com.example.memeheroes.network.C2SMenuRequestPacket;
import com.example.memeheroes.network.ModMessages;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MenuItem extends Item {

    public MenuItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // 客户端发送请求，服务端返回信息后由 S2C 包打开菜单界面
            ModMessages.INSTANCE.sendToServer(new C2SMenuRequestPacket());
        }
        // 不消耗物品，无冷却
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
