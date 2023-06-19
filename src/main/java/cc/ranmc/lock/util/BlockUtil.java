package cc.ranmc.lock.util;

import cc.ranmc.lock.Main;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockUtil {
    private final static Main plugin = Main.getInstance();
    public static String getOwner(Block block) {
        Location location = block.getLocation();
        return plugin.getLockMap().getOrDefault(DataUtil.getStrByLoc(location), null);
    }

    public static boolean isLock(Block block) {
        Location location = block.getLocation();
        return plugin.getLockMap().getOrDefault(DataUtil.getStrByLoc(location), null) != null;
    }
}
