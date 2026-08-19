package com.example.memeheroes.item;

import com.example.memeheroes.entity.WatermelonProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 华强买瓜 · 一技能：西瓜投掷
 *
 * 伤害 7 | 速度 = 玩家最大速度 × 2 | 无视重力 | 大小 = 玩家 × 1.5 | 冷却 5 秒
 */
public class WatermelonItem extends Item {
    public static final int COOLDOWN_TICKS = 100; // 5 秒
    private static final float DAMAGE = 7.0F;
    private static final float SCALE = 1.5F;
    private static final float SPEED_MULTIPLIER = 2.0F;

    public WatermelonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (!level.isClientSide) {
            WatermelonProjectile projectile = new WatermelonProjectile(level, player, DAMAGE, SCALE);
            // 玩家最大速度 × 2：getSpeed() 默认 0.1，×10 转为抛射物速度基数
            float velocity = (float) (player.getSpeed() * 10.0 * SPEED_MULTIPLIER);
            if (velocity < 0.5F) velocity = 1.5F; // 兜底
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
