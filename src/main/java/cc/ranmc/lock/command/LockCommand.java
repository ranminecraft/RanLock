package cc.ranmc.lock.command;

import cc.ranmc.lock.Main;
import cc.ranmc.lock.util.Colorful;
import cc.ranmc.lock.util.DataUtil;
import cc.ranmc.lock.util.GuiUtil;
import cc.ranmc.lock.util.LoadTask;
import cc.ranmc.lock.util.ActionUtil;
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
                    if (plugin.getSqLite() != null) plugin.getSqLite().close();
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
                    if (!plugin.isEnableSqlite()) {
                        sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("sql-not-enable")));
                        return true;
                    }
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("syncing")));

                    if (plugin.isFolia()) {
                        Bukkit.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
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
                    } else {
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
                    }
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
            ActionUtil.lockauto(player);
            return true;
        }

        /*
         * 上锁
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 0) {
            ActionUtil.lock(player);
            return true;
        }

        /*
         * 解锁
         */
        if (cmd.getName().equalsIgnoreCase("unlock") && args.length == 0) {
            ActionUtil.unlock(player);
            return true;
        }

        /*
         * 添加白名单
         */
        if (cmd.getName().equalsIgnoreCase("trust") && args.length == 1) {
            ActionUtil.trust(player, args[0]);
            return true;
        }

        /*
         * 移除白名单
         */
        if (cmd.getName().equalsIgnoreCase("untrust") && args.length == 1) {
            ActionUtil.untrust(player, args[0]);
            return true;
        }

        sender.sendMessage(Colorful.valueOf("&c未知指令"));
        return true;
    }
}
