package com.example.memeheroes.client;

import com.example.memeheroes.entity.PaoyeTntProjectile;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class PaoyeTntRenderer extends EntityRenderer<PaoyeTntProjectile> {
    private final BlockRenderDispatcher blockRenderer;

    public PaoyeTntRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(PaoyeTntProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        
        BlockState blockstate = Blocks.TNT.defaultBlockState();
        this.blockRenderer.getModelRenderer().tesselateBlock(entity.level(), this.blockRenderer.getBlockModel(blockstate), blockstate, entity.blockPosition(), poseStack, buffer.getBuffer(RenderType.cutout()), false, entity.level().random, 0, packedLight);
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PaoyeTntProjectile entity) {
        return null;
    }
}