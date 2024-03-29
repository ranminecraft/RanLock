package cc.ranmc.lock.util;

import cc.ranmc.lock.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;

public class DataUtil {
    public static String getStrByLoc(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() +
                "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static Location getLocByStr(String location) {
        String[] data = location.split(",");
        return new Location(Bukkit.getWorld(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
    }

    public static List<String> getTrustList(String playerName) {
        Main plugin = Main.getInstance();
        List<String> trustList;
        if (plugin.isEnableSqlite()) {
            trustList = plugin.getSqLite().selectTrustLst(playerName);
        } else {
            trustList = plugin.getTrustYaml().getStringList(playerName);
        }
        return trustList;
    }

    public static void lock(String playerName, Location location) {
        Main plugin = Main.getInstance();
        if (plugin.isEnableSqlite()) {
            plugin.getSqLite().insertLock(playerName, location);
        } else{
            plugin.getLockMap().put(DataUtil.getStrByLoc(location), playerName);
        }
    }

    public static void unlock(Location location) {
        Main plugin = Main.getInstance();
        if (plugin.isEnableSqlite()) {
            plugin.getSqLite().deleteLock(location);
        } else {
            plugin.getLockMap().remove(DataUtil.getStrByLoc(location));
        }
    }
}
