package net.ian.dcpu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.ian.dcpu.DCPU.Register;

public class Monitor extends Hardware implements MemoryListener {
	public static final int ID = 0x7349f615;
	public static final int VERSION = 0x1802;
	public static final int MANUFACTURER = 0x1c6c8b36;
	
	public static final int COLUMNS = 32;
	public static final int ROWS = 12;
	
	public static final int CHAR_WIDTH = 4;
	public static final int CHAR_HEIGHT = 8;
	
	public static final int BORDER = 12;	
	public static final int WIDTH = COLUMNS * CHAR_WIDTH + BORDER * 2;
	public static final int HEIGHT = ROWS * CHAR_HEIGHT + BORDER * 2;
	
	public BufferedImage screen;
	
	public BufferedImage font[];
	public Color palette[];
	public MonitorCell cells[];
	
	static Color defaultPalette[] = new Color[16];
	
	static {
		for (int i = 0; i < 16; i++) {
			boolean h = (i >> 3 & 1) == 1;
			
			int r = 0xAA * (i >> 2 & 1) + (h ? 0x55 : 0);
			int g = 0xAA * (i >> 1 & 1) + (h ? 0x55 : 0);
			int b = 0xAA * (i >> 0 & 1) + (h ? 0x55 : 0);
			
			if ((i & 0xf) == 6)
				g -= 0x55;
			
			defaultPalette[i] = new Color(r, g, b);
		}
	}
	
	public Color borderColor = Color.BLACK;
		
	private DCPU cpu;
	private char memStart, fontStart, paletteStart;

	public boolean shouldRender;
	
	public static class MonitorCell {
		char character;
		Color fgColor, bgColor;
		boolean blink, show;
		
		public MonitorCell(char c, Color fg, Color bg, boolean blink) {
			character = c;
			fgColor = fg;
			bgColor = bg;
			this.blink = blink;
			show = false;
		}
	}
	
	public Monitor(DCPU cpu) {
        super(ID, VERSION, MANUFACTURER);
		
        screen = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.cpu = cpu;
        
        cells = new MonitorCell[32 * 12];
        for (int i = 0; i < 32 * 12; i++)
        	cells[i] = new MonitorCell((char)0, Color.BLACK, Color.BLACK, false);
        
		font = loadDefaultFont();
		palette = Arrays.copyOf(defaultPalette, defaultPalette.length);
                
        cpu.attachDevice(this);
        cpu.addListener(this);
    }
	
	public BufferedImage[] loadDefaultFont() {
		BufferedImage img;
		try {
		img = ImageIO.read(DCPU.class.getResource("/net/ian/dcpu/res/font.png"));
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g = img2.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
				
		BufferedImage[] defaultFont = new BufferedImage[img2.getWidth() / 4 * (img2.getHeight() / 8)];
		
		for (int x = 0; x < img2.getWidth() / 4; x++) {
			for (int y = 0; y < img2.getHeight() / 8; y++) {
				defaultFont[y * 32 + x] = img2.getSubimage(x * 4, y * 8, 4, 8);
			}
		}
		
		return defaultFont;
	}
	
	public void buildFont(int location, char word) {
		// Actually builds half a font, because a full font takes two words.
		BufferedImage fontChar = font[location / 2];
		int half = location % 2;
		
		for (int col = 0; col < 2; col++) {
			for (int row = 0; row < 8; row++) {
				int color = ((word >> ((1 - col) * 8 + row)) & 1) == 1 ? Color.WHITE.getRGB() : Color.BLACK.getRGB();
				fontChar.setRGB(col + (half * 2), row, color);
			}
		}
	}
	
	public void buildPalette(int index, char word) {
		int red   = word >> 8;
        int green = word >> 4 & 0xf;
        int blue  = word & 0xf;
        
        // 255 / 15 = 17
        palette[index] = new Color(red * 17, green * 17, blue * 17);
	}
	
	public void dumpFont(char start, BufferedImage[] font) {
		// Stick a font in DCPU memory. For use with MEM_DUMP_FONT.
		for (int i = 0; i < font.length; i++) {
			char word = 0;
			for (int x = 0; x < font[i].getWidth(); x++) {
				for (int y = 0; y < font[i].getHeight(); y++) {
					if (font[i].getRGB(x, y) == Color.WHITE.getRGB())
						word |= 1 << ((3 - x) * 8 + y);
					char word2 = (char)(word & 0xffff);
					char word1 = (char)(word >> 16);
					cpu.memory[start + (i * 2)].set(word1);
					cpu.memory[start + (i * 2) + 1].set(word2);
				}
			}
		}
	}
	
	public void dumpPalette(char start, Color[] palette) {
		// Stick a palette in DCPU memory. For use with MEM_DUMP_PALETTE.
		for (int i = 0; i < palette.length; i++) {
			char word = 0;
			word &= (palette[i].getRed() / 17) << 8;
			word &= (palette[i].getGreen() / 17) << 4;
			word &= palette[i].getBlue() / 17;
			cpu.memory[start + i].set(word);
		}
	}

	private BufferedImage replaceColor(BufferedImage img, Color fgColor, Color bgColor) {
		int fgColorRGB = fgColor.getRGB();
		int bgColorRGB = bgColor.getRGB();
		
		BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img2.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		for (int x = 0; x < img2.getWidth(); x++) {
			for (int y = 0; y < img2.getHeight(); y++) {
				int rgb = img2.getRGB(x, y);
				if (rgb == Color.BLACK.getRGB())
					img2.setRGB(x, y, bgColorRGB);
				else
					img2.setRGB(x, y, fgColorRGB);
			}
		}
		return img2;
	}
	
	// Returns whether the screen was updated.
	public boolean update() {
		if (System.currentTimeMillis() % 100 == 0) {
			for (int x = 0; x < COLUMNS; x++) {
				for (int y = 0; y < ROWS; y++) {
					MonitorCell cell = cells[y * 32 + x];
					if (cell.blink) {
						setShouldRender(true);
						cell.show = !cell.show;
					}
				}
			}
		}
		
		if (getShouldRender()) {
			render();
			setShouldRender(false);
			return true;
		}
		return false;
	}
	
	public synchronized void render() {
		Graphics2D g = screen.createGraphics();
		
		g.setColor(borderColor);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		for (int x = 0; x < COLUMNS; x++) {
			for (int y = 0; y < ROWS; y++) {
				MonitorCell cell = cells[y * 32 + x];
				if (!cell.show) {
					g.setColor(Color.BLACK);
					g.fillRect(x * 4 + BORDER, y * 8 + BORDER, 4, 8);
				} else if (cell.fgColor.equals(cell.bgColor)) {
					g.setColor(cell.bgColor);
					g.fillRect(x * 4 + BORDER, y * 8 + BORDER, 4, 8);
				} else
					g.drawImage(replaceColor(font[cell.character], cell.fgColor, cell.bgColor), x * 4 + BORDER, y * 8 + BORDER, null);
			}
		}
		
		g.dispose();
	}
	
	@Override
	public boolean inMemoryRange(char loc) {
		return (loc >= memStart && loc < memStart + 0x180)
				|| (fontStart != 0 && (loc >= fontStart && loc < fontStart + 0x280))
				|| (paletteStart != 0 && (loc >= paletteStart && loc < paletteStart + 0xf));
	}
	
	@Override
	public void onSet(char location, char value) {
		if (location >= memStart && location < (memStart + 0x180)) {
    		MonitorCell cell = cells[location - memStart];
    		cell.character = (char)(value & 127);
    		cell.fgColor = palette[value >> 12];
    		cell.bgColor = palette[value >> 8 & 0xf];
    		cell.blink = (value >> 7 & 1) == 1;
    		cell.show = true;
		} else if (fontStart != 0 && location >= fontStart && location < (fontStart + 0x280)) {
			// Builds half a font
			buildFont(location - fontStart, value);
		} else if (paletteStart != 0 && location >= paletteStart && location < (paletteStart + 0xf)) {
			buildPalette(location - paletteStart, value);
		}
		setShouldRender(true);
	}

	@Override
	public void onGet(char location, char value) {}
	
	public synchronized boolean getShouldRender() {
		return shouldRender;
	}
	
	public synchronized void setShouldRender(boolean value) {
		shouldRender = value;
	}
	
	public void interrupt() {
		char b = cpu.getRegister(Register.B).value;
		switch (cpu.getRegister(Register.A).value) {
		case 0: // MEM_MAP_SCREEN
			memStart = b;
			for (int i = 0; i < 0x180; i++) {
				char value = cpu.memory[memStart + i].value;
				MonitorCell cell = cells[i];
	    		cell.character = (char)(value & 127);
	    		cell.fgColor = palette[value >> 12];
	    		cell.bgColor = palette[value >> 8 & 0xf];
	    		cell.blink = (value >> 7 & 1) == 1;
	    		cell.show = true;
			}
			setShouldRender(true);
			break;
		case 1: // MEM_MAP_FONT
			fontStart = b;
			if (b == 0)
		        font = loadDefaultFont();
			else {
				for (int i = 0; i < 0x100 && fontStart + i < 0x10000; i++)
					buildFont(i, cpu.memory[fontStart + i].value);
			}
			setShouldRender(true);
			break;
		case 2: // MEM_MAP_PALETTE
			paletteStart = b;
			if (b == 0)
				palette = Arrays.copyOf(defaultPalette, defaultPalette.length);
			else {
				for (int i = 0; i < 0x10; i++)
					buildPalette(i, cpu.memory[paletteStart + i].value);
			}
			break;
		case 3: // SET_BORDER_COLOR
			borderColor = palette[b];
			setShouldRender(true);
			break;
		case 4: // MEM_DUMP_FONT
			dumpFont(b, loadDefaultFont());
			cpu.cycles += 256;
			break; 
		case 5: // MEM_DUMP_PALETTE
			dumpPalette(b, defaultPalette);
			cpu.cycles += 16;
			break;
		}
	}
}
