package com.example.memeheroes.client;

import com.example.memeheroes.entity.WatermelonProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.phe.polymesh.client.GltfEntityRendererFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 西瓜抛射物的 PolyMesh 渲染器。仅在 PolyMesh 已安装时由 ClientSetup 加载。
 */
public class WatermelonPolyMeshRenderer extends EntityRenderer<WatermelonProjectile> {

    private final EntityRenderer<WatermelonProjectile> delegate;
    private final BlockRenderDispatcher blockRenderer;

    public WatermelonPolyMeshRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.delegate = GltfEntityRendererFactory.<WatermelonProjectile>create(
                PolyMeshModels.getByFileName("watermelon_1.0.gltf")
        ).create(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(WatermelonProjectile entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // PolyMesh glTF 渲染
        poseStack.pushPose();
        float scale = entity.getWatermelonScale();
        poseStack.scale(scale, scale, scale);
        delegate.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();

        // Fallback: 若 PolyMesh 未渲染出模型，同时画一个 MC 西瓜方块兜底
        poseStack.pushPose();
        poseStack.translate(-0.5, 0.0, -0.5);
        BlockState blockstate = Blocks.MELON.defaultBlockState();
        this.blockRenderer.renderSingleBlock(blockstate, poseStack, buffer, packedLight,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WatermelonProjectile entity) {
        return delegate.getTextureLocation(entity);
    }
}
