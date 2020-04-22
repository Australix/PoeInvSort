package PoeInvSort;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
	static Inventory inventory;
	static ArrayList<SortingRule> sortRules = new ArrayList<SortingRule>();
	
	public static void main(String[] args) {
		try {
			//Thread.sleep(6000);
			initialize();
			inventory.scanInventory();
			inventory.transferItemsToStash();
			MKControl.openTab(0);

	    } catch (Throwable ex) {
	        System.err.println("Uncaught exception - " + ex.getMessage());
	        ex.printStackTrace(System.err);
	    }		
	}
	
	private static void initialize() throws Throwable {
		inventory = new Inventory();
		MKControl.init();
		
		// load settings from Settings file
		String settings = fileToString("Settings.txt");
		String[] lines = settings.split("\\r?\\n");
		if (lines[4].equals("TRUE")) MKControl.slowerExc = true;
		
		// parse ignored inventory slots
		String[] ignoreSlots = lines[7].split(",");
		LinkedList<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < ignoreSlots.length; i++) {
			list.add(Integer.parseInt(ignoreSlots[i]));
		}
		inventory.setIgnore(list);
		
		// item sorting rules
		for (int i = 10; i < lines.length; i++) {
			if (!lines[i].equals("")) sortRules.add(new SortingRule(lines[i]));
		}
	}

    public static String fileToString(String resource) {
        // path within jar file
        InputStream input = Main.class.getResourceAsStream("/resources/" + resource);
        if (input == null) {
            // path within eclipse
            input = Main.class.getClassLoader().
            		getResourceAsStream("PoeInvSort/resources/" + resource);
        }
        @SuppressWarnings("resource")
		Scanner s = new Scanner(input).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
