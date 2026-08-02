package com.example.memeheroes.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class JiechuSealItem extends Item {
    public static final int COOLDOWN_TICKS = 700;
    public static final int EFFECT_DURATION = 400;

    public JiechuSealItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        player.getPersistentData().putInt("jiechu_seal_time", EFFECT_DURATION);
        
        if (!level.isClientSide) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.success(itemStack);
    }
}