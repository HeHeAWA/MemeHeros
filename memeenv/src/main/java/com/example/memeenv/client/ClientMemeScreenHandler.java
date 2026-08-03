package com.example.memeenv.client;

import net.minecraft.client.Minecraft;

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
