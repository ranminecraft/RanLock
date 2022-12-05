package cc.ranmc.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    /**
     * 创建物品
     * @param m 材质
     * @param name 名字
     * @return 物品
     */
    public static ItemStack create(Material m, String name) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colorful.valueOf(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(Material m, String name, String... lore) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colorful.valueOf(name));
        List<String> loreList = Arrays.asList(lore);
        for (int i = 0; i < loreList.size(); i++) {
            loreList.set(i, Colorful.valueOf(loreList.get(i)));
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSkull(Material m, Player player, String... lore) {
        ItemStack item = new ItemStack(m);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(Colorful.valueOf("&b" + player.getName()));
        List<String> loreList = Arrays.asList(lore);
        for (int i = 0; i < loreList.size(); i++) {
            loreList.set(i, Colorful.valueOf(loreList.get(i)));
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }
}
