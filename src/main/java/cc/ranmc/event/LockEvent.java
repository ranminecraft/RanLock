package cc.ranmc.event;

import cc.ranmc.Main;
import cc.ranmc.util.BlockUtil;
import cc.ranmc.util.Colorful;
import cc.ranmc.util.DataUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class LockEvent implements Listener {

    private Main plugin = Main.getInstance();

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (plugin.getConfig().getStringList("lock-block").contains(block.getType().toString())) {
            plugin.getLockMap().put(DataUtil.getStrByLoc(block.getLocation()), player.getName());
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("place")));
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        String owner = BlockUtil.getOwner(block);
        if (owner.isEmpty()) return;
        if (player.getName().equalsIgnoreCase(BlockUtil.getOwner(block))) {
            plugin.getLockMap().remove(DataUtil.getStrByLoc(block.getLocation()));
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("break")));
        } else {
            event.setCancelled(true);
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("cant-break")).replace("%owner%", owner));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;
        String owner = BlockUtil.getOwner(block);
        if (owner.isEmpty()) {
            if (plugin.getLockAction().contains(player.getName())) {
                event.setCancelled(true);
                plugin.getLockAction().remove(player.getName());
                if (!plugin.getConfig().getStringList("lock-block").contains(block.getType().toString())) {
                    player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("cant-lock")));
                    return;
                }
                plugin.getLockMap().put(DataUtil.getStrByLoc(block.getLocation()), player.getName());
                player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("place")));
            }
            return;
        }
        if (!player.getName().equalsIgnoreCase(owner)) {
            event.setCancelled(true);
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("cant-open")).replace("%owner%", owner));
        } else if (plugin.getUnlockAction().contains(player.getName())) {
            event.setCancelled(true);
            plugin.getLockMap().remove(DataUtil.getStrByLoc(block.getLocation()));
            player.sendMessage(Colorful.valueOf(plugin.getLangYaml().getString("break")));
        }
        plugin.getUnlockAction().remove(player.getName());
        plugin.getLockAction().remove(player.getName());

    }

    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (BlockUtil.isLock(block)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (BlockUtil.isLock(block)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onStructureGrowEvent(StructureGrowEvent event) {
        for (BlockState blockState : event.getBlocks()) {
            if (BlockUtil.isLock(blockState.getBlock())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (BlockUtil.isLock(event.getBlock())) event.setNewCurrent(event.getOldCurrent());
    }

    @EventHandler
    public void onMobChangeBlock(EntityChangeBlockEvent event) {
        if (BlockUtil.isLock(event.getBlock())) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        if (!BlockUtil.isLock(event.getSource().getLocation().getBlock())) return;
        if (event.getDestination().getHolder() instanceof HopperMinecart || event.getDestination().getHolder() instanceof org.bukkit.block.Hopper) {
            event.setCancelled(true);
        }
    }
}
