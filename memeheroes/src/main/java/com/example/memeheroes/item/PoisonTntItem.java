package com.example.memeheroes.item;

import com.example.memeheroes.entity.PoisonTntProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PoisonTntItem extends Item {
    public static final int COOLDOWN_TICKS = 600;

    public PoisonTntItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            PoisonTntProjectile projectile = new PoisonTntProjectile(level, player);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
            
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        
        return InteractionResultHolder.success(itemStack);
    }
}