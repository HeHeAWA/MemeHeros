# 🎮 梗明星大乱斗

> 将网络热梗转化为技能的 Minecraft 1.20.1 Forge 模组

![mod-banner](https://dao3.fun/_next/image?url=https%3A%2F%2Fassets.box3.fun%2Fcontent%2FjOz2G9mGu1vXlSzGz_y04hZtFDXgkO_OBDigD28likg.png&w=1920&q=75)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.1.3+-orange.svg)](https://files.minecraftforge.net/)
[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-lightgrey.svg)](#-许可证)
[![Release](https://img.shields.io/github/v/release/HeHeAWA/MemeHeros?include_prereleases)](https://github.com/HeHeAWA/MemeHeros/releases)

---

## 📖 项目简介

**梗明星大乱斗**（Meme Heroes）是一个将中文互联网热梗做成可选"职业"的 Minecraft 模组。玩家登录后可从 5 个梗职业中选择一个，获得对应的技能物品；安装环境模组后还能享受排行榜、换梗 GUI、全局增益等完整体验。

项目采用**双模组架构**：核心战斗内容（梗技能）与环境管理功能（排行榜/菜单/效果）解耦，玩家可按需安装。

---

## 🏗️ 双模组架构

```
┌──────────────────────────────────┐         ┌──────────────────────────────────┐
│  memeheroes (梗明星大乱斗)        │         │  memeenv (梗明星环境)             │
│  ────────────────────────────    │         │  ────────────────────────────    │
│  • 5 个梗职业的技能物品 + 实体     │  ←────   │  • 击杀排行榜 (右上角 Overlay)    │
│  • 8 个客户端渲染器               │ 强依赖  │  • 换梗道具 (选梗 GUI)            │
│  • GameRendererFOVMixin           │         │  • 菜单道具 (玩家信息面板)         │
│  • 杰除封印特效                   │         │  • 全局夜视 + 跳跃提升 8 (无限)    │
│  • MemeBridge 跨模组注册 API ⭐  │         │  • 摔落伤害取消                   │
│  • 独立创造栏 (10 个梗物品)       │         │  • 道具自动补发                   │
│  • 无任何外部 mod 依赖            │         │  • mandatory 依赖 memeheroes       │
└──────────────────────────────────┘         └──────────────────────────────────┘
            ▲                                              ▲
            │              编译期 / 运行期                  │
            └────────── implementation project ─────────────┘
```

### 📦 安装组合

| 安装方式 | 是否可用 | 说明 |
|---|:---:|---|
| 只装 `memeheroes` | ✅ | 5 个梗技能物品可从创造栏取出使用 |
| 只装 `memeenv` | ❌ | Forge 启动时因 mandatory 依赖缺失而拒绝加载 |
| 两个都装 | ✅ 推荐 | 获得完整体验：选梗 GUI、排行榜、菜单、全局效果 |

---

## 🎭 5 个梗职业

登录服务器后，环境模组会弹出选梗 GUI 让你选择职业。每个职业拥有 2~3 件专属技能物品：

### 1. 🎯 炮爷出击（paoye）
> "炮爷出击，飞！"
- **炮爷TNT**：投掷后产生爆炸的 TNT 投掷物
- **毒炮TNT**：投掷后产生带毒雾效果的 TNT

### 2. 😊 开朗的网友（netizen）
- **石头**：可投掷的石头抛射物
- **神羽**：使用后获得速度提升

### 3. ⚔️ 一人攻沙虐船厂（shachang）
- **一人攻沙（金剑）**：可投掷的金剑抛射物
- **虐船厂（金剑雨）**：召唤从天而降的金剑与金粒

### 4. 🏹 开朗的猎人（hunter）
- **猎人**：远程攻击武器
- **蓄力猎人**：可蓄力的强化版猎人
- **望远镜**：原版物品，配合猎人使用

### 5. 🍺 杰哥不要啦（jiege）
- **杰哥啤酒**：投掷啤酒瓶抛射物
- **杰除封印**：触发特殊的杰除封印特效

---

## 🎁 环境模组功能

安装 `memeenv` 后获得以下环境功能：

| 功能 | 说明 |
|---|---|
| 🏆 **击杀排行榜** | 右上角实时显示前 10 名玩家击杀数 |
| 🔄 **换梗道具** | 右键打开选梗 GUI，切换职业（5 秒冷却） |
| 📜 **菜单道具** | 右键查看玩家信息：击杀数、血量、饱食度、护甲、经验、当前梗 |
| 💡 **全局夜视** | 所有玩家无限时长夜视（amplifier=0） |
| 🦘 **跳跃提升 8** | 所有玩家无限时长跳跃提升（amplifier=7） |
| ☁️ **摔落免疫** | 取消所有摔落伤害 |
| 🎒 **道具自补** | 登录 / 复活 / 每 3 秒自动补发换梗 + 菜单道具 |
| 🌉 **MemeBridge API** | 跨模组注册中心，本体 mod 通过它注册梗条目 |

---

## ⚙️ 安装指南

### 玩家安装

1. **前置要求**
   - Minecraft 1.20.1
   - Forge 47.1.3 或更高
   - Java 17

2. **下载 jar**
   - 前往 [Releases 页面](https://github.com/HeHeAWA/MemeHeros/releases) 下载最新版
   - 至少下载 `memeheroes-2.0.0.jar`（本体）
   - 推荐同时下载 `memeenv-2.0.0.jar`（环境）

3. **放入 mods 目录**
   ```
   .minecraft/mods/
   ├── memeheroes-2.0.0.jar   ← 必须
   └── memeenv-2.0.0.jar      ← 推荐
   ```

4. **启动游戏**，登录后即可选择梗职业开始游玩

---

## 🔧 构建指南

### 环境要求

- JDK 17
- Gradle 8.x（或使用项目自带 `gradlew`）

### 编译

```bash
# 在项目根目录执行
./gradlew :memeheroes:build      # 仅构建本体 mod
./gradlew :memeenv:build         # 仅构建环境 mod
./gradlew build                  # 构建全部
```

构建产物位于各子模块的 `build/libs/`：
- `memeheroes/build/libs/memeheroes-2.0.0.jar`
- `memeenv/build/libs/memeenv-2.0.0.jar`

### 开发调试

```bash
# 启动客户端（同时加载两个 mod 源码）
./gradlew :memeheroes:runClient
```

---

## 📂 项目结构

```
MemeHeros/
├── settings.gradle              # 多模块设置：include 'memeenv', 'memeheroes'
├── build.gradle                 # 共享插件配置
├── gradle.properties            # 共享 MC/Forge 版本 + 作者
│
├── memeheroes/                  # ── 本体 MOD（可独立运行）──
│   ├── build.gradle
│   ├── gradle.properties        # mod_id=memeheroes, mod_name=Meme Heroes
│   └── src/main/
│       ├── java/com/example/memeheroes/
│       │   ├── MemeHeroes.java              # @Mod 入口，注册 5 个梗到 MemeBridge
│       │   ├── api/MemeBridge.java           # 跨模组注册中心 ⭐
│       │   ├── entity/                      # 9 个抛射物实体
│       │   ├── item/                         # 10 个技能物品 + ModItems + ModTabs
│       │   ├── client/                      # 8 个渲染器 + ClientSetup
│       │   ├── event/JiechuSealHandler.java  # 杰除封印特效
│       │   └── mixin/GameRendererFOVMixin.java
│       └── resources/
│           ├── META-INF/mods.toml           # 无依赖声明
│           ├── memeheroes.mixins.json
│           ├── assets/memeheroes/           # 语言文件、模型
│           └── data/memeheroes/             # 创造栏标签
│
├── memeenv/                     # ── 环境 MOD（依赖 memeheroes）──
│   ├── build.gradle             # implementation project(':memeheroes')
│   ├── gradle.properties        # mod_id=memeenv, mod_name=Meme Environment
│   └── src/main/
│       ├── java/com/example/memeenv/
│       │   ├── MemeEnv.java                  # @Mod 入口
│       │   ├── event/
│       │   │   ├── MemeGameHandler.java      # 登录/复活/Tick 补道具+效果
│       │   │   ├── KillCountHandler.java     # 击杀计数 + 排行榜同步
│       │   │   └── CommonEvents.java         # 摔落伤害取消
│       │   ├── item/                         # ChangeMemeItem + MenuItem + ModTabs
│       │   ├── network/                      # 6 个 C2S/S2C packet
│       │   └── client/                       # 排行榜 Overlay + 选梗 GUI + 菜单 GUI
│       └── resources/
│           ├── META-INF/mods.toml           # mandatory 依赖 memeheroes
│           └── assets/memeenv/               # 语言文件、模型
│
└── models/                      # 用户 3D 模型参考（不参与构建）
```

---

## 🧩 技术细节

### 跨模组通信：MemeBridge

本体模组（memeheroes）提供 `MemeBridge` 注册中心，让内容模组向环境模组注册"梗"的元数据和物品：

```java
// memeheroes/MemeHeroes.java
MemeBridge.register(1, "paoye", MOD_ID,
    ModItems.PAOYE_TNT, ModItems.POISON_TNT);
MemeBridge.register(2, "netizen", MOD_ID,
    ModItems.STONE, ModItems.FEATHER_SPEED);
// ...
MemeBridge.freeze();  // commonSetup 时冻结
```

```java
// memeenv 读取注册表（选梗 GUI / 给物品 / 菜单显示）
List<MemeEntry> memes = MemeBridge.getAll();
for (MemeEntry e : memes) {
    List<Item> items = e.getItems();  // 延迟 resolve RegistryObject
}
```

- 物品用 `Supplier<Item>` 保存，延迟到真正使用时才 resolve，安全兼容 `DeferredRegister`
- `freeze()` 在本体 mod 的 `commonSetup` 调用；环境 mod 的 `commonSetup` 晚于此执行（依赖关系保证），读取时数据已就绪

### 全局效果实现

夜视与跳跃提升使用 `MobEffects.INFINITE_DURATION`（无限时长），并通过 `amplifier` 检查防止被信标等弱效果覆盖：

```java
// 仅在缺失或当前等级更低时施加
if (effect == null || effect.getAmplifier() < targetAmplifier) {
    player.addEffect(new MobEffectInstance(...));
}
```

### 网络通信

使用 SimpleChannel 实现 6 个 packet：

| 方向 | Packet | 用途 |
|---|---|---|
| S→C | `S2COpenMemeScreenPacket` | 服务器通知客户端打开选梗 GUI |
| S→C | `S2CKillLeaderboardPacket` | 同步击杀榜数据 |
| S→C | `S2CMenuInfoPacket` | 返回玩家信息（菜单面板） |
| C→S | `C2SMemeSelectPacket` | 玩家选择梗 |
| C→S | `C2SMenuRequestPacket` | 请求玩家信息 |

---

## 🛠️ 技术栈

- **Minecraft** 1.20.1
- **Forge** 47.1.3（NeoForge 1.20.1 过渡期，包名仍为 `net.minecraftforge.*`）
- **Java** 17
- **Gradle** 8.x（多模块工程）
- **Parchment** mappings 2023.09.03
- **Moddev LegacyForge** 插件 2.0.91

---

## 📋 版本说明

| 版本 | 说明 |
|---|---|
| 2.0.0 | 拆分为双模组架构（memeheroes + memeenv），反转依赖方向使本体可独立运行 |
| 1.0.1 | 全局夜视与跳跃提升改为无限时长，修复闪烁问题 |
| 1.0.0 | 初始版本（单模组） |

详见 [Releases](https://github.com/HeHeAWA/MemeHeros/releases)。

---

## 👤 作者

**lqlovehehe**

---

## 📄 许可证

All Rights Reserved. 未经作者授权，请勿二次分发或商用。

---

## 🙏 致谢

感谢以下网络热梗为这个模组提供的灵感：
- 炮爷出击
- 开朗的网友
- 一人攻沙虐船厂
- 开朗的猎人
- 杰哥不要啦
