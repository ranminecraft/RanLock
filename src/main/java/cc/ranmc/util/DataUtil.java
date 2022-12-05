package cc.ranmc.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DataUtil {
    public static String getStrByLoc(Location location) {
        StringBuilder builder = new StringBuilder(location.getWorld().getName());
        builder.append(",");
        builder.append(location.getBlockX());
        builder.append(",");
        builder.append(location.getBlockY());
        builder.append(",");
        builder.append(location.getBlockZ());
        return builder.toString();
    }

    public static Location getLocByStr(String location) {
        String[] data = location.split(",");
        return new Location(Bukkit.getWorld(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
    }
}
