package com.example.memeheroes.item;

import com.example.memeheroes.config.MemeDamageConfig;
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
 * 伤害 7（可通过 /changeMemeDamage huaqiang.watermelon 调整）| 无视重力 | 冷却 5 秒
 * 范围伤害 5×5 | 存活 1.5 秒
 */
public class WatermelonItem extends Item {
    public static final int COOLDOWN_TICKS = 100; // 5 秒
    private static final String DAMAGE_KEY = "huaqiang.watermelon";
    private static final float SCALE = 0.375F;       // 原 1.5 的 1/4
    private static final float SPEED_MULTIPLIER = 2.0F;  // 减速 1.5 倍
    private static final float AREA_SIZE = 5.0F;     // 5×5
    private static final int LIFETIME_TICKS = 30;    // 1.5 秒

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
            float damage = MemeDamageConfig.getDamage(DAMAGE_KEY);
            WatermelonProjectile projectile = new WatermelonProjectile(level, player, damage, SCALE, AREA_SIZE, LIFETIME_TICKS);
            float velocity = (float) (player.getSpeed() * 10.0 * SPEED_MULTIPLIER);
            if (velocity < 0.5F) velocity = 1.5F;
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
