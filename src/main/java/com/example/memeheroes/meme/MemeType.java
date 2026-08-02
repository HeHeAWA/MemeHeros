package com.example.memeheroes.meme;

import com.example.memeheroes.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum MemeType {
    PAOYE(1, "paoye", ModItems.PAOYE_TNT, ModItems.POISON_TNT),
    NETIZEN(2, "netizen", ModItems.STONE, ModItems.FEATHER_SPEED),
    SHACHANG(3, "shachang", ModItems.GOLD_SWORD, ModItems.RAIN_GOLD_SWORD),
    HUNTER(4, "hunter", ModItems.HUNTER, ModItems.CHARGE_HUNTER, () -> Items.SPYGLASS),
    JIEGE(5, "jiege", ModItems.BEER, ModItems.JIECHU_SEAL);

    private final int id;
    private final String nameId;
    private final List<Supplier<Item>> itemSuppliers;

    @SafeVarargs
    MemeType(int id, String nameId, Supplier<Item>... items) {
        this.id = id;
        this.nameId = nameId;
        this.itemSuppliers = Arrays.asList(items);
    }

    public int getId() {
        return id;
    }

    public String getNameId() {
        return nameId;
    }

    public String translationKey() {
        return "memeheroes.meme." + nameId + ".name";
    }

    public String descKey() {
        return "memeheroes.meme." + nameId + ".desc";
    }

    public List<Item> getItems() {
        return itemSuppliers.stream().map(Supplier::get).toList();
    }

    public static MemeType byId(int id) {
        for (MemeType m : values()) {
            if (m.id == id) {
                return m;
            }
        }
        return null;
    }
}
