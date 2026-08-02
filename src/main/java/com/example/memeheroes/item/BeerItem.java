package com.example.memeheroes.item;

import com.example.memeheroes.entity.BeerBottleProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BeerItem extends Item {
    public static final int COOLDOWN_TICKS = 100;

    public BeerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            Vec3 lookDirection = player.getLookAngle().normalize();
            BeerBottleProjectile projectile = new BeerBottleProjectile(level, player, lookDirection);
            level.addFreshEntity(projectile);
            
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.success(itemStack);
    }
}