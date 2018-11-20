package PoeInvSort;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.*;

public class GetWindowRect {

   public interface User32 extends StdCallLibrary {
       User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class,
               W32APIOptions.DEFAULT_OPTIONS);
       int GetWindowRect(HWND handle, int[] rect);
       HWND GetForegroundWindow();
       int GetWindowTextA(PointerType hWnd, byte[] lpString, int nMaxCount);
   }
   
   public static int[] getPoeWindowDims() throws NotActiveWindowException {
	   HWND hwnd = User32.INSTANCE.GetForegroundWindow();
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