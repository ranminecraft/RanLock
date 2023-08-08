package cc.ranmc.lock.util;

import cc.ranmc.lock.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionUtil {

    private static final Main plugin = Main.getInstance();
    public static void trust(Player player, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("offline")));
            return;
        }
        List<String> trustList = DataUtil.getTrustList(player.getName());
        if (trustList.size() > 44) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("full")));
            return;
        }
        if (target == player || trustList.contains(target.getName())) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("exist")));
            return;
        }
        if (plugin.isEnableSqlite()) {
            plugin.getSqLite().insertTrust(player.getName(), target.getName());
        } else {
            trustList.add(target.getName());
            plugin.getTrustYaml().set(player.getName(), trustList);
            LoadTask.end();
        }
        player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("trust")).replace("%player%", target.getName()));
    }

    public static void untrust(Player player, String name) {
        List<String> trustList = DataUtil.getTrustList(player.getName());
        if (!trustList.contains(name)) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("doesnt-exist")));
            return;
        }
        if (plugin.isEnableSqlite()) {
            plugin.getSqLite().deleteTrust(player.getName(), name);
        } else {
            trustList.remove(name);
            plugin.getTrustYaml().set(player.getName(), trustList);
            LoadTask.end();
        }
        player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("untrust")).replace("%player%", name));
    }

    public static void lockauto(Player player) {
        if (plugin.isEnableSqlite()) {
            if (plugin.getSqLite().selectAuto(player)) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("auto-lock-off")));
            } else {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("auto-lock-on")));
            }
            plugin.getSqLite().updateAuto(player);
        } else {
            List<String> list = plugin.getAutoYaml().getStringList("off");
            if (list.contains(player.getName())) {
                list.remove(player.getName());
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("auto-lock-on")));
            } else {
                list.add(player.getName());
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("auto-lock-off")));
            }
            plugin.getAutoYaml().set("off", list);
            LoadTask.end();
        }
    }

    public static void lock(Player player) {
        plugin.getUnlockAction().remove(player.getName());
        plugin.getLockAction().remove(player.getName());
        plugin.getLockAction().add(player.getName());
        player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("lock")));
    }

    public static void unlock(Player player) {
        plugin.getUnlockAction().remove(player.getName());
        plugin.getLockAction().remove(player.getName());
        plugin.getUnlockAction().add(player.getName());
        player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("unlock")));
    }

}
