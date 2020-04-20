package PoeInvSort;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.WORD;

import static com.sun.jna.platform.win32.User32.INSTANCE;

public class MKControl {
	private static double slotSize; // 66.3 pixels/1360 height
	private static double scaleFactor; // windows scaling factor
	private static int[] windowDims; // [0]=left, [1]=top, [2]=right, [3]=bottom
	// Pixels on screen have to be multiplied by these values for mouse movement
	private static double xMouseScale;
	private static double yMouseScale;
	
	// JNA inputs for mouse/keyboard functions
	private static INPUT[] mousePress;
	private static INPUT[] mouseMove;
	private static INPUT[] keyPress;
	// Constants for JNA mouse control
	private static final long MOUSEEVENTF_MOVE = 0x0001L;
	private static final long MOUSEEVENTF_ABSOLUTE = 0x8000L;
	private static final long MOUSEEVENTF_LEFTDOWN = 0x0002L;
	private static final long MOUSEEVENTF_LEFTUP = 0x0004L;
	// Constants for window scaling
	private static final int VERTRES = 10;
	private static final int DESKTOPVERTRES = 117;
	
	static boolean slowerExc = false;
	static Integer vendorSellOffset = null;
	
	public static HWND hwnd = User32.INSTANCE.GetForegroundWindow();

	/* Interface for something, I don't really know */
	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
				W32APIOptions.DEFAULT_OPTIONS);
		int GetWindowRect(HWND handle, int[] rect);
		HWND GetForegroundWindow();
		int GetWindowTextA(PointerType hWnd, byte[] lpString, int nMaxCount);
		HDC GetDC(HWND hwnd);
		HWND FindWindow(String winClass, String title); 
		int GetSystemMetrics(int i);
	}

	private static double getScaleFactor() {
		WinDef.HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(null);
		if (hdc != null) {
			double actual  = GDI32.INSTANCE.GetDeviceCaps(hdc, VERTRES);
			double logical = GDI32.INSTANCE.GetDeviceCaps(hdc, DESKTOPVERTRES);
			GDI32.INSTANCE.DeleteDC(hdc);

			if (logical != 0 && logical/actual >= 1) {
				return logical/actual;
			}
		}
		System.out.println("scaling failed");
        return 1.25;
	}

	public static int[] getPoeWindowDims() throws NotActiveWindowException {
		String poe = "Path of Exile";
		if (!getWindowTitle(hwnd).equals(poe)) throw new NotActiveWindowException(poe);
		int[] rect = {0, 0, 0, 0};
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		// offsets for title bar and extra border
		if (scaleFactor == 1.0) {
			rect[0] = rect[0] + 8;
			rect[1] = rect[1] + 32;
			rect[2] = rect[2] - 8;
			rect[3] = rect[3] - 8;
		} else {
			rect[0] = rect[0] + 7;
			rect[1] = rect[1] + 7 + (int)(24*scaleFactor);
			rect[2] = (int)((rect[2] - 7) * scaleFactor);
			rect[3] = (int)((rect[3] - 7) * scaleFactor);
		}
		return rect;
	}

	public static String getWindowTitle(HWND hwnd) {
		if (hwnd == null) return "";
		byte[] title = new byte[512];
		User32.INSTANCE.GetWindowTextA(hwnd, title, 512);
		return Native.toString(title);
	}   
	   
	@SuppressWarnings("serial")
	public static class NotActiveWindowException extends Exception {
		public NotActiveWindowException(String name) {
			super(name + " window not active/open!");
		}
	}
	
	// 2656 803
	public static void init() throws Throwable {
		scaleFactor = getScaleFactor();
		windowDims = getPoeWindowDims();
		slotSize = 0.04875 * (windowDims[3] - windowDims[1]);
		int xMonitor = User32.INSTANCE.GetSystemMetrics(0x0); // SM_CXSCREEN
		int yMonitor = User32.INSTANCE.GetSystemMetrics(0x1); // SM_CYSCREEN
		xMouseScale = 65536.0 / xMonitor / scaleFactor;
		yMouseScale = 65536.0 / yMonitor / scaleFactor;
	}

	private static void jnaMouseMove(LONG x, LONG y) {
		// init
		if (mouseMove == null) {
			// mouse movement code gotten from 
			// https://stackoverflow.com/questions/52174294/how-to-drag-mouse-with-jna-platform-win32
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
		DWORD result = INSTANCE.SendInput(new DWORD(1), mouseMove, mouseMove[0].size()); 
		// inputs are # inputs, INPUT[] array, struct size
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
		// init
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
		// init
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
		if (slowerExc) Thread.sleep(rand*3);
		else Thread.sleep(rand);
	}
	
	public static void waitCnst(int time) throws InterruptedException {
		if (slowerExc) Thread.sleep(time*3);
		else Thread.sleep(time);
	}

	/* Used for UI elements on left half of screen e.g. stash.
	 * Inputs are in scale of inventory spaces. */
	private static void mouseMoveFromLeft(double X, double Y) throws Throwable {
		LONG x = new LONG((int) ((windowDims[0] + X * slotSize) * xMouseScale));
		LONG y = new LONG((int) ((windowDims[1] + Y * slotSize) * yMouseScale));
		jnaMouseMove(x, y);
		waitCnst(15);
	}
	
	/* Used for UI elements on right half of screen e.g. inventory.
	 * Inputs are in scale of inventory spaces. */
	private static void mouseMoveFromRight(double X, double Y) throws Throwable {
		LONG x = new LONG((int) ((windowDims[2] - X * slotSize) * xMouseScale));
		LONG y = new LONG((int) ((windowDims[1] + Y * slotSize) * yMouseScale));
		jnaMouseMove(x, y);
		waitCnst(15);
	}
	
	/* Used for character movement. 
	 * Measured from middle of width of screen and top of window. */
	public static void mouseMoveFromMiddle(double X, double Y) throws Throwable {
		LONG x = new LONG((int) (((windowDims[0] + windowDims[2])/2 + X * slotSize) * xMouseScale));
		LONG y = new LONG((int) ((windowDims[1] + Y * slotSize) * yMouseScale));
		jnaMouseMove(x, y);
		waitCnst(15);
	}
	
	public static void openTab(int tab) throws Throwable {
		mouseMoveFromLeft(12.157, 2.715);
		click();
		wait(100);
		mouseMoveFromLeft(12.972, 2.715 + 0.4247 * tab);
		click();
		wait(200);
	}
	
	public static void ctrlClickAt(LinkedList<Item> items) throws Throwable {
		jnaKeyPress(KeyEvent.VK_CONTROL);
		for (Item item : items) {
			int loc = item.location;
			mouseMoveFromRight(11.840 - (loc/5), 11.674 + (loc%5));
			click();
			wait(15);
		}
		jnaKeyRelease(KeyEvent.VK_CONTROL);
	}	
	
	public static String copyItemInfo(int loc) throws Throwable {
		String ret = null;
		for (int attempts = 0; ret == null && attempts < 5; attempts++) {
			try {
				Toolkit.getDefaultToolkit().getSystemClipboard().
						setContents(new StringSelection(""), null);
				mouseMoveFromRight(11.840 - (loc/5), 11.674 + (loc%5));
				jnaKeyPress(KeyEvent.VK_CONTROL);
				jnaKeyPress(KeyEvent.VK_C);
				jnaKeyRelease(KeyEvent.VK_C);
				jnaKeyRelease(KeyEvent.VK_CONTROL);
				wait(25);
				ret = (String) Toolkit.getDefaultToolkit().
						getSystemClipboard().getData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				wait(25);
			}
		}
		return ret;
	}
}
