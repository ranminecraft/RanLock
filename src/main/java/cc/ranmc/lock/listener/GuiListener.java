package cc.ranmc.lock.listener;

import cc.ranmc.lock.Main;
import cc.ranmc.lock.util.Colorful;
import cc.ranmc.lock.util.GuiUtil;
import cc.ranmc.sign.SignApi;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class GUIListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (event.getView().getTitle().contains(Colorful.valueOf("&e&l锁箱管理丨白名单"))) {
            // 取消点击
            event.setCancelled(true);
            if (clicked == null) return;
            // 关闭菜单
            if (event.getRawSlot() < 45) {
                player.chat("/untrust " + ChatColor.stripColor(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName()));
                player.closeInventory();
            }

            // 关闭菜单
            if (event.getRawSlot() == 53 || event.getRawSlot() == 45) {
                player.closeInventory();
                return;
            }

            // 添加白名单
            if (event.getRawSlot() == 51) {
                GuiUtil.openSecond(player);
                return;
            }

            // 上锁
            if (event.getRawSlot() == 47) {
                player.chat("/lock");
                player.closeInventory();
                return;
            }

            // 解锁
            if (event.getRawSlot() == 48) {
                player.chat("/unlock");
                player.closeInventory();
                return;
            }

            // 自动锁定
            if (event.getRawSlot() == 50) {
                player.chat("/lockauto");
                player.closeInventory();
                return;
            }
        }

        if (event.getView().getTitle().contains(Colorful.valueOf("&e&l锁箱管理丨添加玩家"))) {
            // 取消点击
            event.setCancelled(true);

            // 关闭菜单
            if (event.getRawSlot() < 45) {
                player.chat("/trust " + ChatColor.stripColor(Objects.requireNonNull(Objects.requireNonNull(clicked).getItemMeta()).getDisplayName()));
                player.closeInventory();
            }

            // 关闭菜单
            if (event.getRawSlot() == 53 || event.getRawSlot() == 45) {
                GuiUtil.open(player);
                return;
            }

            // 添加白名单
            if (event.getRawSlot() == 49) {
                SignApi.newMenu("此行输入玩家ID")
                        .response((p, strings) -> {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> p.chat("/trust " + strings[0]));
                            return true;
                        }).open(player);
            }
        }
    }

}
