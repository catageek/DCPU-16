package net.ian.dcpu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public final class DCPUCraft extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	public static DCPUCraft myPlugin;
	public static String logPrefix = "DCPUCraft.";

	private final Map<String,String> names = new HashMap<String,String>(); 
	private final Map<String,DCPU> cpus = new HashMap<String,DCPU>(); 
	DCPU cpu;
	MonitorMapRenderer display;

	Assembler assembler;
	String programBuffer;


	public void onEnable(){
		log.info("DCPUCraft plugin has been enabled.");

		myPlugin = this;
		assembler = new Assembler();

		getCommand("dcpurun").setExecutor(new DCPUCommandExecutor());
		getCommand("dcpustop").setExecutor(new DCPUCommandExecutor());
		getCommand("dcpuload").setExecutor(new DCPUCommandExecutor());
		getCommand("dcpumonitor").setExecutor(new DCPUCommandExecutor());
		getCommand("dcpu").setExecutor(new DCPUCommandExecutor());
		this.getServer().getPluginManager().registerEvents(new DCPUListener(), this);

		//		this.saveDefaultConfig();
	}

	public void onDisable(){ 
		log.info("DCPUCraft plugin has been disabled.");

		myPlugin = null;
		log = null;

	}

	boolean addDCPU(String location, String name) {
		if (cpus.containsKey(location) || names.containsKey(name))
			return false;
		cpu = new DCPU(name);
		names.put(location,name);
		cpus.put(name, cpu);
		cpu.addDevices();

		return true;
	}

	boolean removeDCPU(String location) {
		String name;
		if (names.containsKey(location)) {
			name = names.get(location);
			DCPU c = cpus.get(name);
			c.stop();
			c.restoreRenderers();
			cpus.remove(name);
			names.remove(location);
			return true;
		}
		return false;
	}

	boolean isDCPU(String location) {
		return names.containsKey(location);
	}

	boolean addMapMonitor(String location, MapView map) {
		if (isDCPU(location)) {
			cpu = cpus.get(names.get(location));
			List<MapRenderer> renderers = map.getRenderers();
			MonitorMapRenderer mapmonitor = new MonitorMapRenderer(cpu.getMonitor());
			

			for (MapRenderer r : renderers)
			{
				map.removeRenderer(r);
			}

			cpu.addMonitorRenderer(map, renderers);
			map.addRenderer(mapmonitor);
			return true;
		}
		return false;
	}

	DCPU getOwningDCPU(MapView map) {
		Iterator<DCPU> it = cpus.values().iterator();
		while (it.hasNext()) {
			DCPU mycpu;
			if ((mycpu = it.next()).isMonitor(map))
				return mycpu;
		}
		return null;
	}
}
