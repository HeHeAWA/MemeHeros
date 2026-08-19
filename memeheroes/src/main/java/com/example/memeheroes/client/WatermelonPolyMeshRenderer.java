package com.example.memeheroes.client;

import com.example.memeheroes.entity.WatermelonProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.phe.polymesh.client.GltfEntityRendererFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 西瓜抛射物的 PolyMesh 渲染器。
 *
 * <p>仅在 PolyMesh 已安装时由 ClientSetup 加载。
 * <p>由于 GltfEntityRenderer 是抽象类且其 jar 为 SRG 映射，
 * 这里通过 GltfEntityRendererFactory.create(...) 拿到一个
 * EntityRendererProvider，再用 .apply(context) 得到具体的 EntityRenderer 实例，
 * 以委托（delegation）方式调用其 render，并按实体的 scale 做额外缩放。
 *
 * <p>调用 delegate.render(...) 在编译期解析为 EntityRenderer.render（mojmap），
 * 运行期由 Forge 的反混淆器把虚拟调用派发到 GltfEntityRenderer 的实现上。
 */
public class WatermelonPolyMeshRenderer extends EntityRenderer<WatermelonProjectile> {

    private final EntityRenderer<WatermelonProjectile> delegate;

    public WatermelonPolyMeshRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.delegate = GltfEntityRendererFactory.<WatermelonProjectile>create(
                PolyMeshModels.getByFileName("西瓜1.0.gltf")
        ).create(context);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(WatermelonProjectile entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float scale = entity.getWatermelonScale();
        // 先做实体级缩放（glTF 原始大小 × scale），再交给 delegate 渲染
        poseStack.scale(scale, scale, scale);
        delegate.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
        // 不调用 super.render 以避免重复渲染 shadow；delegate 自己会画 shadow
    }

    @Override
    public ResourceLocation getTextureLocation(WatermelonProjectile entity) {
        return delegate.getTextureLocation(entity);
    }
}
