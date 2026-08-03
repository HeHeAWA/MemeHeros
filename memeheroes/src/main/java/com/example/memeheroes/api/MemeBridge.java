package com.example.memeheroes.api;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 跨模组注册表：允许内容 MOD 向环境 MOD（memeenv）注册"梗"的元数据和物品。
 *
 * 该类放在本体 MOD（memeheroes）里，使得：
 * - 本体 MOD 可独立运行（不依赖环境 MOD）
 * - 环境 MOD（memeenv）通过编译期依赖读取此注册表
 * - 运行期：memeenv 强依赖 memeheroes，memeheroes 无依赖
 *
 * 物品用 Supplier<Item> 保存，延迟到真正使用（选梗给物品/清物品）时才 resolve。
 */
public final class MemeBridge {

    public static final class MemeEntry {
        private final int id;
        private final String nameId;
        private final String namespace;
        private final List<Supplier<Item>> itemSuppliers;

        public MemeEntry(int id, String nameId, String namespace, List<Supplier<Item>> itemSuppliers) {
            this.id = id;
            this.nameId = nameId;
            this.namespace = namespace;
            this.itemSuppliers = List.copyOf(itemSuppliers);
        }

        public int getId() { return id; }
        public String getNameId() { return nameId; }
        public String getNamespace() { return namespace; }

        /** 在注册阶段完成后（即需要给/清物品时）调用，安全 resolve RegistryObject。 */
        public List<Item> getItems() {
            List<Item> list = new ArrayList<>(itemSuppliers.size());
            for (Supplier<Item> s : itemSuppliers) {
                Item item = s.get();
                if (item == null) {
                    throw new IllegalStateException(
                            "Meme id=" + id + " has a null-resolving Item supplier;"
                                    + " ensure the target mod was loaded and its DeferredRegister fired.");
                }
                list.add(item);
            }
            return list;
        }

        public String translationKey() {
            return namespace + ".meme." + nameId + ".name";
        }

        public String descKey() {
            return namespace + ".meme." + nameId + ".desc";
        }
    }

    private static final Map<Integer, MemeEntry> BY_ID = new LinkedHashMap<>();
    private static final List<MemeEntry> ENTRIES = new ArrayList<>();
    private static boolean frozen = false;

    private MemeBridge() {}

    @SafeVarargs
    public static synchronized MemeEntry register(int id, String nameId, String namespace, Supplier<Item>... items) {
        return register(id, nameId, namespace, List.of(items));
    }

    public static synchronized MemeEntry register(int id, String nameId, String namespace, List<Supplier<Item>> items) {
        if (frozen) {
            throw new IllegalStateException("MemeBridge is frozen; cannot register meme id=" + id);
        }
        if (BY_ID.containsKey(id)) {
            throw new IllegalArgumentException("Meme id " + id + " already registered");
        }
        MemeEntry entry = new MemeEntry(id, nameId, namespace, items);
        BY_ID.put(id, entry);
        ENTRIES.add(entry);
        return entry;
    }

    /** 冻结注册表，由本体 MOD 在 commonSetup 时调用。 */
    public static synchronized void freeze() {
        frozen = true;
    }

    public static MemeEntry byId(int id) {
        return BY_ID.get(id);
    }

    public static List<MemeEntry> getAll() {
        return Collections.unmodifiableList(ENTRIES);
    }

    public static boolean isEmpty() {
        return ENTRIES.isEmpty();
    }
}
