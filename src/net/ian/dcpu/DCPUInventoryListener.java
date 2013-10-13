package net.ian.dcpu;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class DCPUInventoryListener implements Listener {

	private final Player Player;
	private final ModifiableRunnable<Inventory> Execute;

	public DCPUInventoryListener(DCPUCraft plugin, Player player, ModifiableRunnable<Inventory> execute) {
		this.Player = player;
		this.Execute = execute;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (event.getPlayer().equals(Player) && (block.getState() instanceof Chest)) {
			this.Execute.SetParam(block.getLocation().toString());
			this.Execute.run();
			event.setCancelled(true);
		}
		PlayerInteractEvent.getHandlerList().unregister(this);
	}
}


