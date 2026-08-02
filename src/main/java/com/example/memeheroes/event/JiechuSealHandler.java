package com.example.memeheroes.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "memeheroes", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JiechuSealHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;

        int time = player.getPersistentData().getInt("jiechu_seal_time");

        if (time > 0) {
            // 服务端递减时间（客户端粒子自己单独生成）
            player.getPersistentData().putInt("jiechu_seal_time", time - 1);

            // 扩大检测范围，使碰撞更可靠
            AABB boundingBox = player.getBoundingBox().inflate(1.5D);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, boundingBox)) {
                if (entity == player) continue;
                if (entity.isDeadOrDying()) continue;
                // 跳过正在无敌帧的实体，避免浪费伤害
                if (entity.invulnerableTime > 0) continue;

                double distance = entity.distanceTo(player);
                if (distance <= 2.0D) {
                    // 使用玩家攻击伤害源，正确计入战斗/复活机制
                    entity.hurt(player.damageSources().playerAttack(player), 50.0F);
                }
            }

            ((ServerLevel) level).sendParticles(ParticleTypes.FLAME,
                    player.getX(), player.getY() + 0.5D, player.getZ(),
                    10, 0.1D, 0.2D, 0.1D, 0.05D);
            ((ServerLevel) level).sendParticles(ParticleTypes.CRIMSON_SPORE,
                    player.getX(), player.getY() + 0.5D, player.getZ(),
                    6, 0.08D, 0.13D, 0.08D, 0.01D);
        }
    }

    /**
     * 客户端粒子单独处理（不依赖服务端 tick 包，确保视觉稳定）
     */
    @SubscribeEvent
    public static void onClientPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (!level.isClientSide) return;

        int time = player.getPersistentData().getInt("jiechu_seal_time");
        if (time > 0) {
            spawnRedParticles(player);
        }
    }

    private static void spawnRedParticles(Player player) {
        Level level = player.level();
        double x = player.getX();
        double y = player.getY() + 0.5D;
        double z = player.getZ();

        int particleCount = 16;
        for (int i = 0; i < particleCount; i++) {
            double angle = (Math.PI * 2.0D * i) / particleCount;
            double px = x + Math.cos(angle) * 0.25D;
            double py = y + (level.random.nextDouble() - 0.5D) * 0.4D;
            double pz = z + Math.sin(angle) * 0.25D;

            level.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0D, 0.2D, 0.0D);
        }

        for (int i = 0; i < 8; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0D;
            double px = x + Math.cos(angle) * 0.18D;
            double py = y + level.random.nextDouble() * 0.4D;
            double pz = z + Math.sin(angle) * 0.18D;

            level.addParticle(ParticleTypes.CRIMSON_SPORE, px, py, pz, 0.0D, 0.1D, 0.0D);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            // 仅当玩家加入（登录）时重置，不影响中途的技能状态
            player.getPersistentData().putInt("jiechu_seal_time", 0);
        }
    }
}
