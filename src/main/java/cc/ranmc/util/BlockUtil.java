package cc.ranmc.util;

import cc.ranmc.Main;
import org.bukkit.block.Block;

public class BlockUtil {
    public static boolean isLock(Block block) {
        if (Main.getInstance().getLockMap().containsKey(DataUtil.getStrByLoc(block.getLocation()))) return true;
        return false;
    }

        public static String getOwner(Block block) {
        if (isLock(block)) return Main.getInstance().getLockMap().get(DataUtil.getStrByLoc(block.getLocation()));
        return "";
    }
}
