package com.example.memeheroes.client;

import com.example.memeheroes.entity.WatermelonProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * 西瓜抛射物的 Fallback 渲染器。
 * 当 PolyMesh 未安装时使用，渲染一个红色方块（取自红石块贴图）。
 *
 * 此类不引用任何 PolyMesh API，独立于 PolyMesh 运行。
 */
public class WatermelonFallbackRenderer extends EntityRenderer<WatermelonProjectile> {
    private final BlockRenderDispatcher blockRenderer;

    public WatermelonFallbackRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(WatermelonProjectile entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float scale = entity.getWatermelonScale();
        poseStack.translate(0.0D, 0.25D, 0.0D);
        poseStack.scale(scale * 0.5F, scale * 0.5F, scale * 0.5F);

        // 用西瓜方块（红色方块）做 fallback
        BlockState blockstate = Blocks.MELON.defaultBlockState();
        this.blockRenderer.getModelRenderer().tesselateBlock(
                entity.level(),
                this.blockRenderer.getBlockModel(blockstate),
                blockstate,
                entity.blockPosition(),
                poseStack,
                buffer.getBuffer(RenderType.cutout()),
                false,
                entity.level().random,
                0,
                packedLight
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WatermelonProjectile entity) {
        return new ResourceLocation("minecraft", "block/melon_side");
    }
}
