package com.example.memeheroes.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class BeerBottleProjectile extends Entity {
    private LivingEntity owner;
    private int lifespan = 80;

    public BeerBottleProjectile(EntityType<? extends BeerBottleProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BeerBottleProjectile(Level level, LivingEntity shooter, Vec3 direction) {
        super(ModEntities.BEER_BOTTLE_PROJECTILE.get(), level);
        this.owner = shooter;
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        this.setDeltaMovement(direction.normalize().scale(1.5D));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            lifespan--;
            if (lifespan <= 0) {
                this.discard();
                return;
            }
            
            this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y - 0.03D, this.getDeltaMovement().z);
            
            AABB boundingBox = this.getBoundingBox().inflate(0.5D);
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, boundingBox)) {
                if (entity != this.owner) {
                    entity.hurt(this.damageSources().thrown(this, this.owner), 7.0F);
                    entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                    this.discard();
                    return;
                }
            }
            
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
    }
}