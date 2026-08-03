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

public class GoldNuggetProjectile extends ThrowableProjectile {
    private int lifespan = 60;

    public GoldNuggetProjectile(EntityType<? extends GoldNuggetProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public GoldNuggetProjectile(Level level, LivingEntity shooter, Vec3 direction) {
        super(ModEntities.GOLD_NUGGET_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        this.setDeltaMovement(direction.normalize().scale(3.0D));
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            lifespan--;
            if (lifespan <= 0) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && !this.level().isClientSide) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity hitEntity = entityHitResult.getEntity();
            if (hitEntity instanceof LivingEntity && hitEntity != this.getOwner()) {
                hitEntity.hurt(this.damageSources().thrown(this, this.getOwner()), 16.0F);
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