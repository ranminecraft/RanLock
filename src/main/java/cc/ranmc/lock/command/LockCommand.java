package cc.ranmc.lock.command;

import cc.ranmc.lock.Main;
import cc.ranmc.lock.util.Colorful;
import cc.ranmc.lock.util.DataUtil;
import cc.ranmc.lock.util.GuiUtil;
import cc.ranmc.lock.util.LoadTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class LockCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        /*
         * 重载插件配置
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 1) {
            if (sender.hasPermission("lock.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.getSqLite().close();
                    LoadTask.start();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("reload")));
                    return true;
                }
                if (args[0].equalsIgnoreCase("save")) {
                    LoadTask.end();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("save")));
                    return true;
                }
                if (args[0].equalsIgnoreCase("sync")) {
                    if (!plugin.isEnableSqlite()) return true;
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("syncing")));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                        int count = 0;
                        Map<String,String> lockMap = plugin.getLockMap();
                        for (String key : lockMap.keySet()) {
                            plugin.getSqLite().insertLock(lockMap.get(key), DataUtil.getLocByStr(key));
                            count++;
                        }
                        YamlConfiguration trustYml = plugin.getTrustYaml();
                        for (String key : trustYml.getKeys(false)) {
                            List<String> list = trustYml.getStringList(key);
                            for (String name : list) {
                                plugin.getSqLite().insertTrust(key, name);
                                count++;
                            }
                        }
                        sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("sync").replace("%count%", String.valueOf(count))));
                    });
                    return true;
                }
            }
        }

        if (!(sender instanceof Player player)) {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("console")));
            return true;
        }

        /*
         * 鉴权
         */
        if (!sender.hasPermission("lock.user")) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("permission")));
            return true;
        }

        /*
         * 菜单
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 1 && args[0].equals("gui")) {
            GuiUtil.open(player);
            return true;
        }

        /*
         * 自动上锁开关
         */
        if (cmd.getName().equalsIgnoreCase("lockauto") && args.length == 0) {
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

            return true;
        }

        /*
         * 上锁
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 0) {
            plugin.getUnlockAction().remove(player.getName());
            plugin.getLockAction().remove(player.getName());
            plugin.getLockAction().add(player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("lock")));
            return true;
        }

        /*
         * 解锁
         */
        if (cmd.getName().equalsIgnoreCase("unlock") && args.length == 0) {
            plugin.getUnlockAction().remove(player.getName());
            plugin.getLockAction().remove(player.getName());
            plugin.getUnlockAction().add(player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("unlock")));
            return true;
        }

        /*
         * 添加白名单
         */
        if (cmd.getName().equalsIgnoreCase("trust") && args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("offline")));
                return true;
            }
            List<String> trustList = DataUtil.getTrustList(player.getName());
            if (trustList.size() > 44) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("full")));
                return true;
            }
            if (target == player || trustList.contains(target.getName())) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("exist")));
                return true;
            }
            if (plugin.isEnableSqlite()) {
                plugin.getSqLite().insertTrust(player.getName(), target.getName());
            } else {
                trustList.add(target.getName());
                plugin.getTrustYaml().set(player.getName(), trustList);
                LoadTask.end();
            }
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("trust")).replace("%player%", target.getName()));
            return true;
        }

        /*
         * 移除白名单
         */
        if (cmd.getName().equalsIgnoreCase("untrust") && args.length == 1) {
            List<String> trustList = DataUtil.getTrustList(player.getName());
            if (!trustList.contains(args[0])) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("doesnt-exist")));
                return true;
            }
            if (plugin.isEnableSqlite()) {
                plugin.getSqLite().deleteTrust(player.getName(), args[0]);
            } else {
                trustList.remove(args[0]);
                plugin.getTrustYaml().set(player.getName(), trustList);
                LoadTask.end();
            }
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("untrust")).replace("%player%", args[0]));
            return true;
        }

        sender.sendMessage(Colorful.valueOf("&c未知指令"));
        return true;
    }
}
