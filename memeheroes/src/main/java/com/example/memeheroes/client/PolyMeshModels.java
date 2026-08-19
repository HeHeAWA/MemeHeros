package com.example.memeheroes.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 所有 PolyMesh glTF 模型的中心注册表。
 *
 * <p>模型文件统一打包在：
 * <pre>   assets/memeheroes/gltf/&lt;原文件名&gt;.gltf</pre>
 *
 * <p>用法（现在先用不上，但这些 API 随时可用）：
 * <ul>
 *   <li>{@link #getResourceLocation(String)} — 取模型的 ResourceLocation（丢给 PolyMesh 渲染器即可）</li>
 *   <li>{@link #getByFileName(String)} — 按原文件名（可带或不带 .gltf）查找</li>
 *   <li>{@link #listAllKeys()} — 返回全部注册 key，便于调试/UI 选择</li>
 *   <li>{@link #warmupIfAvailable()} — 在 Polymesh 已安装时预热所有模型缓存（客户端调用）</li>
 * </ul>
 *
 * <p>注册流程：
 * <ol>
 *   <li>类加载时静态初始化 — 从 {@link #MODELS} 内置索引读取（避免运行期 I/O 扫描资源包）</li>
 *   <li>{@code MemeHeroes.clientSetup} 调用 {@link #warmupIfAvailable()} — 若 Polymesh 已安装则逐个预热</li>
 * </ol>
 *
 * <p>将 Polymesh 设为可选依赖：没有 Polymesh 时，调用此文件中的普通 API 不会报错，
 * 只有真正调用 {@code PolymeshApi.*} 时才需要 Polymesh 存在（我们通过 {@link ModList#isLoaded(String)} 保护）。
 */
public final class PolyMeshModels {

    /** 所有模型资源所在的资源命名空间（即 memeheroes 的 modId）。 */
    public static final String NAMESPACE = com.example.memeheroes.MemeHeroes.MOD_ID;

    /** 所有模型在资源包中的相对目录（相对于 assets/<namespace>/）。
     *  Polymesh 的 GltfModelManager.reload() 扫描的固定路径是 "models/gltf"。 */
    public static final String GLTF_DIR = "models/gltf";

    // ===============================
    // 模型索引 —— 309 条。放在静态 BY_* 初始化之前，避免非法前向引用。
    // 若添加新 gltf：
    //   1. 把文件复制到 memeheroes/src/main/resources/assets/memeheroes/gltf/
    //   2. 把对应文件名追加到 MODELS 数组末尾。
    // ===============================

    private static final String[] MODELS = new String[] {
            "watermelon_1.0.gltf",
            "-back-.gltf",
            "114514.gltf",
            "1万8 屠龙宝刀.gltf",
            "5年高考3年模拟.gltf",
            "QQ频道.gltf",
            "Q币.gltf",
            "Sans.gltf",
            "TNT.gltf",
            "[原神]不灭月华.gltf",
            "[原神]无锋剑.gltf",
            "happy.gltf",
            "sans-1.gltf",
            "sans头套.gltf",
            "yy.gltf",
            "“喷气背包”.gltf",
            "【原神】护摩之杖.gltf",
            "【原神】松籁响起之时.gltf",
            "【原神】狼的末路.gltf",
            "【原神】苍古自由之誓.gltf",
            "【原神】裁叶萃光.gltf",
            "【原神】雾切之回光.gltf",
            "【原神】静水流涌之辉.gltf",
            "【崩坏三】轩辕剑.gltf",
            "一根香.gltf",
            "万圣南瓜.gltf",
            "万圣节地毯.gltf",
            "万圣节跳板.gltf",
            "万毒之剑.gltf",
            "万界归墟.gltf",
            "世界杯.gltf",
            "中秋节月饼.gltf",
            "主持人.gltf",
            "乌龟.gltf",
            "乐大王.gltf",
            "习武门Pro.gltf",
            "书.gltf",
            "二技能：牧师的宣言.gltf",
            "云雾（烟雾）.gltf",
            "亡灵十字架.gltf",
            "人.gltf",
            "人皇幡.gltf",
            "代码师.gltf",
            "代码师送你一颗小心心.gltf",
            "代码教程师.gltf",
            "代码板.gltf",
            "企鹅.gltf",
            "传送门.gltf",
            "你的作业！！！.gltf",
            "偷袭爪刀.gltf",
            "光剑.gltf",
            "八大行星_地球.gltf",
            "公告栏.gltf",
            "冰柜.gltf",
            "冰棍.gltf",
            "冰霜行者之靴.gltf",
            "净世莲台.gltf",
            "刀.gltf",
            "剑客-回旋镖.gltf",
            "劣质的魔法阵.gltf",
            "医药处.gltf",
            "十二星座——双鱼剑.gltf",
            "千年魂环.gltf",
            "单元方块.gltf",
            "南瓜羽翼.gltf",
            "原子吐息炮弹.gltf",
            "原神#2.gltf",
            "原色龙卷风.gltf",
            "只知道写BUG的屑代码师.gltf",
            "只知道写BUG的屑代码师_2.gltf",
            "只知道写BUG的屑代码师_3.gltf",
            "只知道写BUG的屑代码师_4.gltf",
            "可乐.gltf",
            "吉吉专用电脑.gltf",
            "吉吉专用电脑_2.gltf",
            "吉吉专用电脑_3.gltf",
            "吉吉专用电脑_4.gltf",
            "吉吉喵头部.gltf",
            "吉吉喵头部_2.gltf",
            "吉吉喵头部_3.gltf",
            "吉吉喵头部_4.gltf",
            "吉吉喵来了哦.gltf",
            "吉吉喵的手.gltf",
            "吉吉喵的手臂.gltf",
            "吉吉喵的脚.gltf",
            "吉吉喵的腿.gltf",
            "吉吉喵身体.gltf",
            "吉吉喵身体_2.gltf",
            "吉吉喵身体_3.gltf",
            "吉吉喵身体_4.gltf",
            "吉吉月饼.gltf",
            "吸烟的老头.gltf",
            "和平队长复刻版.gltf",
            "和平队长复刻版_2.gltf",
            "哈利波特-巫师帽.gltf",
            "哈利波特-巫师长袍.gltf",
            "哈哈小镇-水母.gltf",
            "喷泉.gltf",
            "土星.gltf",
            "圣光剑.gltf",
            "圣诞帽子.gltf",
            "圣诞拐杖糖.gltf",
            "圣诞电视人皮肤.gltf",
            "圣诞电视人皮肤_2.gltf",
            "圣诞电视人皮肤_3.gltf",
            "圣诞电视人皮肤_4.gltf",
            "圣诞礼盒.gltf",
            "坤家军.gltf",
            "坤家军冲锋.gltf",
            "坤家军队.gltf",
            "基地宠物研究所控制台.gltf",
            "塔罗牌背面.gltf",
            "墓碑.gltf",
            "墨镜.gltf",
            "大楼传送门.gltf",
            "大锅.gltf",
            "头盔.gltf",
            "奇异果博士【施法中】.gltf",
            "奖杯.gltf",
            "子弹.gltf",
            "孤空长剑.gltf",
            "学习机-PPK301型.gltf",
            "守夜人法杖.gltf",
            "宝箱.gltf",
            "小摊-1.gltf",
            "小狐狸门牌.gltf",
            "小草.gltf",
            "小飞机.gltf",
            "尚方宝剑.gltf",
            "屑风起.gltf",
            "屠龙宝刀.gltf",
            "帝国重炮.gltf",
            "年兽.gltf",
            "幸运盲盒抽奖机.gltf",
            "幸运转盘.gltf",
            "广播.gltf",
            "床.gltf",
            "异界权杖.gltf",
            "恐龙（低配版）.gltf",
            "意大利炮.gltf",
            "我家坤坤.gltf",
            "我的世界--TNT.gltf",
            "我的世界--铁链.gltf",
            "我的世界_钻石胸甲.gltf",
            "战叉.gltf",
            "战斗鸟.gltf",
            "手机.gltf",
            "打开的书本.gltf",
            "打火机.gltf",
            "投递箱.gltf",
            "披风.gltf",
            "指示箭头.gltf",
            "收银机.gltf",
            "斑点.gltf",
            "新春桃花树.gltf",
            "新春灯笼.gltf",
            "方石头.gltf",
            "春节宫灯.gltf",
            "春节披风.gltf",
            "月兔.gltf",
            "月饼.gltf",
            "有血的刀子.gltf",
            "木制公告栏.gltf",
            "末影龙.gltf",
            "末影龙【还原版】.gltf",
            "末影龙翅膀-右.gltf",
            "末影龙翅膀-左.gltf",
            "末影龙翅膀.gltf",
            "杰哥.gltf",
            "杰哥啤酒.gltf",
            "染色皮革胸甲-红.gltf",
            "树叶.gltf",
            "格林德沃.gltf",
            "桃子汁罐头.gltf",
            "桌球.gltf",
            "梗明星大乱斗封面.gltf",
            "梵高的音符.gltf",
            "死亡之环.gltf",
            "毒液池.gltf",
            "气泡鱼.gltf",
            "沙发.gltf",
            "沾满血的骨架.gltf",
            "沾血的小刀.gltf",
            "泡泡.gltf",
            "海盗骷髅人.gltf",
            "深海长剑.gltf",
            "混沌羽翼.gltf",
            "混沌锚点.gltf",
            "混沌长刀.gltf",
            "温迪大招.gltf",
            "滑稽.gltf",
            "滑雪板.gltf",
            "满分试卷.gltf",
            "激光.gltf",
            "火球-1.gltf",
            "火球-2.gltf",
            "火球.gltf",
            "灯.gltf",
            "炽阳锚点.gltf",
            "照相机.gltf",
            "爱心.gltf",
            "牛牛.gltf",
            "犀牛冲刺.gltf",
            "狗头.gltf",
            "狼头.gltf",
            "瑞克.gltf",
            "瓶装水.gltf",
            "电摇动作1.gltf",
            "电磁弹.gltf",
            "电磁炮台1级.gltf",
            "电脑-1.gltf",
            "电脑.gltf",
            "电脑2.gltf",
            "画戟-攻击效果.gltf",
            "白兔系列 围巾.gltf",
            "白色地毯-1.gltf",
            "白色地毯.gltf",
            "百大创作者摆件.gltf",
            "皮肤_圣诞鹿.gltf",
            "盾牌.gltf",
            "瞄准镜.gltf",
            "破霞披风.gltf",
            "硬币-1.gltf",
            "硬币-2.gltf",
            "硬币-3.gltf",
            "硬币.gltf",
            "神-天之耀盾.gltf",
            "神奇代码岛.gltf",
            "秩序界域.gltf",
            "空气墙.gltf",
            "空气墙（）.gltf",
            "端午节的粽子礼.gltf",
            "笔记本电脑-1.gltf",
            "笔记本电脑.gltf",
            "筷子.gltf",
            "篮球.gltf",
            "米粒.gltf",
            "红色的球.gltf",
            "红茶.gltf",
            "纸.gltf",
            "纸巾盒.gltf",
            "绿色特种兵.gltf",
            "绿色的球.gltf",
            "美国队长香肠皮肤.gltf",
            "老八秘制小汉堡.gltf",
            "老式电脑.gltf",
            "耗子尾汁.gltf",
            "耳机.gltf",
            "耳机VC.gltf",
            "聚光灯.gltf",
            "脉冲DS12B1型军刀.gltf",
            "自动门.gltf",
            "舞狮头套.gltf",
            "苦力怕.gltf",
            "草坪上的邮箱.gltf",
            "蓝雀.gltf",
            "蔬菜.gltf",
            "蜘蛛网.gltf",
            "血迹.gltf",
            "西瓜.gltf",
            "警报器.gltf",
            "豪车.gltf",
            "赤炎剑气.gltf",
            "赤炎剑气01.gltf",
            "超级激光弹.gltf",
            "足球.gltf",
            "转圈攻击特效.gltf",
            "郝哥.gltf",
            "酸奶.gltf",
            "量子苦无-地狱烈火.gltf",
            "金坷垃.gltf",
            "金翅战斗鸟.gltf",
            "金色切尔西.gltf",
            "钟表.gltf",
            "钢丝.gltf",
            "钢铁侠-香肠皮肤.gltf",
            "钴护盾.gltf",
            "闪光弹.gltf",
            "防御立场.gltf",
            "防护力场.gltf",
            "防护盾.gltf",
            "阿伟.gltf",
            "阿爸.gltf",
            "雪球.gltf",
            "雷光圣剑.gltf",
            "雷电猴.gltf",
            "雷电猴[圣诞限定版].gltf",
            "雷神香肠皮肤.gltf",
            "雷能双刃-1.gltf",
            "飞碟.gltf",
            "饭碗.gltf",
            "香.gltf",
            "马桶.gltf",
            "骷髅头.gltf",
            "骷髅皮肤（复刻）.gltf",
            "高级特工穿山甲.gltf",
            "魔云志.gltf",
            "魔王长剑.gltf",
            "鸡块.gltf",
            "鸡汤.gltf",
            "鸽鸽0486....gltf",
            "麦克风.gltf",
            "黄色书包.gltf",
            "黄色小玉弹.gltf",
            "黑奇异.gltf",
            "黑暗欺骗-地板.gltf",
            "黑洞.gltf",
            "黑色地砖.gltf",
            "龙头.gltf"
    };

    private static final Map<String, ResourceLocation> BY_KEY = new LinkedHashMap<>();
    private static final Map<String, ResourceLocation> BY_FILE_NAME = new LinkedHashMap<>();
    private static final Map<ResourceLocation, String> RL_TO_KEY = new LinkedHashMap<>();

    static {
        // Minecraft 1.20.1 的 ResourceLocation 路径只允许 [a-z0-9/._-]，
        // 不允许中文、大写字母、空格等。含非法字符的文件名会被跳过（无法用于 PolyMesh）。
        // 如需使用某个中文命名的模型，需先将文件重命名为纯 ASCII 名并更新 MODELS 数组。
        for (String fileName : MODELS) {
            try {
                String key = toKey(fileName);
                ResourceLocation rl = new ResourceLocation(NAMESPACE, GLTF_DIR + "/" + fileName);
                BY_KEY.put(key, rl);
                BY_FILE_NAME.put(fileName, rl);
                RL_TO_KEY.put(rl, key);
            } catch (Exception e) {
                // 文件名含非法字符（中文/大写/空格等），跳过此模型
            }
        }
    }

    private PolyMeshModels() {}

    // ===============================
    // 查询 API
    // ===============================

    /** 按文件名（可带或不带 ".gltf" 后缀）取 ResourceLocation。找不到返回 null。 */
    public static ResourceLocation getByFileName(String fileName) {
        if (fileName == null) return null;
        ResourceLocation rl = BY_FILE_NAME.get(fileName);
        if (rl != null) return rl;
        // 尝试补全/去除 .gltf
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".gltf")) {
            return BY_FILE_NAME.get(fileName + ".gltf");
        }
        return BY_FILE_NAME.get(fileName.substring(0, fileName.length() - 5));
    }

    /** 按 slug key（见 {@link #toKey(String)}）取 ResourceLocation。找不到返回 null。 */
    public static ResourceLocation getResourceLocation(String key) {
        if (key == null) return null;
        return BY_KEY.get(key);
    }

    /** 反向：把已注册的 ResourceLocation 转成 key。 */
    public static Optional<String> getKey(ResourceLocation rl) {
        return Optional.ofNullable(RL_TO_KEY.get(rl));
    }

    /** 返回所有已注册 key（按文件名顺序）。 */
    public static List<String> listAllKeys() {
        return new ArrayList<>(BY_KEY.keySet());
    }

    /** 返回所有已注册文件原名（包括 .gltf）。 */
    public static Set<String> listAllFileNames() {
        return Collections.unmodifiableSet(BY_FILE_NAME.keySet());
    }

    /** 返回所有 ResourceLocation。 */
    public static List<ResourceLocation> listAllResourceLocations() {
        return new ArrayList<>(BY_KEY.values());
    }

    /** 已注册的模型总数。 */
    public static int count() {
        return BY_KEY.size();
    }

    // ===============================
    // 预热（可选加载 Polymesh）
    // ===============================

    /**
     * 如果当前 forge 运行时装载了 "polymesh" mod，则把 309 个模型逐个丢给
     * {@code PolymeshApi.loadModel(ResourceLocation)} 做首次解析并放入缓存。
     *
     * <p>必须在 Minecraft 客户端线程调用（建议放在 {@code clientSetup} 阶段，
     * 此时 ResourceManager 已经就绪）。
     */
    public static void warmupIfAvailable() {
        if (!ModList.get().isLoaded("polymesh")) {
            return;
        }
        // 避免编译期把 PolymeshApi 直接引用：没有 Polymesh 的情况下，
        // 外层也必须进不来。使用反射 + try/catch 保证独立运行。
        try {
            Class<?> api = Class.forName("dev.phe.polymesh.api.PolymeshApi");
            java.lang.reflect.Method load = api.getMethod("loadModel", ResourceLocation.class);
            for (ResourceLocation rl : BY_KEY.values()) {
                load.invoke(null, rl);
            }
        } catch (Throwable ignore) {
            // 加载失败不影响主流程，玩家在渲染某个 Item/Entity 时 Polymesh 会延迟加载。
        }
    }

    /**
     * 注册某个 Item 使用指定 gltf 模型渲染（Polymesh 必须已安装，否则 no-op）。
     * 相当于：PolymeshApi.registerItemRenderer(item, model, options)
     */
    public static void bindItemModel(net.minecraft.world.item.Item item,
                                     ResourceLocation model,
                                     Object /* GltfRenderOptions or null */ options) {
        if (!ModList.get().isLoaded("polymesh")) return;
        try {
            Class<?> api = Class.forName("dev.phe.polymesh.api.PolymeshApi");
            if (options != null) {
                java.lang.reflect.Method m = api.getMethod("registerItemRenderer",
                        net.minecraft.world.item.Item.class,
                        ResourceLocation.class,
                        Class.forName("dev.phe.polymesh.api.GltfRenderOptions"));
                m.invoke(null, item, model, options);
            } else {
                java.lang.reflect.Method m = api.getMethod("registerItemRenderer",
                        net.minecraft.world.item.Item.class,
                        ResourceLocation.class);
                m.invoke(null, item, model);
            }
        } catch (Throwable ignore) { }
    }

    // ===============================
    // 工具
    // ===============================

    /**
     * 把 gltf 文件名变成稳定的 "key"：
     *  - 去掉 ".gltf" 后缀
     *  - 中英文空格/特殊符号替换成 "_"
     *  - 中文/数字/字母保留
     *  用于代码中按名字取（避免在代码里写中文符号太啰嗦）。
     */
    public static String toKey(String fileName) {
        String base = fileName;
        if (base.toLowerCase(Locale.ROOT).endsWith(".gltf")) {
            base = base.substring(0, base.length() - 5);
        }
        StringBuilder sb = new StringBuilder(base.length());
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    /** 简单的一致性校验：MODELS 里的文件必须都存在于 assets 中。仅开发期调用。 */
    public static List<String> diffMissingAgainst(java.util.function.Predicate<String> assetsPathTest) {
        return listAllFileNames().stream()
                .filter(fn -> !assetsPathTest.test(GLTF_DIR + "/" + fn))
                .collect(Collectors.toList());
    }
}
