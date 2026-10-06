package com.example.memeheroes.item;

import com.example.memeheroes.config.MemeDamageConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 野兽先辈 · 二技能：野兽咆哮
 *
 * 玩家周围 8×8×8 范围内所有生物受到 16 点伤害（可通过 /changeMemeDamage yaju.beast_cry 调整）。
 * 冷却 20 秒。
 * 对受到伤害的玩家发送私信："哼哼哼啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊"。
 */
public class BeastCryItem extends Item {
    public static final int COOLDOWN_TICKS = 400; // 20 秒
    private static final String DAMAGE_KEY = "yaju.beast_cry";
    private static final double RANGE = 8.0;      // 8×8×8 范围（边长 8）

    /** 私信内容 */
    private static final Component CRY_MESSAGE =
            Component.literal("哼哼哼啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");

    public BeastCryItem(Properties properties) {
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

            // 以玩家为中心 8×8×8 区域
            AABB box = AABB.ofSize(player.position(), RANGE, RANGE, RANGE);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);

            for (LivingEntity entity : entities) {
                if (entity == player) continue;
                if (!entity.isAlive()) continue;
                entity.hurt(player.damageSources().playerAttack(player), damage);
                // 仅对玩家目标发送咆哮私信
                if (entity instanceof Player target) {
                    target.sendSystemMessage(CRY_MESSAGE);
                }
            }

            // 视觉与音效：在玩家周围生成爆炸粒子环
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                        player.getX(), player.getY() + 1.0D, player.getZ(),
                        1, 0, 0, 0, 0);
                serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                        player.getX(), player.getY() + 1.0D, player.getZ(),
                        1, 0, 0, 0, 0);
            }
            level.playSound(null, player.blockPosition(),
                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.0F);

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
