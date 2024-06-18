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
        List<String> trustList = LockUtil.getTrustList(player.getName());
        if (trustList.size() > 44) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("full")));
            return;
        }
        if (target == player || trustList.contains(target.getName())) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("exist")));
            return;
        }
        trustList.add(target.getName());
        plugin.getTrustYaml().set(player.getName(), trustList);
        DataUtil.save();
        player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("trust")).replace("%player%", target.getName()));
    }

    public static void untrust(Player player, String name) {
        List<String> trustList = LockUtil.getTrustList(player.getName());
        if (!trustList.contains(name)) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("doesnt-exist")));
            return;
        }
        trustList.remove(name);
        plugin.getTrustYaml().set(player.getName(), trustList);
        DataUtil.save();
        player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("untrust")).replace("%player%", name));
    }

    public static void lockauto(Player player) {
        List<String> list = plugin.getAutoYaml().getStringList("off");
        if (list.contains(player.getName())) {
            list.remove(player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("auto-lock-on")));
        } else {
            list.add(player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("auto-lock-off")));
        }
        plugin.getAutoYaml().set("off", list);
        DataUtil.save();
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
