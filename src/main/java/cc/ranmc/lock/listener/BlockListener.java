package cc.ranmc.lock.listener;

import cc.ranmc.lock.Main;
import cc.ranmc.lock.util.BlockUtil;
import cc.ranmc.lock.util.Colorful;
import cc.ranmc.lock.util.DataUtil;
import cc.ranmc.lock.util.ResCheck;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockListener implements Listener {

    private final Main plugin = Main.getInstance();

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (!plugin.getConfig().getStringList("enable-world").contains(block.getWorld().getName())) return;
        Player player = event.getPlayer();
        if (plugin.isEnableSqlite()) {
            if (!plugin.getSqLite().selectAuto(player)) return;
        } else if (plugin.getAutoYaml().getStringList("off").contains(player.getName())) return;
        if (plugin.getConfig().getStringList("lock-block").contains(block.getType().toString())) {
            DataUtil.lock(player.getName(), block.getLocation());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("place")));
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();
        if (!plugin.getConfig().getStringList("enable-world").contains(block.getWorld().getName())) return;
        Player player = event.getPlayer();
        String owner = BlockUtil.getOwner(block);
        if (owner == null) return;
        if (player.getName().equalsIgnoreCase(BlockUtil.getOwner(block)) || plugin.getTrustYaml().getStringList(owner).contains(player.getName()) || player.hasPermission("lock.admin")) {
            DataUtil.unlock(block.getLocation());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("break")));
        } else {
            event.setCancelled(true);
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("cant-break")).replace("%owner%", owner));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;
        String owner = BlockUtil.getOwner(block);
        if (owner == null) {
            if (plugin.getLockAction().contains(player.getName())) {
                event.setCancelled(true);
                plugin.getLockAction().remove(player.getName());
                if (!plugin.getConfig().getStringList("enable-world").contains(block.getWorld().getName())) {
                    player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("disabled-world")));
                    return;
                }
                if (!plugin.getConfig().getStringList("lock-block").contains(block.getType().toString())) {
                    player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("cant-lock")));
                    return;
                }
                if (!ResCheck.hasPermission(player, block)) {
                    player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("residence")));
                    event.setCancelled(true);
                    return;
                }
                DataUtil.lock(player.getName(), block.getLocation());
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("place")));
            }
            return;
        }
        if (player.getName().equalsIgnoreCase(owner) || DataUtil.getTrustList(owner).contains(player.getName()) || player.hasPermission("lock.admin")) {
            if (plugin.getUnlockAction().contains(player.getName())) {
                event.setCancelled(true);
                DataUtil.unlock(block.getLocation());
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("break")));
            }
        } else {
            event.setCancelled(true);
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("cant-open")).replace("%owner%", owner));
            }
        }
        plugin.getUnlockAction().remove(player.getName());
        plugin.getLockAction().remove(player.getName());
    }

    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        if (!plugin.getConfig().getStringList("enable-world").contains(event.getBlock().getWorld().getName())) return;
        for (Block block : event.getBlocks()) {
            if (BlockUtil.isLock(block)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
        if (!plugin.getConfig().getStringList("enable-world").contains(event.getBlock().getWorld().getName())) return;
        for (Block block : event.getBlocks()) {
            if (BlockUtil.isLock(block)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onStructureGrowEvent(StructureGrowEvent event) {
        if (!plugin.getConfig().getStringList("enable-world").contains(event.getLocation().getWorld().getName())) return;
        for (BlockState blockState : event.getBlocks()) {
            if (BlockUtil.isLock(blockState.getBlock())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (!plugin.getConfig().getStringList("enable-world").contains(block.getWorld().getName())) return;
        if (plugin.getConfig().getStringList("lock-block").contains(block.getType().toString())) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    @EventHandler
    public void onMobChangeBlock(EntityChangeBlockEvent event) {
        if (!plugin.getConfig().getStringList("enable-world").contains(event.getBlock().getWorld().getName())) return;
        if (BlockUtil.isLock(event.getBlock())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        Location location = event.getSource().getLocation();
        if (location == null) return;
        if (!BlockUtil.isLock(location.getBlock())) return;
        if (event.getDestination().getHolder() instanceof HopperMinecart || event.getDestination().getHolder() instanceof org.bukkit.block.Hopper) {
            event.setCancelled(true);
        }
    }
}
