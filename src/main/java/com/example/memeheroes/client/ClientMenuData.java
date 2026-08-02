package com.example.memeheroes.client;

/**
 * 客户端持有的玩家菜单信息，由服务端 S2CMenuInfoPacket 写入。
 * 仅纯 Java 数据，服务端类加载安全。
 */
public class ClientMenuData {
    private static int killCount = 0;
    private static int selectedMemeId = 0;

    public static void set(int kills, int memeId) {
        killCount = kills;
        selectedMemeId = memeId;
    }

    public static int getKillCount() {
        return killCount;
    }

    public static int getSelectedMemeId() {
        return selectedMemeId;
    }
}
