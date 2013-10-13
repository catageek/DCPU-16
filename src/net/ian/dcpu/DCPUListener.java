package net.ian.dcpu;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

final class DCPUListener implements Listener {

	private final Location loc = new Location(null, 0, 0, 0);
	private final DCPUCraft plugin = DCPUCraft.myPlugin;

	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item;
		String location;
		if (plugin.isDCPU(location = event.getClickedBlock().getLocation(loc).toString())
				&& (item = player.getItemInHand()) != null
				&& item.getType().equals(Material.MAP)) {
			MapView map = Bukkit.getServer().getMap(item.getDurability());

			if (plugin.addMapMonitor(location, map))
				LogUtil.sendSuccess(player, "Monitor attached to DCPU");
			player.sendMap(map);
			event.setCancelled(true);
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item;
		if ((item = player.getItemInHand()).getType().equals(Material.MAP)) {
			MapView map = Bukkit.getServer().getMap(item.getDurability());
			final DCPU dcpu = DCPUCraft.myPlugin.getOwningDCPU(map);
			if (dcpu != null) {
				KeyboardListener listener = new KeyboardListener(dcpu.getKeyboard());
				DCPUSessionManager.addSession(player.getName(), listener);
				return;
			}
		}
		DCPUSessionManager.removeSession(player.getName());

	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		removeDCPU(event.getBlock().getLocation(loc).toString(), event.getPlayer());
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		removeDCPU(event.getBlock().getLocation(loc).toString());
	}

	private void removeDCPU(String location, Player player) {
		if (plugin.removeDCPU(location))
			LogUtil.sendSuccess(player, "DCPU destroyed");
	}

	private void removeDCPU(String location) {
		plugin.removeDCPU(location);
	}
}
