package PoeInvSort;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.WORD;

import static com.sun.jna.platform.win32.User32.INSTANCE;

public class MKControl {
	private static final double uiWidth = 12.670; // 840/66.3 pixels
	private static double slotSize; // 66.3 pixels/1360 height
	private static int[] windowDims; // [0]=left, [1]=top, [2]=right, [3]=bottom
	
	// JNA inputs for mouse/keyboard functions
	private static INPUT[] mousePress;
	private static INPUT[] mouseMove;
	private static INPUT[] keyPress;
	
	private static final long MOUSEEVENTF_MOVE = 0x0001L;
	private static final long MOUSEEVENTF_ABSOLUTE = 0x8000L;
	private static final long MOUSEEVENTF_LEFTDOWN = 0x0002L;
	private static final long MOUSEEVENTF_LEFTUP = 0x0004L;
	
	static boolean slowerExc = false;
	static Integer vendorSellOffset = null;
	
	
	public static void init() throws Throwable {
		windowDims = GetWindowRect.getPoeWindowDims();
		slotSize = 0.04875 * (windowDims[3] - windowDims[1]);
	}
	
	private static void jnaMouseMove(LONG x, LONG y) {
		// init
		if (mouseMove == null) {
			// mouse movement code gotten from https://stackoverflow.com/questions/52174294/how-to-drag-mouse-with-jna-platform-win32
			INPUT mm = new INPUT();
			mm.type = new DWORD(INPUT.INPUT_MOUSE);
			mm.input.setType("mi");
			mm.input.mi.mouseData = new DWORD(0);
			mm.input.mi.dwFlags = new DWORD(MOUSEEVENTF_MOVE | MOUSEEVENTF_ABSOLUTE);
			mm.input.mi.time = new DWORD(0);
			mouseMove = new INPUT[] {mm};
		}

		mouseMove[0].input.mi.dx = x;
		mouseMove[0].input.mi.dy = y;
		@SuppressWarnings("unused")
		DWORD result = INSTANCE.SendInput(new DWORD(1), mouseMove, mouseMove[0].size()); // inputs are # inputs, INPUT[] array, struct size
	}
	
	private static void click() {
		// init
		if (mousePress == null) {
			INPUT mp = new INPUT();
			mp.type = new DWORD(INPUT.INPUT_MOUSE);
			mp.input.setType("mi");
			mp.input.mi.mouseData = new DWORD(0);
			mp.input.mi.time = new DWORD(0);
			mousePress = new INPUT[] {mp};
		}

		mousePress[0].input.mi.dwFlags = new DWORD(MOUSEEVENTF_LEFTDOWN); // press
		@SuppressWarnings("unused")
		DWORD result = INSTANCE.SendInput(new DWORD(1), mousePress, mousePress[0].size());
		mousePress[0].input.mi.dwFlags = new DWORD(MOUSEEVENTF_LEFTUP); // release
		result = INSTANCE.SendInput(new DWORD(1), mousePress, mousePress[0].size());
	}
	
	private static void jnaKeyPress(int key) {
		if (keyPress == null) {
			INPUT input = new INPUT();
			input.type = new DWORD(INPUT.INPUT_KEYBOARD);
			input.input.setType("ki");
			input.input.ki.time = new DWORD(0);
			input.input.ki.wScan = new WORD(0);
			input.input.ki.dwExtraInfo = new ULONG_PTR(0);
			keyPress = new INPUT[] {input};
		}

		keyPress[0].input.ki.dwFlags = new DWORD(0);  // key press
		keyPress[0].input.ki.wVk = new WORD(key);
		INSTANCE.SendInput(new DWORD(1), keyPress, keyPress[0].size());
	}
	
	private static void jnaKeyRelease(int key) {
		if (keyPress == null) {
			INPUT input = new INPUT();
			input.type = new DWORD(INPUT.INPUT_KEYBOARD);
			input.input.setType("ki");
			input.input.ki.time = new DWORD(0);
			input.input.ki.wScan = new WORD(0);
			input.input.ki.dwExtraInfo = new ULONG_PTR(0);
			keyPress = new INPUT[] {input};
		}

		keyPress[0].input.ki.dwFlags = new DWORD(2);  // key release
		keyPress[0].input.ki.wVk = new WORD(key);
		INSTANCE.SendInput(new DWORD(1), keyPress, keyPress[0].size());
	}
	
	public static void wait(int time) throws InterruptedException {
		int rand = (int) (Math.random() * time/4) + time;
		if (slowerExc) rand += 100;
		Thread.sleep(rand);
	}
	
	public static void waitCnst(int time) throws InterruptedException {
		if (slowerExc) Thread.sleep(time + 100);
		else Thread.sleep(time);
	}

	/* Used for UI elements on left half of screen e.g. stash.
	 * Inputs are in scale of inventory spaces. */
	private static void mouseMoveFromLeft(double X, double Y) throws Throwable {
		LONG x = new LONG((int) ((windowDims[0] + X * slotSize) * 65536 / (windowDims[2] - windowDims[0])));
		LONG y = new LONG((int) ((windowDims[1] + Y * slotSize) * 65536 / (windowDims[3] - windowDims[1])));
		jnaMouseMove(x, y);
		//waitCnst(25);
	}
	
	/* Used for UI elements on right half of screen e.g. inventory.
	 * Inputs are in scale of inventory spaces. */
	private static void mouseMoveFromRight(double X, double Y) throws Throwable {
		LONG x = new LONG((int) ((windowDims[2] - X * slotSize) * 65536 / (windowDims[2] - windowDims[0])));
		LONG y = new LONG((int) ((windowDims[1] + Y * slotSize) * 65536 / (windowDims[3] - windowDims[1])));
		jnaMouseMove(x, y);
		//waitCnst(25);
	}
	
	/* Used for character movement. 
	 * Measured from middle of width of screen and top of window. */
	private static void mouseMoveFromMiddle(double X, double Y) throws Throwable {
		LONG x = new LONG((int) (((windowDims[0] + windowDims[2])/2 + X * slotSize) * 65536 / (windowDims[2] - windowDims[0])));
		LONG y = new LONG((int) ((windowDims[1] + Y * slotSize) * 65536 / (windowDims[3] - windowDims[1])));
		jnaMouseMove(x, y);
		//waitCnst(25);
	}
	
	// 1732, 528	1510
	public static void moveTo(String loc) throws Throwable {
		jnaKeyPress(KeyEvent.VK_SPACE);
		jnaKeyRelease(KeyEvent.VK_SPACE);
		wait(50);
		if (loc.equals("stash")) mouseMoveFromMiddle(0.181, 8.30);
		else if (loc.equals("vendor")) mouseMoveFromMiddle(-3.167, 7.964);
		else return;
		click();
		wait(1000);
	}
	
	public static void openTab(int tab) throws Throwable {
		mouseMoveFromLeft(12.157, 2.715);
		click();
		wait(30);
		mouseMoveFromLeft(12.972, 2.715 + 0.4247 * tab);
		click();
		wait(110);
	}
	
	public static void ctrlClickAt(LinkedList<Item> items) throws Throwable {
		jnaKeyPress(KeyEvent.VK_CONTROL);
		for (Item item : items) {
			int loc = item.occupying.get(0);
			mouseMoveFromRight(11.840 - (loc/5), 11.674 + (loc%5));
			click();
			wait(80);
		}
		jnaKeyRelease(KeyEvent.VK_CONTROL);
	}	
	
	public static String copyItemInfo(int loc) throws Throwable {
		Toolkit.getDefaultToolkit().getSystemClipboard().
				setContents(new StringSelection(""), null);
		mouseMoveFromRight(11.840 - (loc/5), 11.674 + (loc%5));
		jnaKeyPress(KeyEvent.VK_CONTROL);
		jnaKeyPress(KeyEvent.VK_C);
		jnaKeyRelease(KeyEvent.VK_C);
		jnaKeyRelease(KeyEvent.VK_CONTROL);
		wait(80);
		return (String) Toolkit.getDefaultToolkit().
				getSystemClipboard().getData(DataFlavor.stringFlavor);
	}

	public static void sellItems(LinkedList<Item> sellable) throws Throwable {
		if (!sellable.isEmpty() && vendorSellOffset != null) {
			moveTo("vendor"); // one line = 28.25 pixels
			mouseMoveFromMiddle(0.0, 4.0 + 0.426*vendorSellOffset); // DESKTOP Y COORD IS 9.11
			click();
			wait(300);
			ctrlClickAt(sellable);
			int x = windowDims[2] - (int)(uiWidth * slotSize);
			x = x/2 - (int)(4.5 * slotSize);
			LONG xx = new LONG(x * 65536 / (windowDims[2] - windowDims[0]));
			LONG yy = new LONG((int)(16.048 * slotSize) * 65536 / (windowDims[3] - windowDims[1]));
			jnaMouseMove(xx, yy);
			waitCnst(25);
			click();
			wait(500);
			moveTo("stash");
		}
	}
	
	public static void copypastetest() {
		jnaKeyPress(KeyEvent.VK_CONTROL);
		jnaKeyPress(KeyEvent.VK_A);
		jnaKeyRelease(KeyEvent.VK_A);
		jnaKeyPress(KeyEvent.VK_C);
		jnaKeyRelease(KeyEvent.VK_C);
		for (int i = 0; i < 10; i++) {
			jnaKeyPress(KeyEvent.VK_V);
			jnaKeyRelease(KeyEvent.VK_V);
		}
		jnaKeyRelease(KeyEvent.VK_CONTROL);
	}
}
