package com.example.memeheroes.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class DelayedGoldNugget extends ThrowableProjectile {
    private int lifespan = 220;
    private int chargeTime = 30;
    private boolean hasLaunched = false;
    private Vec3 launchDirection = Vec3.ZERO;

    public DelayedGoldNugget(EntityType<? extends DelayedGoldNugget> entityType, Level level) {
        super(entityType, level);
    }

    public DelayedGoldNugget(Level level, double x, double y, double z, LivingEntity owner, Vec3 direction) {
        super(ModEntities.DELAYED_GOLD_NUGGET.get(), level);
        this.setPos(x, y, z);
        this.setOwner(owner);
        this.launchDirection = direction.normalize();
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            lifespan--;
            if (lifespan <= 0) {
                this.discard();
                return;
            }
            
            if (!hasLaunched) {
                chargeTime--;
                if (chargeTime <= 0) {
                    hasLaunched = true;
                    this.setDeltaMovement(launchDirection.x * 3.0D, launchDirection.y * 3.0D, launchDirection.z * 3.0D);
                }
            }
        }
        
        if (hasLaunched) {
            super.tick();
        } else {
            this.setDeltaMovement(Vec3.ZERO);
            this.tickCount++;
            this.refreshDimensions();
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!hasLaunched) {
            return;
        }
        
        if (hitResult.getType() == HitResult.Type.ENTITY && !this.level().isClientSide) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity hitEntity = entityHitResult.getEntity();
            if (hitEntity instanceof LivingEntity && hitEntity != this.getOwner()) {
                hitEntity.hurt(this.damageSources().thrown(this, this.getOwner()), 25.0F);
            }
        }
        this.discard();
    }

    @Override
    public float getGravity() {
        return 0.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }
}