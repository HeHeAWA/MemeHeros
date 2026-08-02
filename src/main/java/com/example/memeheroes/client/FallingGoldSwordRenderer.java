package com.example.memeheroes.client;

import com.example.memeheroes.entity.FallingGoldSword;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class FallingGoldSwordRenderer extends EntityRenderer<FallingGoldSword> {
    private final ItemRenderer itemRenderer;

    public FallingGoldSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(FallingGoldSword entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.25D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees((float)entity.tickCount * 10.0F));
        
        ItemStack itemStack = new ItemStack(Items.GOLDEN_SWORD);
        this.itemRenderer.renderStatic(itemStack, net.minecraft.world.item.ItemDisplayContext.GROUND, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FallingGoldSword entity) {
        return null;
    }
}