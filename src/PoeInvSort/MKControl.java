package PoeInvSort;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class MKControl {
	private static Robot robot;
	private static final double uiWidth = 12.670; // 840/66.3 pixels
	private static double slotSize; // 66.3 pixels/1360 height
	private static int[] windowDims; // [0]=left, [1]=top, [2]=right, [3]=bottom
	
	static boolean slowerExc = false;
	static Integer vendorSellOffset = null;
	
	
	public static void init() throws Throwable {
		robot = new Robot();
		windowDims = GetWindowRect.getPoeWindowDims();
		slotSize = 0.04875 * (windowDims[3] - windowDims[1]);
	}
	
	private static void click() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
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
		robot.mouseMove((int) (windowDims[0] + X * slotSize), 
						(int) (windowDims[1] + Y * slotSize));
		waitCnst(25);
	}
	
	/* Used for UI elements on right half of screen e.g. inventory.
	 * Inputs are in scale of inventory spaces. */
	private static void mouseMoveFromRight(double X, double Y) throws Throwable {
		robot.mouseMove((int) (windowDims[2] - X * slotSize), 
						(int) (windowDims[1] + Y * slotSize));
		waitCnst(25);
	}
	
	/* Used for character movement. 
	 * Measured from middle of width of screen and top of window. */
	private static void mouseMoveFromMiddle(double X, double Y) throws Throwable {
		robot.mouseMove((int) ((windowDims[0] + windowDims[2])/2 + X * slotSize), 
						(int) (windowDims[1] + Y * slotSize));
		waitCnst(25);
	}
	
	// 1732, 528	1510
	public static void moveTo(String loc) throws Throwable {
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_SPACE);
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
		robot.keyPress(KeyEvent.VK_CONTROL);
		for (Item item : items) {
			int loc = item.occupying.get(0);
			mouseMoveFromRight(11.840 - (loc/5), 11.674 + (loc%5));
			click();
			wait(80);
		}
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}	
	
	public static String copyItemInfo(int loc) throws Throwable {
		Toolkit.getDefaultToolkit().getSystemClipboard().
				setContents(new StringSelection(""), null);
		mouseMoveFromRight(11.840 - (loc/5), 11.674 + (loc%5));
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
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
			robot.mouseMove(x, (int)(16.048 * slotSize));
			waitCnst(25);
			click();
			wait(500);
			moveTo("stash");
		}
	}
}
