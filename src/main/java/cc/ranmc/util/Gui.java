package cc.ranmc.util;

import cc.ranmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Gui {
    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, Colorful.valueOf("&e&l锁箱管理丨白名单"));
        List<String> trustList = Main.getInstance().getTrustYaml().getStringList(player.getName());
        int inventorySize = 0;
        for(String name : trustList) {
            inventory.setItem(inventorySize, ItemBuilder.create(Material.PLAYER_HEAD, "&b" + name, "&e点击取消白名单"));
            inventorySize++;
        }
        inventory.setItem(47, ItemBuilder.create(Material.NETHERITE_SCRAP, "&b上锁", "&e对容器进行上锁"));
        inventory.setItem(48, ItemBuilder.create(Material.SHEARS, "&b解锁", "&e对容器进行解锁"));
        inventory.setItem(51, ItemBuilder.create(Material.CHEST, "&b添加玩家", "&e不要分享权限给陌生人", "&e否则造成损失后果自负"));
        inventory.setItem(50, ItemBuilder.create(Material.REDSTONE, "&b自动锁定", "&e开关放置后自动锁箱"));
        ItemStack closeItem = ItemBuilder.create(Material.BARRIER, "&b关闭菜单");
        inventory.setItem(45, closeItem);
        inventory.setItem(53, closeItem);
        player.openInventory(inventory);
    }

    public static void openSecond(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, Colorful.valueOf("&e&l锁箱管理丨添加玩家"));
        List<String> trustList = Main.getInstance().getTrustYaml().getStringList(player.getName());
        int inventorySize = 0;
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!trustList.contains(onlinePlayer.getName()) && onlinePlayer != player) {
                if (inventorySize < 45) {
                    inventory.setItem(inventorySize, ItemBuilder.createSkull(Material.PLAYER_HEAD, onlinePlayer, "&e点击添加白名单"));
                    inventorySize++;
                }
            }
        }
        inventory.setItem(49, ItemBuilder.create(Material.OAK_SIGN, "&b手动添加", "&e找不到玩家点这里", "&e手动输入玩家名字"));
        ItemStack closeItem = ItemBuilder.create(Material.BARRIER, "&b返回菜单");
        inventory.setItem(45, closeItem);
        inventory.setItem(53, closeItem);
        player.openInventory(inventory);
    }
}
