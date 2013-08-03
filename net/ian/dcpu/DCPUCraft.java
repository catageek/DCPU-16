package net.ian.dcpu;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class DCPUCraft extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	public static DCPUCraft myPlugin;
	public static String logPrefix = "DCPUCraft.";

	public void onEnable(){
		log.info("DCPUCraft plugin has been enabled.");

		myPlugin = this;

//		this.saveDefaultConfig();
}

	public void onDisable(){ 
		log.info("DCPUCraft plugin has been disabled.");

		myPlugin = null;
		log = null;

	}
}
