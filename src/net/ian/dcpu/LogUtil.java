package net.ian.dcpu;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class LogUtil {
	public static void sendError(CommandSender sender, String message) {
		display(sender, ChatColor.DARK_GREEN+"[DCPUCraft] " + ChatColor.RED + message);
	}

	public static void sendSuccess(CommandSender sender, String message) {
		display(sender, ChatColor.DARK_GREEN+"[DCPUCraft] " + ChatColor.YELLOW + message);
	}
	
	private static void display(CommandSender sender, String message) {
		if (sender != null && (sender instanceof Player) && ((Player) sender).isOnline())
			sender.sendMessage(message);
		else
			DCPUCraft.log.info(message);
	}
}
