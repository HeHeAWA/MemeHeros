package com.example.memeheroes.item;

import com.example.memeheroes.entity.FallingGoldSword;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RainGoldSwordItem extends Item {
    public static final int COOLDOWN_TICKS = 300;

    public RainGoldSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            double centerX = player.getX();
            double centerY = player.getY();
            double centerZ = player.getZ();
            
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    FallingGoldSword sword = new FallingGoldSword(level, centerX + dx, centerY + 10, centerZ + dz, player);
                    level.addFreshEntity(sword);
                }
            }
            
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.success(itemStack);
    }
}