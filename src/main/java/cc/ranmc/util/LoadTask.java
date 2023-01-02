package cc.ranmc.util;

import cc.ranmc.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 加载配置文件
 */
public class LoadTask {

    private static File lockFile, trustFile, langFile, autoFile;
    private static Main plugin = Main.getInstance();

    public static void start() {
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&e-----------------------"));
        Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&b" + plugin.PLUGIN + " &dBy阿然"));
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

        plugin.setLockMap(new HashMap());
        for (String key : plugin.getLockYaml().getKeys(false)) {
            if (!key.contains("zy") && !key.contains("world_the_end") && !key.contains("world_nether")) plugin.getLockMap().put(key, plugin.getLockYaml().get(key).toString());
        }

        trustFile = new File(plugin.getDataFolder(), "trust.yml");
        if (!trustFile.exists()) plugin.saveResource("trust.yml", true);
        plugin.setTrustYaml(YamlConfiguration.loadConfiguration(trustFile));

        langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists()) plugin.saveResource("lang.yml", true);
        plugin.setLangYaml(YamlConfiguration.loadConfiguration(langFile));

        if (Bukkit.getPluginManager().getPlugin("Residence") != null) {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&a成功加载Residence"));
            plugin.setResidence(true);
        } else {
            Bukkit.getConsoleSender().sendMessage(Colorful.valueOf("&c无法找到Residence"));
        }
    }

    public static void end() {
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
