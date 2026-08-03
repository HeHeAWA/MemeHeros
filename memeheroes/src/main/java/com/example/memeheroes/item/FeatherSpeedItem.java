package com.example.memeheroes.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FeatherSpeedItem extends Item {
    public static final int COOLDOWN_TICKS = 400;

    public FeatherSpeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 19));
            
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.success(itemStack);
    }
}