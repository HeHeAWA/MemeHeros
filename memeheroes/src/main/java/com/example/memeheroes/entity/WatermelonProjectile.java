package com.example.memeheroes.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

/**
 * 西瓜抛射物 —— 华强买瓜专属。
 *
 * 无视重力，可配置伤害（damage）、渲染缩放（scale）、范围伤害（areaSize）、存活时间（lifetimeTicks）。
 * scale 通过 SynchedEntityData 同步到客户端用于渲染。
 * damage/areaSize/lifetimeTicks 通过 IEntityAdditionalSpawnData 同步。
 */
public class WatermelonProjectile extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    private static final EntityDataAccessor<Float> SCALE =
            SynchedEntityData.defineId(WatermelonProjectile.class, EntityDataSerializers.FLOAT);

    private LivingEntity owner;
    private float damage = 7.0F;
    private float scale = 1.5F;
    private float areaSize = 5.0F;       // 伤害范围边长（5×5 或 7×7）
    private int lifetimeTicks = 140;    // 存活时间（7秒 = 140 tick）

    public WatermelonProjectile(EntityType<? extends WatermelonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WatermelonProjectile(Level level, LivingEntity shooter, float damage, float scale,
                                float areaSize, int lifetimeTicks) {
        super(ModEntities.WATERMELON_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.owner = shooter;
        this.damage = damage;
        this.scale = scale;
        this.areaSize = areaSize;
        this.lifetimeTicks = lifetimeTicks;
        this.entityData.set(SCALE, scale);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SCALE, 1.5F);
    }

    public float getWatermelonScale() {
        return this.entityData.get(SCALE);
    }

    /** 无视重力 */
    @Override
    protected float getGravity() {
        return 0.0F;
    }

    /** 不与发射者碰撞 */
    @Override
    public boolean canHitEntity(Entity entity) {
        return entity.isAlive() && entity instanceof LivingEntity && entity != this.owner;
    }

    @Override
    public void tick() {
        super.tick();
        // 存活时间到期后单纯消失，不爆炸
        if (!this.level().isClientSide && this.tickCount >= lifetimeTicks) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        Level level = this.level();
        LivingEntity owner = this.owner;
        super.onHit(hitResult);
        if (!level.isClientSide) {
            // 直击伤害
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                Entity hitEntity = entityHitResult.getEntity();
                if (hitEntity instanceof LivingEntity && hitEntity != owner) {
                    hitEntity.hurt(this.damageSources().thrown(this, owner), this.damage);
                }
            }
            // 范围伤害（5×5 或 7×7）
            explodeArea();
            this.discard();
        }
    }

    /** 在命中点/超时位置造成范围伤害，附带爆炸粒子效果但不破坏方块。 */
    private void explodeArea() {
        Level level = this.level();
        if (level == null) return;

        // 爆炸粒子和音效
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5), this.getZ(), 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(0.5), this.getZ(), 15, 0.3, 0.3, 0.3, 0.05);
        }
        level.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.8F, 1.0F);

        // 范围伤害：以命中点为中心 areaSize×areaSize×areaSize 的区域内所有生物
        AABB box = AABB.ofSize(this.position(), areaSize, areaSize, areaSize);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity entity : entities) {
            if (entity == this.owner) continue;
            if (!entity.isAlive()) continue;
            // 直击目标已经受了全额伤害，跳过避免双倍
            if (entity == this.getOwner()) continue;
            entity.hurt(this.damageSources().thrown(this, owner), this.damage);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // --- IEntityAdditionalSpawnData ---
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.damage);
        buffer.writeFloat(this.scale);
        buffer.writeFloat(this.areaSize);
        buffer.writeInt(this.lifetimeTicks);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.damage = additionalData.readFloat();
        this.scale = additionalData.readFloat();
        this.areaSize = additionalData.readFloat();
        this.lifetimeTicks = additionalData.readInt();
    }
}
