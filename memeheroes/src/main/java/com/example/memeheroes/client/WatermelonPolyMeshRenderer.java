package com.example.memeheroes.client;

import com.example.memeheroes.entity.WatermelonProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.phe.polymesh.client.GltfEntityRendererFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 西瓜抛射物的 PolyMesh 渲染器。仅在 PolyMesh 已安装时由 ClientSetup 加载。
 */
public class WatermelonPolyMeshRenderer extends EntityRenderer<WatermelonProjectile> {

    private final EntityRenderer<WatermelonProjectile> delegate;

    public WatermelonPolyMeshRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.delegate = GltfEntityRendererFactory.<WatermelonProjectile>create(
                PolyMeshModels.getByFileName("watermelon_1.0.gltf")
        ).create(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(WatermelonProjectile entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float scale = entity.getWatermelonScale();
        poseStack.scale(scale, scale, scale);
        delegate.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WatermelonProjectile entity) {
        return delegate.getTextureLocation(entity);
    }
}
