package cc.ranmc.command;

import cc.ranmc.Main;
import cc.ranmc.util.Colorful;
import cc.ranmc.util.Gui;
import cc.ranmc.util.LoadTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LockCommand implements CommandExecutor {

    private Main plugin = Main.getInstance();

    /**
     * 指令输入
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        /**
         * 重载插件配置
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 1) {
            if (sender.hasPermission("lock.admin")) {
                if (args[0].equalsIgnoreCase("reload")){
                    LoadTask.start();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("reload")));
                    return true;
                }
                if (args[0].equalsIgnoreCase("save")){
                    LoadTask.end();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("save")));
                    return true;
                }
            } else {
                sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("permission")));
            }
        }

        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("console")));
            return true;
        }

        Player player = (Player) sender;

        /**
         * 鉴权
         */
        if (!sender.hasPermission("lock.user")) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("permission")));
            return true;
        }

        /**
         * 菜单
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 1 && args[0].equals("gui")) {
            Gui.open(player);
            return true;
        }

        /**
         * 上锁
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 0) {
            plugin.getUnlockAction().remove(player.getName());
            plugin.getLockAction().remove(player.getName());
            plugin.getLockAction().add(player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("lock")));
            return true;
        }

        /**
         * 解锁
         */
        if (cmd.getName().equalsIgnoreCase("unlock") && args.length == 0) {
            plugin.getUnlockAction().remove(player.getName());
            plugin.getLockAction().remove(player.getName());
            plugin.getUnlockAction().add(player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("unlock")));
            return true;
        }

        /**
         * 添加白名单
         */
        if (cmd.getName().equalsIgnoreCase("trust") && args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("offline")));
                return true;
            }
            List<String> trustList = plugin.getTrustYaml().getStringList(player.getName());
            if (trustList.size() > 44) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("full")));
                return true;
            }
            if (target == player || trustList.contains(target.getName())) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("exist")));
                return true;
            }
            trustList.add(target.getName());
            plugin.getTrustYaml().set(player.getName(), trustList);
            LoadTask.end();
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("trust")).replace("%player%", target.getName()));
            return true;
        }

        /**
         * 移除白名单
         */
        if (cmd.getName().equalsIgnoreCase("untrust") && args.length == 1) {
            List<String> trustList = plugin.getTrustYaml().getStringList(player.getName());
            if (!trustList.contains(args[0])) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("doesnt-exist")));
                return true;
            }
            trustList.remove(args[0]);
            plugin.getTrustYaml().set(player.getName(), trustList);
            LoadTask.end();
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("untrust")).replace("%player%", args[0]));
            return true;
        }

        sender.sendMessage(Colorful.valueOf("&c未知指令"));
        return true;
    }
}
