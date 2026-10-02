# AI 开发指南（AI_GUIDE.md）

> 本文档专为 **AI 编程助手**（Claude / GPT / Cursor / Trae 等）设计，目标：让 AI 在**不读完全部源码**的前提下，快速、准确地修改或扩展本模组。
> 人类贡献者请优先阅读 [CONTRIBUTING.md](CONTRIBUTING.md)；本文更偏向结构化的事实陈述与代码坐标索引。

---

## 0. 一分钟速览（给 AI 的 TL;DR）

- **项目**：Minecraft 1.20.1 Forge 47.1.3 双模组工程（Gradle 多模块）。
- **模块**：
  - `memeheroes/` — 本体：6 个梗职业的技能物品 + 抛射物实体 + 客户端渲染 + `MemeBridge` API。**无外部 mod 依赖**。
  - `memeenv/` — 环境：选梗 GUI、排行榜、菜单、全局效果、摔落免疫。**编译期 `implementation project(':memeheroes')`，运行期 mandatory 依赖**。
- **包名**：`com.example.memeheroes` / `com.example.memeenv`（注意是 `net.minecraftforge.*`，不是 `net.neoforged.*`）。
- **版本号**：两个模块的 `gradle.properties` 中的 `mod_version` 必须同步修改。`mods.toml` 用 `${file.jarVersion}`，不要硬编码。
- **构建**：`./gradlew :memeheroes:build` / `./gradlew :memeenv:build`。
- **运行**：`./gradlew :memeheroes:runClient`（会同时加载 memeenv 源码）。

---

## 1. 代码坐标索引（AI 最常改的位置）

### 1.1 注册中心（新内容必改）

| 文件 | 作用 |
|---|---|
| [MemeHeroes.java](memeheroes/src/main/java/com/example/memeheroes/MemeHeroes.java) | 主入口：注册 DeferredRegister、MemeBridge、指令、伤害配置加载 |
| [ModItems.java](memeheroes/src/main/java/com/example/memeheroes/item/ModItems.java) | 所有梗物品注册（11 个 `RegistryObject<Item>`） |
| [ModEntities.java](memeheroes/src/main/java/com/example/memeheroes/entity/ModEntities.java) | 所有抛射物实体注册（9 个 `EntityType`） |
| [ModTabs.java](memeheroes/src/main/java/com/example/memeheroes/item/ModTabs.java) | 创造栏标签（12 个物品） |
| [ClientSetup.java](memeheroes/src/main/java/com/example/memeheroes/client/ClientSetup.java) | 客户端渲染器注册（`EntityRenderersEvent.RegisterRenderers`） |

### 1.2 梗职业 ↔ 物品 ↔ 实体 映射表

| 梗 ID | 英文 key | 物品 | 实体 | 伤害 key | 默认伤害 |
|---|---|---|---|---|---|
| 1 | `paoye` | `PAOYE_TNT`, `POISON_TNT` | `PaoyeTntProjectile`, `PoisonTntProjectile` | `paoye.tnt`, `paoye.poison_tnt` | 10000 |
| 2 | `netizen` | `STONE`, `FEATHER_SPEED` | `StoneProjectile` | `netizen.stone` | 10 |
| 3 | `shachang` | `GOLD_SWORD`, `RAIN_GOLD_SWORD` | `GoldSwordProjectile`, `FallingGoldSword` | `shachang.gold_sword`, `shachang.falling_gold_sword` | 7 / 50 |
| 4 | `hunter` | `HUNTER`, `CHARGE_HUNTER` | `GoldNuggetProjectile`, `DelayedGoldNugget` | `hunter.gold_nugget`, `hunter.delayed_gold_nugget` | 16 / 25 |
| 5 | `jiege` | `BEER`, `JIECHU_SEAL` | `BeerBottleProjectile` | `jiege.beer`, `jiege.jiechu_seal` | 7 / 50 |
| 6 | `huaqiang` | `WATERMELON`, `BIG_WATERMELON` | `WatermelonProjectile` | `huaqiang.watermelon`, `huaqiang.big_watermelon` | 7 / 114 |

> **AI 注意**：`GoldNuggetProjectile` 同时被 `HunterItem` 和 `RainGoldSwordItem` 使用，但当前只注册了 `hunter.gold_nugget` 一个 key。若需要为两个梗分别调整金粒伤害，需新增 key 并在 `RainGoldSwordItem` 中传入。

### 1.3 伤害配置系统（v2.2.0 新增）

| 文件 | 作用 |
|---|---|
| [MemeDamageConfig.java](memeheroes/src/main/java/com/example/memeheroes/config/MemeDamageConfig.java) | 伤害 key 注册表 + JSON 持久化（`config/memeheroes/damages.json`） |
| [ChangeMemeDamageCommand.java](memeheroes/src/main/java/com/example/memeheroes/command/ChangeMemeDamageCommand.java) | `/changeMemeDamage` 指令实现 |

**新增带伤害的技能时**：
1. 在 `MemeDamageConfig` 的 `static {}` 块中 `registerDefault("梗.技能", 默认值)`。
2. 在物品/实体代码中用 `MemeDamageConfig.getDamage("梗.技能")` 替代硬编码数字。
3. 无需改动指令类，自动识别新 key。

### 1.4 跨模组 API

| 文件 | 作用 |
|---|---|
| [MemeBridge.java](memeheroes/src/main/java/com/example/memeheroes/api/MemeBridge.java) | 跨模组注册中心：本体注册梗 → 环境读取。`Supplier<Item>` 延迟 resolve |

### 1.5 网络包（memeenv 独有）

| 文件 | 方向 | 作用 |
|---|---|---|
| `S2COpenMemeScreenPacket` | S→C | 登录时通知打开选梗 GUI |
| `S2CKillLeaderboardPacket` | S→C | 排行榜数据同步 |
| `S2CMenuInfoPacket` | S→C | 菜单面板数据 |
| `C2SMemeSelectPacket` | C→S | 玩家选梗 |
| `C2SMenuRequestPacket` | C→S | 请求菜单数据 |

新增 packet 必须在 `ModMessages.register()` **末尾追加**（不能改动已有 packetId）。

---

## 2. 关键设计模式（AI 模仿写代码时遵循）

### 2.1 物品 → 实体 → 渲染器 三段式

```java
// 1) Item.use() 创建实体
WatermelonProjectile p = new WatermelonProjectile(level, player, damage, scale, area, lifetime);
p.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
level.addFreshEntity(p);
player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

// 2) Entity 继承 ThrowableProjectile，重写 onHit / tick / getAddEntityPacket
public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);  // 必须！
}

// 3) ClientSetup 注册渲染器
event.registerEntityRenderer(ModEntities.XXX.get(), XxxRenderer::new);
```

### 2.2 事件监听两种写法

| 写法 | 适用 |
|---|---|
| `@Mod.EventBusSubscriber(bus = Bus.FORGE)` | 静态方法监听 Forge 事件（`JiechuSealHandler`、`KillCountHandler`） |
| `MinecraftForge.EVENT_BUS.register(this)` + `@SubscribeEvent` | 主类实例方法（`MemeHeroes.onRegisterCommands`） |

### 2.3 客户端/服务端隔离

```java
// 客户端专属类必须加 Dist.CLIENT
@Mod.EventBusSubscriber(modid = "memeheroes", bus = Bus.MOD, value = Dist.CLIENT)
public class ClientSetup { ... }

// 物品中调用客户端代码用 DistExecutor
DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientMemeScreenHandler::openItemScreen);
```

### 2.4 伤害读取模板

```java
// 错误：硬编码
entity.hurt(source, 10000.0F);

// 正确：从配置读取
entity.hurt(source, MemeDamageConfig.getDamage("paoye.tnt"));
```

---

## 3. 硬性约束（AI 绝对禁止违反）

> 违反以下任何一条都会导致运行时事故。全部来自真实踩坑记录。

| # | 约束 | 后果 |
|---|---|---|
| 1 | `mods.toml` 版本号**禁止**用 `${mod_version}`，必须用 `${file.jarVersion}` | Forge 启动时报版本解析错误 |
| 2 | 物品模型 `parent` 必须是 `minecraft:item/tnt` 或有效原版路径 | 紫黑块（missing texture） |
| 3 | 实体必须重写 `getAddEntityPacket()` 返回 `NetworkHooks.getEntitySpawningPacket(this)` | 客户端看不到实体 |
| 4 | **禁止**在 `onHit` 中调用 `super.onHit()` | `level` 被置 null，后续代码 NPE |
| 5 | 粒子必须用 `((ServerLevel) level).sendParticles(...)` | 其他玩家看不到粒子 |
| 6 | 击飞 owner 时必须同时：`setDeltaMovement` + `hasImpulse=true` + `fallDistance=0` + 发送 `ClientboundSetEntityMotionPacket` | 速度不生效或立即摔伤 |
| 7 | 清除玩家物品必须遍历全部 **41 个槽位**（`inv.getContainerSize()`） | 副手/盔甲槽物品残留 |
| 8 | 抛射物注册必须 `clientTrackingRange(10).updateInterval(1)` | 客户端动画卡顿/瞬移 |
| 9 | 新 packet 必须追加到 `ModMessages` **末尾** | 破坏网络协议兼容性 |
| 10 | `memeheroes` 模块**禁止** import `com.example.memeenv.*` | 破坏模块独立运行能力 |
| 11 | glTF 模型必须放 `assets/memeheroes/models/gltf/`，且引用外部 `.bin` | PolyMesh 加载失败 |
| 12 | 全局效果（夜视/跳跃）用 `INFINITE_DURATION`，每 5 秒检查刷新 | 效果闪烁 |

---

## 4. 数据流与持久化

### 4.1 玩家持久数据（`player.getPersistentData()`）

| key | 类型 | 作用 |
|---|---|---|
| `selected_meme` | int | 当前梗 ID（0 = 未选择） |
| `kill_count` | int | 击杀数 |
| `jiechu_seal_time` | int | 杰除封印剩余 tick |

### 4.2 伤害配置持久化

- 文件：`config/memeheroes/damages.json`
- 加载时机：`ServerStartedEvent` → `MemeDamageConfig.load()`
- 保存时机：`/changeMemeDamage set/reset/resetAll` 后立即 `save()`
- 格式：`{"paoye.tnt": 10000.0, ...}`

### 4.3 客户端临时缓存

| 类 | 作用 |
|---|---|
| `ClientMenuData` | 菜单界面显示的击杀数/当前梗 |
| `ClientKillLeaderboardData` | 排行榜 Overlay 显示的名单 |

---

## 5. 常见任务决策树（AI 快速定位）

```
用户说"XX 技能伤害不对"
  → 查第 1.2 节映射表找到伤害 key
  → 用 /changeMemeDamage set <key> <value> 在线调整
  → 或改 MemeDamageConfig 中的 registerDefault 改默认值

用户说"新增一个梗职业"
  → 1) ModItems 注册物品
  → 2) ModEntities 注册实体（如有抛射物）
  → 3) ClientSetup 注册渲染器
  → 4) MemeHeroes.registerMemes() 加一行 register(id, "name", items...)
  → 5) MemeDamageConfig 注册伤害 key
  → 6) ModTabs 加 output.accept(...)
  → 7) assets/memeheroes/lang/zh_cn.json 加翻译
  → 8) assets/memeheroes/models/item/xxx.json 建模型（parent: minecraft:item/tnt）

用户说"客户端看不到实体"
  → 检查 getAddEntityPacket() 是否返回 NetworkHooks.getEntitySpawningPacket
  → 检查 clientTrackingRange / updateInterval
  → 检查渲染器是否在 ClientSetup 注册

用户说"排行榜不更新"
  → 检查 KillCountHandler.onLivingDeath 是否正确递增 kill_count
  → 检查 S2CKillLeaderboardPacket 是否广播
  → 检查 ClientKillLeaderboardData 是否收到数据

用户说"菜单没数据"
  → MenuItem.use() 发送 C2SMenuRequestPacket → 服务端回 S2CMenuInfoPacket → ClientMenuData.set()
  → 检查这三个环节哪一环断了
```

---

## 6. 构建与版本发布

### 6.1 版本号修改

两个文件**必须同步改**：
- `memeheroes/gradle.properties` → `mod_version=x.y.z`
- `memeenv/gradle.properties` → `mod_version=x.y.z`

README 中的示例 jar 名也应同步更新。

### 6.2 构建命令

```bash
./gradlew clean build          # 全量构建
./gradlew :memeheroes:build    # 仅本体
./gradlew :memeenv:build       # 仅环境
```

产物：
- `memeheroes/build/libs/memeheroes-x.y.z.jar`
- `memeenv/build/libs/memeenv-x.y.z.jar`

### 6.3 Git 提交规范

```
feat: 新功能
fix: Bug 修复
docs: 文档变更
refactor: 重构（不改变行为）
chore: 构建/依赖/杂项
```

---

## 7. 给 AI 的元提示（Meta-prompt）

当你（AI）被要求修改此项目时：

1. **先读本文档**，不要直接读全部源码（节省 token）。
2. **修改前先确认模块归属**：是 `memeheroes`（技能/实体/渲染）还是 `memeenv`（GUI/排行榜/菜单）？
3. **涉及伤害时**：永远走 `MemeDamageConfig.getDamage(key)`，不要硬编码。
4. **新增实体时**：必须同时改 `ModEntities` + `ClientSetup` + `getAddEntityPacket()`。
5. **新增物品时**：必须同时改 `ModItems` + `ModTabs` + `MemeHeroes.registerMemes()` + `zh_cn.json` + `models/item/xxx.json`。
6. **改完版本号后**：提醒用户 `memeenv/gradle.properties` 也要同步改。
7. **提交前**：运行 `./gradlew build` 确认编译通过。

---

## 8. 文件树速查

```
GMXMOD/
├── build.gradle                  # 根构建脚本（共享插件配置）
├── settings.gradle               # include 'memeenv', 'memeheroes'
├── gradle.properties             # 共享 MC/Forge/Parchment 版本
├── CONTRIBUTING.md               # 人类贡献者指南
├── AI_GUIDE.md                   # 本文件
├── README.md                     # 玩家向说明
│
├── memeheroes/                   # 本体 MOD
│   ├── build.gradle
│   ├── gradle.properties         # mod_id=memeheroes, mod_version=2.2.0
│   └── src/main/
│       ├── java/com/example/memeheroes/
│       │   ├── MemeHeroes.java           # 主入口
│       │   ├── api/MemeBridge.java       # 跨模组注册中心
│       │   ├── config/MemeDamageConfig.java   # 伤害配置（v2.2.0 新增）
│       │   ├── command/ChangeMemeDamageCommand.java  # /changeMemeDamage
│       │   ├── entity/                   # 9 个实体 + ModEntities
│       │   ├── item/                     # 11 个物品 + ModItems + ModTabs
│       │   ├── client/                   # 8 渲染器 + ClientSetup + PolyMeshModels
│       │   ├── event/JiechuSealHandler.java
│       │   └── mixin/GameRendererFOVMixin.java
│       └── resources/
│           ├── META-INF/mods.toml
│           ├── memeheroes.mixins.json
│           ├── assets/memeheroes/        # lang, models, textures
│           └── data/memeheroes/
│
├── memeenv/                      # 环境 MOD
│   ├── build.gradle
│   ├── gradle.properties         # mod_id=memeenv, mod_version=2.2.0
│   └── src/main/
│       ├── java/com/example/memeenv/
│       │   ├── MemeEnv.java              # 主入口
│       │   ├── event/                    # MemeGameHandler, KillCountHandler, CommonEvents
│       │   ├── item/                     # ChangeMemeItem, MenuItem, ModItems, ModTabs
│       │   ├── network/                  # ModMessages + 5 个 packet
│       │   └── client/                   # 排行榜 Overlay, 选梗 GUI, 菜单 GUI
│       └── resources/
│           ├── META-INF/mods.toml        # mandatory 依赖 memeheroes
│           └── assets/memeenv/           # lang, models
│
└── models/                       # 用户 glTF 参考（不参与构建）
```
