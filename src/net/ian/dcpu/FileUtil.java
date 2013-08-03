package net.ian.dcpu;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandSender;

final class FileUtil {
	/**
	 * Gets the path to a file. This method will check to see if the filename
	 * has valid characters and has an extension. It also prevents directory
	 * traversal exploits by checking the root directory and the file directory.
	 * On success, a <code>java.io.File</code> object will be returned.
	 *
	 * @param player
	 * @param dir sub-directory to look in
	 * @param filename filename (user-submitted)
	 * @param defaultExt append an extension if missing one, null to not use
	 * @param extensions list of extensions, null for any
	 * @return
	 * @throws IOException
	 */
	public static File getSafeOpenFile(CommandSender player, File dir, String filename,
			String defaultExt, String... extensions)
					throws IOException {
		return getSafeFile(player, dir, filename, defaultExt, extensions, false);
	}

	/**
	 * Get a safe path to a file.
	 *
	 * @param player
	 * @param dir
	 * @param filename
	 * @param defaultExt
	 * @param extensions
	 * @param isSave
	 * @return
	 * @throws IOException
	 */
	private static File getSafeFile(CommandSender player, File dir, String filename,
			String defaultExt, String[] extensions, boolean isSave)
					throws IOException {
		if (extensions != null && (extensions.length == 1 && extensions[0] == null)) extensions = null;

		File f;


		if (defaultExt != null && filename.lastIndexOf('.') == -1) {
			filename += "." + defaultExt;
		}

		if (!filename.matches("^[A-Za-z0-9_\\- \\./\\\\'\\$@~!%\\^\\*\\(\\)\\[\\]\\+\\{\\},\\?]+\\.[A-Za-z0-9]+$")) {
			throw new IOException("Invalid characters or extension missing");
		}

		f = new File(dir, filename);

		try {
			String filePath = f.getCanonicalPath();
			String dirPath = dir.getCanonicalPath();

			if (!filePath.substring(0, dirPath.length()).equals(dirPath)) {
				throw new IOException("Path is outside allowable root");
			}

			return f;
		} catch (IOException e) {
			throw new IOException("Failed to resolve path");
		}
	}



}
