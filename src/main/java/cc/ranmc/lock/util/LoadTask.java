package cc.ranmc.lock.util;

import cc.ranmc.lock.Main;
import cc.ranmc.lock.sqlite.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 加载配置文件
 */
public class LoadTask {

    private static File lockFile;
    private static File trustFile;
    private static File autoFile;
    private static final Main plugin = Main.getInstance();

    public static void start() {
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&e-----------------------"));
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&b" + Main.PLUGIN + " &dBy阿然"));
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&b插件版本:" + plugin.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&b服务器版本:" + plugin.getServer().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&chttps://www.ranmc.cc/"));
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&e-----------------------"));

        if (!new File(plugin.getDataFolder() + File.separator + "config.yml").exists()) {
            plugin.saveDefaultConfig();
        }
        plugin.reloadConfig();

        autoFile = new File(plugin.getDataFolder(), "auto.yml");
        if (!autoFile.exists()) plugin.saveResource("auto.yml", true);
        plugin.setAutoYaml(YamlConfiguration.loadConfiguration(autoFile));

        lockFile = new File(plugin.getDataFolder(), "lock.yml");
        if (!lockFile.exists()) plugin.saveResource("lock.yml", true);
        plugin.setLockYaml(YamlConfiguration.loadConfiguration(lockFile));
        plugin.setLockMap(new HashMap<>());

        plugin.setEnableSqlite(plugin.getConfig().getBoolean("sqlite", false));
        if (plugin.isEnableSqlite()) {
            plugin.setEnableSqlite(true);
            plugin.setSqLite(new SQLite(plugin.getDataFolder().getPath() + "/data.db").createTable());
            for (Map<String, String> map : plugin.getSqLite().selectLockList()) {
                String key = map.get("World") + "," + map.get("X") + "," + map.get("Y") + "," + map.get("Z");
                plugin.getLockMap().put(key, map.get("Player"));
            }
        } else {
            plugin.setEnableSqlite(false);
            for (String key : plugin.getLockYaml().getKeys(false)) {
                plugin.getLockMap().put(key, plugin.getLockYaml().get(key).toString());
            }
        }

        trustFile = new File(plugin.getDataFolder(), "trust.yml");
        if (!trustFile.exists()) plugin.saveResource("trust.yml", true);
        plugin.setTrustYaml(YamlConfiguration.loadConfiguration(trustFile));

        File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) plugin.saveResource("lang.yml", true);
        plugin.setLangYaml(YamlConfiguration.loadConfiguration(langFile));

        if (Bukkit.getPluginManager().getPlugin("Ranmc") != null) {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&a成功加载Ranmc"));
            plugin.setResidence(true);
        } else {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&c无法找到Ranmc"));
        }
    }

    public static void end() {
        if (!plugin.isEnableSqlite()) {
            YamlConfiguration yaml = new YamlConfiguration();
            for (String key : plugin.getLockMap().keySet()) {
                yaml.set(key, plugin.getLockMap().get(key));
            }
            try {
                yaml.save(lockFile);
                plugin.getTrustYaml().save(trustFile);
                plugin.getAutoYaml().save(autoFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
