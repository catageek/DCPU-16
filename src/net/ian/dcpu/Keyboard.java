package net.ian.dcpu;

import java.util.HashMap;
import java.util.Map;

import net.ian.dcpu.DCPU.Register;

public class Keyboard extends Hardware {
	public static final int ID = 0x30cf7406;
	public static final int VERSION = 1;
	public static final int MANUFACTURER = 0xCC_743_CA7;
	
	private static Map<Integer, Integer> keyMap = new HashMap<>();
	private char[] keyring = new char[64];
	private int keyPushPtr = 0;
	private int keyAccessPtr = 0;
	private char interruptMsg;
	boolean shouldInterrupt;
	
	DCPU cpu;
	
	static {
		keyMap.put(  8, 0x10); // BACKSPACE
		keyMap.put( 10, 0x11); // RETURN
		keyMap.put(155, 0x12); // INSERT
		keyMap.put(127, 0x13); // DELETE
		keyMap.put( 38, 0x80); // ARROW UP
		keyMap.put( 40, 0x81); // ARROW DOWN
		keyMap.put( 37, 0x82); // ARROW LEFT
		keyMap.put( 39, 0x83); // ARROW RIGHT
		keyMap.put( 16, 0x90); // SHIFT
		keyMap.put( 17, 0x91); // CONTROL
	}
	
	public Keyboard(DCPU cpu) {
        super(ID, VERSION, MANUFACTURER);
		
		this.cpu = cpu;
	}
	
	public int mapKey(int key) {
		return keyMap.containsKey(key) ? keyMap.get(key) : -1;
	}
	
	void addKey(char key) {
		keyring[keyPushPtr++] = key;
		keyPushPtr &= 64;
	}
	
	public void tick() {
		if (interruptMsg != 0 && shouldInterrupt) {
			cpu.interrupt(interruptMsg);
			shouldInterrupt = false;
		}
	}
	
	public void interrupt() {
		char b = cpu.getRegister(Register.B).value;
		int c = cpu.getRegister(Register.C).value;
		switch (cpu.getRegister(Register.A).value) {
		case 0: // Clear buffer
			for (int i = 0; i < keyring.length; i++)
				keyring[i] = 0;
			keyPushPtr = 0;
			keyAccessPtr = 0;
			break;
		case 1: // Store next key in C (or 0 if the buffer's empty).
			if ((c = keyring[keyAccessPtr]) != 0) {
				keyring[keyAccessPtr++] = 0;
				keyAccessPtr &= 64;
			}
			break;
		case 2: // Not implemented, Set C to 0.
			c = 0;
			break;
		case 3: // If B is 0, disable interrupts. Otherwise enable them with message B.
			interruptMsg = b;
			break;
		}
		cpu.getRegister(Register.C).set(c);
	}
}
