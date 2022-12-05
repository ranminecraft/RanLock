package cc.ranmc;

import cc.ranmc.command.LockCommand;
import cc.ranmc.event.LockEvent;
import cc.ranmc.util.Colorful;
import cc.ranmc.util.LoadTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    public static final String PLUGIN = "RanLock";

    @Getter
    private final String PREFIX = Colorful.valueOf("&b" + PLUGIN + ">>>");

    /**
     * 数据文件
     */
    @Getter
    @Setter
    private YamlConfiguration lockYaml, trustYaml, langYaml;
    @Getter
    @Setter
    private HashMap<String, String> lockMap, trustMap;

    @Getter
    private static Main instance;

    @Getter
    private List<String> lockAction = new ArrayList<>();
    @Getter
    private List<String> unlockAction = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        // 加载配置
        LoadTask.start();

        // 注册 Event
        Bukkit.getPluginManager().registerEvents(new LockEvent(), this);

        // 注册 Command
        getCommand("lock").setExecutor(new LockCommand());
        getCommand("unlock").setExecutor(new LockCommand());
        getCommand("trust").setExecutor(new LockCommand());
        getCommand("untrust").setExecutor(new LockCommand());

        super.onEnable();
    }

    @Override
    public void onDisable() {

        // 保存数据
        LoadTask.end();

        super.onDisable();
    }
}
