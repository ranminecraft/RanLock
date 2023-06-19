package cc.ranmc.lock;

import cc.ranmc.lock.command.LockCommand;
import cc.ranmc.lock.listener.GUIListener;
import cc.ranmc.lock.listener.BlockListener;
import cc.ranmc.lock.sqlite.SQLite;
import cc.ranmc.lock.util.Colorful;
import cc.ranmc.lock.util.LoadTask;
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
import java.util.Objects;

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
    private final List<String> lockAction = new ArrayList<>();
    @Getter
    private final List<String> unlockAction = new ArrayList<>();

    @Getter
    @Setter
    private boolean residence = false;
    @Getter
    @Setter
    private SQLite sqLite;
    @Getter
    @Setter
    private boolean enableSqlite = false;

    @Override
    public void onEnable() {
        instance = this;

        // 加载配置
        LoadTask.start();

        // 注册 Event
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);

        // 注册 Command
        CommandExecutor commandExecutor = new LockCommand();
        Objects.requireNonNull(getCommand("lock")).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand("unlock")).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand("trust")).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand("untrust")).setExecutor(commandExecutor);
        Objects.requireNonNull(getCommand("lockauto")).setExecutor(commandExecutor);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        // 保存数据
        LoadTask.end();
        //关闭数据库
        if (sqLite != null) sqLite.close();
        super.onDisable();
    }
}
