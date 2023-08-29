package cc.ranmc.lock.util;

import cc.ranmc.bean.Residence;
import cc.ranmc.constant.Flag;
import cc.ranmc.utils.ResidenceUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ResCheck {

    public static boolean hasPermission(Player player, Block block) {
        Residence res = ResidenceUtil.getResidence(block.getLocation());
        if (res == null) return true;
        return res.checkFlag(player, Flag.BUILD);
    }
}

