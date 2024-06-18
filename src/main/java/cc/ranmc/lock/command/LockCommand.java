package cc.ranmc.lock.command;

import cc.ranmc.lock.Main;
import cc.ranmc.lock.util.Colorful;
import cc.ranmc.lock.util.GUIUtil;
import cc.ranmc.lock.util.DataUtil;
import cc.ranmc.lock.util.ActionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                    DataUtil.start();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("reload", "重载成功")));
                    return true;
                }
                if (args[0].equalsIgnoreCase("save")) {
                    DataUtil.save();
                    sender.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("save", "保存成功")));
                    return true;
                }
            }
        }

        if (!(sender instanceof Player player)) {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("console", "该指令不能在控制台输入")));
            return true;
        }

        /*
         * 鉴权
         */
        if (!sender.hasPermission("lock.user")) {
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("permission", "权限不足")));
            return true;
        }

        /*
         * 菜单
         */
        if (cmd.getName().equalsIgnoreCase("lock") && args.length == 1 && args[0].equals("gui")) {
            GUIUtil.open(player);
            return true;
        }

        /*
         * 自动上锁开关
         */
        if (cmd.getName().equalsIgnoreCase("lockauto")) {
            ActionUtil.lockauto(player);
            return true;
        }

        /*
         * 上锁
         */
        if (cmd.getName().equalsIgnoreCase("lock")) {
            ActionUtil.lock(player);
            return true;
        }

        /*
         * 解锁
         */
        if (cmd.getName().equalsIgnoreCase("unlock")) {
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
