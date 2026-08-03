package com.example.memeenv.client;

public class ClientMenuData {
    private static int killCount = 0;
    private static int selectedMemeId = 0;

    public static void set(int kills, int memeId) {
        killCount = kills;
        selectedMemeId = memeId;
    }

    public static int getKillCount() { return killCount; }
    public static int getSelectedMemeId() { return selectedMemeId; }
}
