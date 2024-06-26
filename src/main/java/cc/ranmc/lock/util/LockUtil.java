package cc.ranmc.lock.util;

import cc.ranmc.lock.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;

public class LockUtil {
    public static String getStrByLoc(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();
    }

    public static Location getLocByStr(String location) {
        String[] data = location.split(",");
        return new Location(Bukkit.getWorld(data[0]),
                Integer.parseInt(data[1]),
                Integer.parseInt(data[2]),
                Integer.parseInt(data[3]));
    }

    public static List<String> getTrustList(String playerName) {
        return Main.getInstance().getTrustYaml().getStringList(playerName);
    }

    public static void lock(String playerName, Location location) {
        Main.getInstance().getLockMap().put(LockUtil.getStrByLoc(location), playerName);
    }

    public static void unlock(Location location) {
        Main.getInstance().getLockMap().remove(LockUtil.getStrByLoc(location));
    }
}
