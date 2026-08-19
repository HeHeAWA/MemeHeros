package com.example.memeheroes.item;

import com.example.memeheroes.entity.WatermelonProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 华强买瓜 · 二技能：华强裂瓜
 *
 * 伤害 114 | 速度 = 玩家最大速度 × 1.7 | 无视重力 | 大小 = 玩家 × 2.5 | 冷却 18 秒
 */
public class BigWatermelonItem extends Item {
    public static final int COOLDOWN_TICKS = 360; // 18 秒
    private static final float DAMAGE = 114.0F;
    private static final float SCALE = 2.5F;
    private static final float SPEED_MULTIPLIER = 1.7F;

    public BigWatermelonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            WatermelonProjectile projectile = new WatermelonProjectile(level, player, DAMAGE, SCALE);
            float velocity = (float) (player.getSpeed() * 10.0 * SPEED_MULTIPLIER);
            if (velocity < 0.5F) velocity = 1.3F; // 兜底
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
