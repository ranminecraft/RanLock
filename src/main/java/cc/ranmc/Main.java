package cc.ranmc;

import cc.ranmc.command.LockCommand;
import cc.ranmc.event.GuiEvent;
import cc.ranmc.event.LockEvent;
import cc.ranmc.util.Colorful;
import cc.ranmc.util.LoadTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
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
    private YamlConfiguration lockYaml, trustYaml, langYaml, autoYaml;
    @Getter
    @Setter
    private HashMap<String, String> lockMap;

    @Getter
    private static Main instance;

    @Getter
    private List<String> lockAction = new ArrayList<>();
    @Getter
    private List<String> unlockAction = new ArrayList<>();

    @Getter
    @Setter
    private boolean residence = false;

    @Override
    public void onEnable() {
        instance = this;

        // 加载配置
        LoadTask.start();

        // 注册 Event
        Bukkit.getPluginManager().registerEvents(new LockEvent(), this);
        Bukkit.getPluginManager().registerEvents(new GuiEvent(), this);

        // 注册 Command
        CommandExecutor commandExecutor = new LockCommand();
        getCommand("lock").setExecutor(commandExecutor);
        getCommand("unlock").setExecutor(commandExecutor);
        getCommand("trust").setExecutor(commandExecutor);
        getCommand("untrust").setExecutor(commandExecutor);
        getCommand("lockauto").setExecutor(commandExecutor);

        super.onEnable();
    }

    @Override
    public void onDisable() {

        // 保存数据
        LoadTask.end();

        super.onDisable();
    }
}
