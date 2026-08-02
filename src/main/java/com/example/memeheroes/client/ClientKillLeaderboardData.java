package com.example.memeheroes.client;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端持有的击杀排行榜数据，由服务端 S2CKillLeaderboardPacket 写入。
 * 仅存储纯 Java 数据，不引用客户端专属类，保证服务端类加载安全。
 */
public class ClientKillLeaderboardData {
    private static final List<String> names = new ArrayList<>();
    private static final List<Integer> kills = new ArrayList<>();

    public static void set(List<String> newNames, List<Integer> newKills) {
        names.clear();
        kills.clear();
        names.addAll(newNames);
        kills.addAll(newKills);
    }

    public static List<String> getNames() {
        return names;
    }

    public static List<Integer> getKills() {
        return kills;
    }

    public static boolean isEmpty() {
        return names.isEmpty();
    }
}
