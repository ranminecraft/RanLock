package cc.ranmc.command;

import cc.ranmc.Main;
import cc.ranmc.util.Colorful;
import cc.ranmc.util.LoadTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            if (args[0].equalsIgnoreCase("reload")){
                if (sender.hasPermission("lock.admin")) {
                    LoadTask.start();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("reload")));
                    return true;
                } else {
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("permission")));
                }
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

        sender.sendMessage(Colorful.valueOf("&c未知指令"));
        return true;
    }
}
