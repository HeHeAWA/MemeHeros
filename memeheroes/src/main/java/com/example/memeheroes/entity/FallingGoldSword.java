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
import net.minecraftforge.network.NetworkHooks;

public class FallingGoldSword extends Entity {
    private LivingEntity owner;
    private int lifespan = 200;

    public FallingGoldSword(EntityType<? extends FallingGoldSword> entityType, Level level) {
        super(entityType, level);
    }

    public FallingGoldSword(Level level, double x, double y, double z, LivingEntity owner) {
        super(ModEntities.FALLING_GOLD_SWORD.get(), level);
        this.setPos(x, y, z);
        this.owner = owner;
        this.setDeltaMovement(0.0D, -0.2D, 0.0D);
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
            
            this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y - 0.05D, this.getDeltaMovement().z);
            
            if (this.getDeltaMovement().y < -1.0D) {
                this.setDeltaMovement(this.getDeltaMovement().x, -1.0D, this.getDeltaMovement().z);
            }
            
            this.move(MoverType.SELF, this.getDeltaMovement());
            
            AABB boundingBox = this.getBoundingBox().inflate(0.5D);
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, boundingBox)) {
                if (entity != this.owner) {
                    entity.hurt(this.damageSources().thrown(this, this.owner), 50.0F);
                }
            }
            
            if (this.getY() < this.level().getMinBuildHeight() - 10) {
                this.discard();
            }
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
    public boolean canCollideWith(Entity entity) {
        return entity instanceof LivingEntity && entity != this.owner;
    }

    @Override
    public void push(Entity entity) {
    }
}