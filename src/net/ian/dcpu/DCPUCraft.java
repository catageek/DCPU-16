package net.ian.dcpu;

import java.util.logging.Logger;


import org.bukkit.plugin.java.JavaPlugin;

public final class DCPUCraft extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	public static DCPUCraft myPlugin;
	public static String logPrefix = "DCPUCraft.";
	
	final DCPU cpu = new DCPU();
	Keyboard keyboard;
	Monitor monitor;
	Clock clock;
	MonitorMapRenderer display;
	
	Assembler assembler;
	String programBuffer;


	public void onEnable(){
		log.info("DCPUCraft plugin has been enabled.");

		myPlugin = this;

		keyboard = new Keyboard(cpu);
		monitor = new Monitor(cpu);
		clock = new Clock(cpu);
		
		assembler = new Assembler();

       	getCommand("dcpurun").setExecutor(new DCPUCommandExecutor());
       	getCommand("dcpustop").setExecutor(new DCPUCommandExecutor());
       	getCommand("dcpuload").setExecutor(new DCPUCommandExecutor());
       	getCommand("dcpumonitor").setExecutor(new DCPUCommandExecutor());

//		this.saveDefaultConfig();
}

	public void onDisable(){ 
		log.info("DCPUCraft plugin has been disabled.");

		myPlugin = null;
		log = null;

	}
}
