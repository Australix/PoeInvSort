package PoeInvSort;

import java.awt.Toolkit;
import java.awt.Window;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public class GetWindowRect {
   public static HWND hwnd = User32.INSTANCE.GetForegroundWindow();

   public interface User32 extends StdCallLibrary {
       User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
               W32APIOptions.DEFAULT_OPTIONS);
       int GetWindowRect(HWND handle, int[] rect);
       HWND GetForegroundWindow();
       int GetWindowTextA(PointerType hWnd, byte[] lpString, int nMaxCount);
       HDC GetDC(HWND hwnd);
       HWND FindWindow(String winClass, String title); 
   }
   
	public static void getScaling() {
        float toolkit = 0;
        float jna = 0;
        WinDef.HDC hdc = GDI32.INSTANCE.CreateCompatibleDC(User32.INSTANCE.GetDC(hwnd));
        if (hdc != null) {
            float actual = GDI32.INSTANCE.GetDeviceCaps(hdc, 10 /* VERTRES */);
            float logical = GDI32.INSTANCE.GetDeviceCaps(hdc, 117 /* DESKTOPVERTRES */);
            GDI32.INSTANCE.DeleteDC(hdc);

            System.out.println(actual + ", " + logical);
            if (logical != 0 && logical/actual >= 1) {
                jna = logical/actual;
            }
        }
        toolkit = (Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f); // TODO this stuff doesn't work in test, might delete later
        System.out.println("JNA found: " + jna);
        System.out.println("Toolkit found: " + toolkit);
   }
   
   public static int[] getPoeWindowDims() throws NotActiveWindowException {
	   String poe = "Path of Exile";
	   if (!getWindowTitle(hwnd).equals(poe)) throw new NotActiveWindowException(poe);
	   int[] rect = {0, 0, 0, 0};
	   User32.INSTANCE.GetWindowRect(hwnd, rect);
       // offsets for title bar and 9-pixel border
       rect[0] = rect[0] + 9;
       rect[1] = rect[1] + 39;
       rect[2] = rect[2] - 9;
       rect[3] = rect[3] - 9;
	   
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
}