package net.ian.dcpu;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MonitorMapRenderer extends MapRenderer {

	public static final int SCALE = 1;

	public static final int WIDTH = Monitor.WIDTH * SCALE;
	public static final int HEIGHT = Monitor.HEIGHT * SCALE;

	private BufferedImage screen;

	private AffineTransformOp scaler;

	public Monitor monitor;

	public MonitorMapRenderer(Monitor m) {
		monitor = m;

		screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		AffineTransform scale = new AffineTransform();
		scale.scale(SCALE, SCALE);
		scaler = new AffineTransformOp(scale, null);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (monitor.update()) {
			Graphics2D g = screen.createGraphics();
			g.drawImage(monitor.screen, scaler, 0, 0);
			g.dispose();

			canvas.drawImage(0, 0, screen);
			player.sendMap(map);
		}
	}
}
