package cc.ranmc.lock.util;

import cc.ranmc.lock.Main;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResCheck {

    public static boolean hasPermissom(Player player, Block block) {
        if (Main.getInstance().isResidence()) {
            ClaimedResidence claimedResidence = ResidenceApi.getResidenceManager().getByLoc(block.getLocation());
            if (claimedResidence == null) return true;
            List<String> permList = Arrays.asList(removeBrackets(claimedResidence.getPermissions().listPlayersFlags().replace("§f", "")).split(" "));
            Boolean perm = false;
            if (permList.contains(player.getName())) {
                perm = claimedResidence.getPermissions().getPlayerFlags(player.getName()).get("place");
            }
            return (perm != null && perm) || claimedResidence.getOwner().equalsIgnoreCase(player.getName());
        }
        return true;
    }

    /**
     * 删除中括号文本
     * @param text 文本
     * @return 文本
     */
    public static String removeBrackets(String text) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("(\\[[^]]*])");
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
        }
        for (String tmp : list) {
            text = text.replace("[" + tmp + "]", "");
        }
        return text;
    }
}

