package net.ian.dcpu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public final class DCPUListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onMapInitialize(MapInitializeEvent event) {
		MapView map = event.getMap();
		for (MapRenderer r : map.getRenderers())
		{
			map.removeRenderer(r);
		}
		
		map.addRenderer(new MonitorMapRenderer(DCPUCraft.myPlugin.monitor));
	}


}
