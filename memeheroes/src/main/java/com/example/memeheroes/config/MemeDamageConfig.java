package com.example.memeheroes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 梗技能伤害配置中心。
 *
 * 所有梗技能的伤害数值统一注册在此处，管理员可通过 /changeMemeDamage 指令在运行时修改。
 * 修改后的值会持久化到 config/memeheroes/damages.json，服务器重启后自动加载。
 *
 * Key 命名规则：{梗英文名}.{技能英文名}
 * 例：paoye.tnt、netizen.stone、shachang.gold_sword
 *
 * 新增技能时必须在此注册默认值，否则 /changeMemeDamage 无法识别该技能。
 */
public final class MemeDamageConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE_NAME = "damages.json";

    /** key -> 当前伤害值（可被管理员指令覆盖） */
    private static final Map<String, Float> DAMAGES = new LinkedHashMap<>();
    /** key -> 默认伤害值（reset 时恢复到此值） */
    private static final Map<String, Float> DEFAULTS = new LinkedHashMap<>();

    static {
        // 炮爷出击
        registerDefault("paoye.tnt", 10000.0F);
        registerDefault("paoye.poison_tnt", 10000.0F);

        // 开朗的网友
        registerDefault("netizen.stone", 10.0F);
        registerDefault("netizen.feather_speed", 0.0F);        // 无伤害，仅速度效果

        // 一人攻沙虐船厂
        registerDefault("shachang.gold_sword", 7.0F);            // GoldSwordItem → GoldSwordProjectile
        registerDefault("shachang.falling_gold_sword", 50.0F);   // RainGoldSwordItem → FallingGoldSword

        // 开朗的猎人
        registerDefault("hunter.gold_nugget", 16.0F);            // HunterItem → GoldNuggetProjectile
        registerDefault("hunter.delayed_gold_nugget", 25.0F);    // ChargeHunterItem → DelayedGoldNugget

        // 杰哥不要啦
        registerDefault("jiege.beer", 7.0F);
        registerDefault("jiege.jiechu_seal", 50.0F);

        // 华强买瓜
        registerDefault("huaqiang.watermelon", 7.0F);
        registerDefault("huaqiang.big_watermelon", 114.0F);

        // 野兽先辈
        registerDefault("yaju.projectile_114514", 8.0F);    // Item114514 → 复用 WatermelonProjectile
        registerDefault("yaju.beast_cry", 16.0F);            // BeastCryItem → 8×8 AOE
    }

    private MemeDamageConfig() {}

    private static void registerDefault(String key, float defaultValue) {
        DEFAULTS.put(key, defaultValue);
        DAMAGES.put(key, defaultValue);
    }

    /** 获取某技能的当前伤害值。若 key 未注册返回 0。 */
    public static float getDamage(String key) {
        return DAMAGES.getOrDefault(key, 0.0F);
    }

    /** 设置某技能的伤害值（仅内存，需配合 save() 持久化）。 */
    public static void setDamage(String key, float value) {
        DAMAGES.put(key, value);
    }

    /** 重置某技能为默认伤害。 */
    public static void resetDamage(String key) {
        DAMAGES.put(key, DEFAULTS.getOrDefault(key, 0.0F));
    }

    /** 重置所有技能为默认伤害。 */
    public static void resetAll() {
        DAMAGES.putAll(DEFAULTS);
    }

    /** 返回所有已注册的伤害 key（有序）。 */
    public static Map<String, Float> getAllDamages() {
        return Collections.unmodifiableMap(DAMAGES);
    }

    /** 返回所有默认值（有序）。 */
    public static Map<String, Float> getAllDefaults() {
        return Collections.unmodifiableMap(DEFAULTS);
    }

    /** 判断 key 是否已注册。 */
    public static boolean isValidKey(String key) {
        return DEFAULTS.containsKey(key);
    }

    // ==================== 持久化 ====================

    private static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve("memeheroes").resolve(CONFIG_FILE_NAME);
    }

    /** 从磁盘加载配置（服务器启动时调用）。 */
    public static void load() {
        Path path = getConfigPath();
        if (!Files.exists(path)) {
            save(); // 首次运行生成默认文件
            return;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            Map<String, Float> loaded = GSON.fromJson(reader, new TypeToken<Map<String, Float>>() {}.getType());
            if (loaded != null) {
                for (Map.Entry<String, Float> e : loaded.entrySet()) {
                    if (DEFAULTS.containsKey(e.getKey())) {
                        DAMAGES.put(e.getKey(), e.getValue());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 保存当前配置到磁盘。 */
    public static void save() {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(DAMAGES, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 服务器启动完成事件回调：自动加载配置。 */
    public static void onServerStarted(MinecraftServer server) {
        load();
    }

    /** 检查服务器是否已运行（用于指令中判断能否保存）。 */
    public static boolean isServerRunning() {
        return ServerLifecycleHooks.getCurrentServer() != null;
    }
}
