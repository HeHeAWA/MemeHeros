package com.example.memeheroes.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

/**
 * 西瓜抛射物 —— 华强买瓜专属。
 *
 * 无视重力，可配置伤害（damage）和渲染缩放（scale）。
 * scale 通过 SynchedEntityData 同步到客户端用于渲染。
 * damage 通过 IEntityAdditionalSpawnData 同步到客户端（客户端碰撞时不需要，
 *   但以防万一多端环境）。
 */
public class WatermelonProjectile extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    private static final EntityDataAccessor<Float> SCALE =
            SynchedEntityData.defineId(WatermelonProjectile.class, EntityDataSerializers.FLOAT);

    private LivingEntity owner;
    private float damage = 7.0F;
    private float scale = 1.5F;

    public WatermelonProjectile(EntityType<? extends WatermelonProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public WatermelonProjectile(Level level, LivingEntity shooter, float damage, float scale) {
        super(ModEntities.WATERMELON_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.owner = shooter;
        this.damage = damage;
        this.scale = scale;
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

    public float getWatermelonDamage() {
        return this.damage;
    }

    /** 无视重力：ThrowableProjectile 默认每 tick 给 y 减 0.03，这里覆盖为 0。 */
    @Override
    protected float getGravity() {
        return 0.0F;
    }

    /** 不与发射者碰撞，防止出生即销毁。 */
    @Override
    public boolean canHitEntity(Entity entity) {
        return entity.isAlive() && entity instanceof LivingEntity && entity != this.owner;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        // super.onHit 可能将 level 置 null，必须先保存引用
        Level level = this.level();
        LivingEntity owner = this.owner;
        super.onHit(hitResult);
        if (!level.isClientSide) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                Entity hitEntity = entityHitResult.getEntity();
                if (hitEntity instanceof LivingEntity && hitEntity != owner) {
                    hitEntity.hurt(this.damageSources().thrown(this, owner), this.damage);
                }
            }
            this.discard();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // --- IEntityAdditionalSpawnData: 同步 damage 到客户端 ---
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.damage);
        buffer.writeFloat(this.scale);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.damage = additionalData.readFloat();
        this.scale = additionalData.readFloat();
    }
}
