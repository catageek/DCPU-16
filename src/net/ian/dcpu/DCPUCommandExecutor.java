package net.ian.dcpu;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


final class DCPUCommandExecutor implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("dcpurun")) {
			DCPU cpu = DCPUCraft.myPlugin.cpu;
			cpu.running = false;

			if (DCPUCraft.myPlugin.programBuffer == null) {
				sender.sendMessage("You must load a program first");
				return true;
			}

			cpu.clear(DCPUCraft.myPlugin.assembler.assemble(DCPUCraft.myPlugin.programBuffer));
			DCPUCraft.myPlugin.dcpuTaskId = Bukkit.getScheduler().runTaskAsynchronously(DCPUCraft.myPlugin, cpu);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("dcpustop")) {
			DCPUCraft.myPlugin.cpu.running = false;
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

		return false;
	}

}
