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
 * 华强买瓜 · 二技能：华强裂瓜
 *
 * 伤害 114（可通过 /changeMemeDamage huaqiang.big_watermelon 调整）| 无视重力 | 冷却 18 秒
 * 范围伤害 7×7 | 存活 2.5 秒
 */
public class BigWatermelonItem extends Item {
    public static final int COOLDOWN_TICKS = 360; // 18 秒
    private static final String DAMAGE_KEY = "huaqiang.big_watermelon";
    private static final float SCALE = 0.625F;       // 原 2.5 的 1/4
    private static final float SPEED_MULTIPLIER = 1.67F;  // 减速 1.5 倍
    private static final float AREA_SIZE = 7.0F;     // 7×7
    private static final int LIFETIME_TICKS = 50;    // 2.5 秒

    public BigWatermelonItem(Properties properties) {
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
            if (velocity < 0.5F) velocity = 1.3F;
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
