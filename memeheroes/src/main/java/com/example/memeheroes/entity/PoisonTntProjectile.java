package com.example.memeheroes.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.network.NetworkHooks;

public class PoisonTntProjectile extends ThrowableProjectile {

    public PoisonTntProjectile(EntityType<? extends PoisonTntProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PoisonTntProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.POISON_TNT_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity hitEntity = entityHitResult.getEntity();
            if (hitEntity != this.getOwner() && hitEntity instanceof LivingEntity && !this.level().isClientSide) {
                hitEntity.hurt(this.damageSources().explosion(this, this.getOwner()), 10000.0F);
            }
        }
        
        explode();
        this.discard();
    }

    private void explode() {
        Level level = this.level();
        
        double explosionX = this.getX();
        double explosionY = this.getY();
        double explosionZ = this.getZ();
        
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, explosionX, explosionY, explosionZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.SMOKE, explosionX, explosionY, explosionZ, 30, 0.8D, 0.8D, 0.8D, 0.02D);
            serverLevel.sendParticles(ParticleTypes.FLAME, explosionX, explosionY, explosionZ, 20, 0.5D, 0.5D, 0.5D, 0.05D);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, explosionX, explosionY, explosionZ, 20, 0.3D, 0.3D, 0.3D, 0.01D);
            serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, explosionX, explosionY, explosionZ, 15, 0.5D, 0.5D, 0.5D, 0.02D);
            
            // 爆炸半径5格
            float explosionRadius = 5.0F;
            
            // 击飞owner - 仅限爆炸范围内
            Entity owner = this.getOwner();
            if (owner instanceof LivingEntity livingOwner && !livingOwner.isDeadOrDying()) {
                double ownerDistance = Math.sqrt(
                    Math.pow(livingOwner.getX() - explosionX, 2) +
                    Math.pow(livingOwner.getY() - explosionY, 2) +
                    Math.pow(livingOwner.getZ() - explosionZ, 2)
                );
                if (ownerDistance <= explosionRadius) {
                    knockbackOwner(livingOwner, explosionX, explosionY, explosionZ, 20.0D);
                }
            }
            
            // 处理其他实体
            DamageSource explosionDamage = this.damageSources().explosion(this, this.getOwner());
            for (Entity entity : level.getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(explosionRadius))) {
                if (entity == this.getOwner()) {
                    continue;
                }
                
                double distance = entity.distanceTo(this);
                if (distance <= explosionRadius) {
                    entity.hurt(explosionDamage, 10000.0F);
                }
            }
            
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, explosionX, explosionY, explosionZ);
            areaeffectcloud.setRadius(3.0F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(0);
            areaeffectcloud.setDuration(300);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());
            areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
            level.addFreshEntity(areaeffectcloud);
        }
    }
    
    private void knockbackOwner(LivingEntity owner, double x, double y, double z, double power) {
        double dx = owner.getX() - x;
        double dy = owner.getY() + owner.getEyeHeight() - y;
        double dz = owner.getZ() - z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        if (distance < 0.01D) {
            dx = 1.0D;
            dy = 1.0D;
            dz = 0.0D;
            distance = Math.sqrt(2.0D);
        }
        
        double factor = power / distance;
        double motionX = dx / distance * factor;
        double motionY = dy / distance * factor + power * 0.2D;
        double motionZ = dz / distance * factor;
        
        owner.setDeltaMovement(motionX, motionY, motionZ);
        owner.hasImpulse = true;
        owner.fallDistance = 0.0F;
        
        // 关键：发送速度同步包给客户端
        if (owner instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }
}