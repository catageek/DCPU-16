package net.ian.dcpu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class KeyboardListener implements Listener {



	private final Keyboard keyboard;

	public KeyboardListener(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		char[] chars = e.getMessage().toCharArray();
		synchronized(keyboard) {
			for (int i = 0; i < chars.length; i++)
				keyboard.addKey(chars[i]);
			keyboard.shouldInterrupt = true;
		}
	}

}
