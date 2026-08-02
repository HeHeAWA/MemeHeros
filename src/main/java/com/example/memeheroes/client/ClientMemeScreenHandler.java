package com.example.memeheroes.client;

import net.minecraft.client.Minecraft;

/**
 * 客户端桥接类：隔离 Minecraft/Screen 等客户端类，
 * 避免服务端在加载网络包时触发客户端类加载。
 */
public class ClientMemeScreenHandler {
    public static void openLoginScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.setScreen(new MemeSelectionScreen(false)));
    }

    public static void openItemScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.setScreen(new MemeSelectionScreen(true)));
    }

    public static void openMenuScreen() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> mc.setScreen(new MenuScreen()));
    }
}
