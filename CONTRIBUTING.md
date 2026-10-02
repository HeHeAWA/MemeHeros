# 贡献指南

> 欢迎参与 **梗明星大乱斗**（Meme Heroes）的开发。本文档面向希望阅读源码、修复 Bug、添加新梗职业或改进环境的贡献者，涵盖从环境搭建到提交 PR 的完整流程。

---

## 目录

- [1. 项目概览](#1-项目概览)
- [2. 开发环境搭建](#2-开发环境搭建)
- [3. 构建与运行](#3-构建与运行)
- [4. 双模组架构详解](#4-双模组架构详解)
- [5. 核心设计模式](#5-核心设计模式)
- [6. 代码规范与硬性约束](#6-代码规范与硬性约束)
- [7. 如何添加一个新梗职业](#7-如何添加一个新梗职业)
- [8. 网络协议与数据同步](#8-网络协议与数据同步)
- [9. 客户端渲染与 PolyMesh](#9-客户端渲染与-polymesh)
- [10. 常见坑与调试技巧](#10-常见坑与调试技巧)
- [11. 提交 PR 前检查清单](#11-提交-pr-前检查清单)

---

## 1. 项目概览

| 条目 | 值 |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.1.3+（NeoForge 1.20.1 过渡期，包名仍为 `net.minecraftforge.*`） |
| Java | 17 |
| 构建工具 | Gradle 8.x（多模块工程，使用 `net.neoforged.moddev.legacyforge` 2.0.91） |
| Mappings | Parchment 2023.09.03 |
| 可选依赖 | PolyMesh 1.0.0+（glTF 3D 模型渲染，仅客户端） |

**模组组成**：
- `memeheroes` —— 本体 MOD：6 个梗职业的技能物品、实体、渲染器、Mixin。**可独立运行**，无外部 mod 依赖。
- `memeenv` —— 环境 MOD：排行榜、换梗 GUI、菜单 GUI、全局夜视/跳跃提升、摔落免疫。**必须依赖 memeheroes**，否则 Forge 拒绝加载。

---

## 2. 开发环境搭建

### 2.1 前置要求

1. **JDK 17**
   ```bash
   java -version   # 应显示 17.x.x
   ```
2. **Git**（用于克隆与提交）
3. **IDE**：推荐 IntelliJ IDEA（社区版即可），安装 [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) 插件可获得更好的 Forge 开发体验。

### 2.2 克隆与导入

```bash
git clone https://github.com/HeHeAWA/MemeHeros.git
cd MemeHeros
./gradlew --version   # 确认 Gradle Wrapper 可用
```

用 IDEA 打开项目根目录，选择 **Open as Project**。Gradle 同步完成后，项目结构应包含 `memeheroes` 和 `memeenv` 两个模块。

> **注意**：`gradle.properties` 中已硬编码 `org.gradle.java.home=/opt/homebrew/opt/openjdk@17/...`（macOS Homebrew 路径）。Windows/Linux 用户请删除该行或改为本地 JDK 17 路径。

### 2.3 生成 IDE 运行配置（可选）

```bash
./gradlew :memeheroes:genIntellijRuns
./gradlew :memeenv:genIntellijRuns
```

---

## 3. 构建与运行

### 3.1 构建产物

```bash
./gradlew :memeheroes:build   # 构建本体
./gradlew :memeenv:build      # 构建环境
./gradlew build               # 构建全部
```

产物位置：
- `memeheroes/build/libs/memeheroes-<version>.jar`
- `memeenv/build/libs/memeenv-<version>.jar`

### 3.2 启动开发客户端

```bash
./gradlew :memeheroes:runClient
```

这会启动一个同时加载 `memeheroes` 和 `memeenv` 源码的 Forge 客户端（因为 `memeenv` 编译期依赖 `memeheroes`，LegacyForge 会自动把两个 source set 都登记为 mod）。

### 3.3 启动开发服务端

```bash
./gradlew :memeheroes:runServer
```

服务端启动后，用开发客户端连接 `localhost` 即可测试联机逻辑（网络包、排行榜同步等）。

---

## 4. 双模组架构详解

### 4.1 为什么拆成两个 mod？

| 场景 | 结果 |
|---|---|
| 玩家只想要梗技能，不想要排行榜/菜单 | 只装 `memeheroes` 即可 |
| 服务器想要完整体验（选梗、排行榜、全局效果） | 两个都装 |
| 第三方想基于梗系统做自己的环境 | 只依赖 `memeheroes`，通过 `MemeBridge` 读取注册表 |

### 4.2 依赖方向

```
memeenv ──implementation project──► memeheroes
   │                                    │
   │  mandatory dependency (mods.toml)  │  no dependency
   ▼                                    ▼
Forge 启动时强制检查              可独立运行
```

- `memeenv` 的 `build.gradle` 中：`implementation project(':memeheroes')`
- `memeenv` 的 `mods.toml` 中：`[[dependencies.memeenv]] modId="memeheroes" mandatory=true`

### 4.3 模块职责划分

| 模块 | 职责 | 关键包 |
|---|---|---|
| `memeheroes` | 梗技能物品、实体、渲染、Mixin、`MemeBridge` API | `com.example.memeheroes.{item,entity,client,mixin,event,api}` |
| `memeenv` | 选梗 GUI、排行榜、菜单、全局效果、网络包、道具补发 | `com.example.memeenv.{client,event,item,network}` |

**重要原则**：`memeheroes` 的任何代码都**不允许**引用 `memeenv` 的类；反向则允许（通过 `MemeBridge`）。

---

## 5. 核心设计模式

### 5.1 MemeBridge —— 跨模组注册中心

位置：`memeheroes/src/main/java/com/example/memeheroes/api/MemeBridge.java`

这是连接本体与环境的核心机制。本体在构造函数中注册所有梗：

```java
// MemeHeroes.java
register(1, "paoye", ModItems.PAOYE_TNT, ModItems.POISON_TNT);
register(2, "netizen", ModItems.STONE, ModItems.FEATHER_SPEED);
// ...
MemeBridge.freeze();   // commonSetup 时冻结，之后不可再注册
```

环境 mod 通过 `MemeBridge.getAll()` 读取注册表，用于：
- 选梗 GUI 渲染按钮列表
- 给玩家发放梗物品
- 清除旧梗物品
- 菜单界面显示当前梗名称

**设计要点**：
- 物品以 `Supplier<Item>` 存储，延迟到真正使用时才 `resolve`，兼容 `DeferredRegister` 的延迟初始化。
- `freeze()` 在 `commonSetup` 调用，利用 mod 加载顺序保证环境 mod 读取时数据已就绪。

### 5.2 物品-实体-渲染器 三段式

每个可投掷技能都遵循相同模式：

```
Item (右键使用) → Entity (抛射物/飞行物) → Renderer (客户端渲染)
```

以西瓜为例：
- `WatermelonItem` —— 处理右键、冷却、创建实体
- `WatermelonProjectile` —— 处理飞行、碰撞、范围伤害、生命周期
- `WatermelonPolyMeshRenderer` / `WatermelonFallbackRenderer` —— 处理客户端渲染

### 5.3 网络通信：SimpleChannel

所有自定义 packet 在 `memeenv/src/main/java/com/example/memeenv/network/ModMessages.java` 注册。

注册模板：

```java
INSTANCE.messageBuilder(MyPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
        .encoder(MyPacket::encode)
        .decoder(MyPacket::decode)
        .consumerMainThread((msg, ctx) -> msg.handle(ctx))
        .add();
```

**注意**：
- `packetId` 从 0 开始递增，**新增 packet 必须追加到末尾**，不能插入中间（会破坏旧客户端兼容性）。
- `consumerMainThread` 确保 handle 在主线程执行，避免线程安全问题。

---

## 6. 代码规范与硬性约束

以下约束来自项目长期维护中踩过的坑，**违反任何一条都会导致运行事故**，请务必遵守。

### 6.1 构建与配置

| 约束 | 原因 |
|---|---|
| `mods.toml` 中 `modLoader="javafml"`，`loaderVersion="[47,)"` | NeoForge 47.1.105 兼容性 |
| `mods.toml` 中版本号**不能**用 `${mod_version}` 占位符，必须硬编码 | 占位符会导致 Forge 版本解析错误 |
| `gradle.properties` 中 `minecraft_version=1.20.1`，`forge_version=47.1.3` | 项目锁定版本 |
| Java 17 | MC 1.20.1 强制要求 |
| 构建插件 `net.neoforged.moddev.legacyforge` 2.0.91 | 多模块 + LegacyForge 支持 |

### 6.2 物品与实体

| 约束 | 原因 |
|---|---|
| 玩家**必须且只能**拥有 1 个换梗道具 + 1 个菜单道具 | 防刷物品 |
| 菜单物品**无冷却**，可随时右键 | 产品需求 |
| 换梗物品**5 秒冷却** | 产品需求 |
| 物品模型继承 `minecraft:item/tnt` | 复用原版 TNT 纹理，避免自定义纹理文件缺失导致紫黑块 |
| 抛射物实体继承 `ThrowableProjectile` | 获得正确的射线碰撞检测 |
| `clientTrackingRange(10)` + `updateInterval(1)` | 确保抛射物在客户端平滑显示 |
| `getAddEntityPacket()` 返回 `NetworkHooks.getEntitySpawningPacket(this)` | 自定义实体必须重写，否则客户端收不到生成包 |

### 6.3 爆炸与伤害

| 约束 | 原因 |
|---|---|
| TNT 爆炸范围固定 **5×5 格**（半径 5） | 产品需求 |
| 击飞 owner 时必须 `setDeltaMovement` + `hasImpulse = true` + `fallDistance = 0.0F` | 否则速度不生效或立即摔伤 |
| 击飞玩家后发送 `ClientboundSetEntityMotionPacket` | 确保客户端正确显示击飞动画 |
| 粒子用 `((ServerLevel) level).sendParticles(...)` | 确保同步到所有客户端 |
| **不要**在 `onHit` 中调用 `super.onHit()` | `super.onHit` 会把 `level` 置 null，导致后续粒子/伤害代码 NPE |

### 6.4 资源与模型

| 约束 | 原因 |
|---|---|
| glTF 模型放在 `assets/memeheroes/models/gltf/` | PolyMesh 只扫描此目录 |
| glTF 必须引用**外部 .bin 文件**，不能内嵌 base64 | PolyMesh 不支持 data URI |
| PolyMesh 缓存 key 格式为 `namespace:model_name`（无路径、无后缀） | 错误格式会导致模型找不到 |
| `pack.mcmeta` 的 `pack_format=15` | MC 1.20.1 资源包格式 |

### 6.5 客户端/服务端隔离

| 约束 | 原因 |
|---|---|
| 客户端专属代码用 `@Mod.EventBusSubscriber(value = Dist.CLIENT)` 或 `DistExecutor` 隔离 | 防止服务端加载客户端类导致崩溃 |
| 渲染器注册放在 `EntityRenderersEvent.RegisterRenderers`（MOD bus） | 正确的注册时机 |
| 粒子倒计时逻辑需在客户端和服务端**同时独立执行** | 依赖 PersistentStateMap 同步会有延迟，导致粒子无法及时停止 |

---

## 7. 如何添加一个新梗职业

以下以"示例梗"为例，完整走一遍流程。

### 7.1 创建物品

```java
// memeheroes/src/main/java/com/example/memeheroes/item/ExampleItem.java
public class ExampleItem extends Item {
    public ExampleItem(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            // 创建实体、加冷却等
        }
        return InteractionResultHolder.success(stack);
    }
}
```

### 7.2 注册物品

```java
// ModItems.java
public static final RegistryObject<Item> EXAMPLE = ITEMS.register("example",
        () -> new ExampleItem(new Item.Properties().stacksTo(16)));
```

### 7.3 创建实体（如有抛射物）

```java
// entity/ExampleProjectile.java
public class ExampleProjectile extends ThrowableProjectile {
    // 必须重写 getAddEntityPacket() 返回 NetworkHooks.getEntitySpawningPacket(this)
    // 必须重写 defineSynchedData()（即使为空）
}
```

### 7.4 注册实体

```java
// ModEntities.java
public static final RegistryObject<EntityType<ExampleProjectile>> EXAMPLE_PROJECTILE =
        ENTITIES.register("example_projectile",
                () -> EntityType.Builder.<ExampleProjectile>of(ExampleProjectile::new, MobCategory.MISC)
                        .sized(0.5F, 0.5F)
                        .clientTrackingRange(10)
                        .updateInterval(1)
                        .build("example_projectile"));
```

### 7.5 注册渲染器

```java
// ClientSetup.java
event.registerEntityRenderer(ModEntities.EXAMPLE_PROJECTILE.get(), ExampleRenderer::new);
```

### 7.6 注册到 MemeBridge

```java
// MemeHeroes.java registerMemes()
register(7, "example", ModItems.EXAMPLE_ITEM, ModItems.EXAMPLE_ITEM_2);
```

### 7.7 添加本地化

```json
// assets/memeheroes/lang/zh_cn.json
{
  "memeheroes.meme.example.name": "示例梗",
  "memeheroes.meme.example.desc": "这是一个示例梗职业",
  "item.memeheroes.example": "示例物品"
}
```

### 7.8 添加物品模型

```json
// assets/memeheroes/models/item/example.json
{
  "parent": "minecraft:item/tnt"
}
```

### 7.9 加入创造栏

```java
// ModTabs.java displayItems
output.accept(ModItems.EXAMPLE.get());
```

---

## 8. 网络协议与数据同步

### 8.1 现有 Packet 一览

| 方向 | 类名 | 用途 |
|---|---|---|
| S→C | `S2COpenMemeScreenPacket` | 通知客户端打开选梗 GUI |
| S→C | `S2CKillLeaderboardPacket` | 同步击杀排行榜（最多 10 条） |
| S→C | `S2CMenuInfoPacket` | 返回菜单面板数据（击杀数、当前梗） |
| C→S | `C2SMemeSelectPacket` | 玩家选择梗 |
| C→S | `C2SMenuRequestPacket` | 请求菜单数据 |

### 8.2 新增 Packet 步骤

1. 在 `network/` 下创建新类，实现 `encode` / `decode` / `handle`。
2. 在 `ModMessages.register()` **末尾**追加注册（使用新的 `packetId++`）。
3. `handle` 中务必调用 `ctx.enqueueWork(...)` 和 `ctx.setPacketHandled(true)`。
4. 客户端发送用 `ModMessages.INSTANCE.sendToServer(packet)`；服务端发送用 `PacketDistributor.PLAYER.with(() -> player)` 或 `PacketDistributor.ALL.noArg()`。

### 8.3 数据存储

- **玩家持久数据**：`player.getPersistentData().getInt/putInt("key")`
  - `selected_meme` —— 当前梗 ID
  - `kill_count` —— 击杀数
  - `jiechu_seal_time` —— 杰除封印剩余 tick

- **客户端缓存**（不持久化，重连失效）：
  - `ClientMenuData` —— 菜单界面显示用
  - `ClientKillLeaderboardData` —— 排行榜 Overlay 用

---

## 9. 客户端渲染与 PolyMesh

### 9.1 渲染器选择逻辑

```java
// ClientSetup.java
if (ModList.get().isLoaded("polymesh")) {
    event.registerEntityRenderer(..., WatermelonPolyMeshRenderer::new);
} else {
    event.registerEntityRenderer(..., WatermelonFallbackRenderer::new);
}
```

- **PolyMesh 渲染器**：用于 glTF 3D 模型，通过 `GltfEntityRendererFactory` 创建。
- **Fallback 渲染器**：用原版方块/物品模型渲染，保证没装 PolyMesh 时也能玩。

### 9.2 添加新 glTF 模型

1. 把 `.gltf` 和对应 `.bin` 放入 `memeheroes/src/main/resources/assets/memeheroes/models/gltf/`。
2. 在 `PolyMeshModels.java` 的 `MODELS` 数组末尾追加文件名（含 `.gltf` 后缀）。
3. 创建渲染器，通过 `PolyMeshModels.getByFileName("xxx.gltf")` 获取 `ResourceLocation`。
4. 在 `ClientSetup` 中按上述选择逻辑注册。

---

## 10. 常见坑与调试技巧

### 10.1 紫黑块（Missing Texture）

**原因**：自定义纹理文件为空或路径错误，覆盖了原版纹理。

**解决**：物品模型 JSON 的 `parent` 必须是 `minecraft:item/tnt`（或其他有效原版路径），不要指向不存在的自定义纹理。

### 10.2 实体在客户端不可见

**检查清单**：
- [ ] `getAddEntityPacket()` 是否返回 `NetworkHooks.getEntitySpawningPacket(this)`？
- [ ] `clientTrackingRange` 和 `updateInterval` 是否设置？
- [ ] 实体是否在服务端正确 `level.addFreshEntity(entity)`？
- [ ] 渲染器是否在 `EntityRenderersEvent.RegisterRenderers` 中注册？

### 10.3 粒子不显示或不同步

- 粒子必须在**服务端**通过 `((ServerLevel) level).sendParticles(...)` 生成。
- 不要依赖 `level.addParticle(...)`（仅客户端本地，其他玩家看不到）。
- 对于倒计时类粒子（如杰除封印），客户端和服务端**各自独立计时**，不要等同步。

### 10.4 爆炸后 owner 不飞/摔伤

确保同时设置：
```java
owner.setDeltaMovement(motionX, motionY, motionZ);
owner.hasImpulse = true;
owner.fallDistance = 0.0F;
if (owner instanceof ServerPlayer sp) {
    sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
}
```

### 10.5 物品清除不干净

清除玩家物品时，必须遍历**全部 41 个槽位**（主背包 36 + 盔甲 4 + 副手 1）：

```java
Inventory inv = player.getInventory();
for (int i = 0; i < inv.getContainerSize(); i++) { ... }
```

### 10.6 开发时热重载

LegacyForge 支持部分热重载：
- 修改 Java 代码后，在 IDEA 中 **Build → Build Project**，然后按 `F3+T` 重载资源。
- 修改渲染器/模型后通常需要重启客户端。
- 修改 `mods.toml` / `gradle.properties` 后必须重启。

---

## 11. 提交 PR 前检查清单

- [ ] `./gradlew build` 编译通过，无警告（尽量）。
- [ ] 只装 `memeheroes` 能正常进入游戏，创造栏能取出新物品。
- [ ] 双模组联机测试：选梗、技能、排行榜、菜单均正常。
- [ ] 未安装 PolyMesh 时，Fallback 渲染器正常工作。
- [ ] 新增 packet 已追加到 `ModMessages` 末尾，未改动已有 packet ID。
- [ ] 新增/修改的物品模型使用 `minecraft:item/tnt` 作为 parent。
- [ ] 本地化文件（`zh_cn.json` / `en_us.json`）已同步更新。
- [ ] 没有引入对 `memeenv` 类的引用到 `memeheroes` 模块。
- [ ] Commit message 清晰描述改动内容。

---

## 许可证

All Rights Reserved. 提交 PR 即表示你同意将代码版权授予项目作者 **lqlovehehe**。
