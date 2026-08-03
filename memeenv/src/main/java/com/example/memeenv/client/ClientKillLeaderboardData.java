package com.example.memeenv.client;

import java.util.ArrayList;
import java.util.List;

public class ClientKillLeaderboardData {
    private static final List<String> names = new ArrayList<>();
    private static final List<Integer> kills = new ArrayList<>();

    public static void set(List<String> newNames, List<Integer> newKills) {
        names.clear();
        kills.clear();
        names.addAll(newNames);
        kills.addAll(newKills);
    }

    public static List<String> getNames() { return names; }
    public static List<Integer> getKills() { return kills; }
    public static boolean isEmpty() { return names.isEmpty(); }
}
