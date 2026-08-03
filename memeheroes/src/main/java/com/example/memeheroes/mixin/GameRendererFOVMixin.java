package com.example.memeheroes.mixin;

import com.example.memeheroes.item.ModItems;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererFOVMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void getFov(CallbackInfoReturnable<Double> cir) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            Player player = mc.player;
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            
            boolean holdingHunter = mainHand.is(ModItems.HUNTER.get()) || offHand.is(ModItems.HUNTER.get());
            
            if (holdingHunter && player.isCrouching()) {
                double originalFov = cir.getReturnValue();
                cir.setReturnValue(originalFov * 0.5D);
            }
        }
    }
}