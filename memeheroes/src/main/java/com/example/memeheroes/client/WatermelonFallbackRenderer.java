package com.example.memeheroes.client;

import com.example.memeheroes.entity.WatermelonProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * 西瓜抛射物的 Fallback 渲染器。
 * 当 PolyMesh 未安装时使用，渲染一个 MC 西瓜方块。
 *
 * 使用 renderSingleBlock（与 vanilla FallingBlockRenderer 相同方式），
 * 而非 tesselateBlock（后者需要世界位置做 AO，不适合实体渲染）。
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
        // 方块模型默认 0~1，需要平移到中心 -0.5，再缩放
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.5D, 0.5D, 0.5D);

        BlockState blockstate = Blocks.MELON.defaultBlockState();
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WatermelonProjectile entity) {
        return new ResourceLocation("minecraft", "block/melon_side");
    }
}
