package net.ian.dcpu;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;


final class DCPUCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("dcpurun")) {
			DCPU cpu = DCPUCraft.myPlugin.cpu;

			if (DCPUCraft.myPlugin.programBuffer == null) {
				sender.sendMessage("You must load a program first");
				return true;
			}
			
			cpu.stop();
			cpu.start();

			return true;
		}

		if (cmd.getName().equalsIgnoreCase("dcpustop")) {
			DCPUCraft.myPlugin.cpu.stop();
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("dcpuload")) {

			if (args.length != 1)
				return false;

			String fileName = args[0];
			File dir = DCPUCraft.myPlugin.getDataFolder();
			File f;
			try {
				f = FileUtil.getSafeOpenFile(sender, dir, fileName, "dasm", "dasm");

				if (!f.exists()) {
					sender.sendMessage("Program " + fileName + " does not exist!");
					return true;
				}

				String filePath = f.getCanonicalPath();
				String dirPath = dir.getCanonicalPath();

				if (!filePath.substring(0, dirPath.length()).equals(dirPath)) {
					sender.sendMessage("Program could not read or it does not exist.");
				} else {

					byte[] encoded = Files.readAllBytes(Paths.get(filePath));
					DCPUCraft.myPlugin.programBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
					DCPUCraft.log.info(sender.getName() + " loaded " + filePath);
					sender.sendMessage(fileName + " loaded. Run it with /dcpurun");
				}
			} catch (IOException e) {
				sender.sendMessage("Program could not read or it does not exist: " + e.getMessage());
			}

			return true;
		}

		if (cmd.getName().equalsIgnoreCase("dcpumonitor")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				Player player = (Player) sender;
				ItemStack item;
				if (! (item = player.getItemInHand()).getType().equals(Material.MAP))
					player.sendMessage("You must hold a map in hand before typing this command.");
				else {
					MapView map = Bukkit.getServer().getMap(item.getDurability());
					for (MapRenderer r : map.getRenderers())
					{
						map.removeRenderer(r);
					}

					map.addRenderer(new MonitorMapRenderer(DCPUCraft.myPlugin.monitor));
					player.sendMap(map);
				}
			}
			return true;

		}


		return false;
	}

}
