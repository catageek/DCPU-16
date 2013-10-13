package net.ian.dcpu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

final class DCPUSessionManager {

	private final static Map<String,Listener> map = new HashMap<String, Listener>();

	static void addSession(String player, Listener listener) {
		map.put(player, listener);
		Bukkit.getServer().getPluginManager().registerEvents(listener, DCPUCraft.myPlugin);
	}

	static void removeSession(String player) {
		Listener listener = map.get(player);
		if (listener != null) {
			AsyncPlayerChatEvent.getHandlerList().unregister(listener);
			map.remove(player);
		}
	}
}
