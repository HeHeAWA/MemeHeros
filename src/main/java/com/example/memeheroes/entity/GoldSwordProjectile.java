package com.example.memeheroes.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class GoldSwordProjectile extends ThrowableProjectile {
    private LivingEntity owner;

    public GoldSwordProjectile(EntityType<? extends GoldSwordProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public GoldSwordProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.GOLD_SWORD_PROJECTILE.get(), level);
        this.setOwner(shooter);
        this.owner = shooter;
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        
        if (!this.level().isClientSide) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                Entity hitEntity = entityHitResult.getEntity();
                if (hitEntity instanceof LivingEntity && hitEntity != this.owner) {
                    hitEntity.hurt(this.damageSources().thrown(this, this.owner), 7.0F);
                }
            }
            this.discard();
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