package com.example.memeheroes.item;

import com.example.memeheroes.entity.DelayedGoldNugget;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChargeHunterItem extends Item {
    public static final int COOLDOWN_TICKS = 300;

    public ChargeHunterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            Vec3 lookDirection = player.getLookAngle().normalize();
            
            double centerX = player.getX();
            double centerY = player.getY();
            double centerZ = player.getZ();
            
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    DelayedGoldNugget nugget = new DelayedGoldNugget(level, centerX + dx, centerY + 1, centerZ + dz, player, lookDirection);
                    level.addFreshEntity(nugget);
                }
            }
            
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.success(itemStack);
    }
}