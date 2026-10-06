package com.example.memeheroes.item;

import com.example.memeheroes.config.MemeDamageConfig;
import com.example.memeheroes.entity.ModEntities;
import com.example.memeheroes.entity.WatermelonProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 野兽先辈 · 一技能：114514 投掷
 *
 * 行为完全参照华强买瓜的西瓜投掷：
 * 伤害 8（可通过 /changeMemeDamage yaju.projectile_114514 调整）| 无视重力 | 冷却 5 秒
 * 范围伤害 5×5 | 存活 1.5 秒 | 速度与西瓜一致 | 渲染 114514.gltf（原始模型的 1/16）
 */
public class Item114514 extends Item {
    public static final int COOLDOWN_TICKS = 100; // 5 秒
    private static final String DAMAGE_KEY = "yaju.projectile_114514";
    private static final float SCALE = 0.0625F;       // glTF 原始模型的 1/16（在 1/8 基础上再减半）
    private static final float SPEED_MULTIPLIER = 2.0F; // 与西瓜一致
    private static final float AREA_SIZE = 5.0F;      // 击中后范围伤害 5×5
    private static final int LIFETIME_TICKS = 30;     // 1.5 秒

    public Item114514(Properties properties) {
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
            // 复用西瓜抛射物逻辑，但绑定 114514 的实体类型（渲染不同模型）
            WatermelonProjectile projectile = new WatermelonProjectile(
                    ModEntities.PROJECTILE_114514.get(), level, player, damage, SCALE, AREA_SIZE, LIFETIME_TICKS);
            float velocity = (float) (player.getSpeed() * 10.0 * SPEED_MULTIPLIER);
            if (velocity < 0.5F) velocity = 1.5F;
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
