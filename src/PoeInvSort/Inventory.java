package PoeInvSort;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Inventory {
	private boolean[] inventory = new boolean[60];
	private LinkedList<Integer> ignore = 
			new LinkedList<Integer>(Arrays.asList(3, 4, 9)); // default for testing
	private HashMap<Integer, LinkedList<Item>> tabs = 
			new HashMap<Integer, LinkedList<Item>>();
	private HashMap<Integer, String> itemData = new HashMap<Integer, String>();
	
	static boolean autoVendor = false;
	
	public Inventory() throws Throwable {
		tabs.put(-2, new LinkedList<Item>());
		for (SortingRule s : Main.sortRules) {
			if (tabs.get(s.tab) == null) {
				tabs.put(s.tab, new LinkedList<Item>());
			}
		}
	}
	
	public void processInventory() throws Throwable {
		scanInventory();
		if (autoVendor) {
			sellItems();
			scanInventory();
		}
		transferItemsToStash();
		MKControl.openTab(0);
	}
	
	public void scanInventory() throws Throwable {
		int count = 0;
		for (int i = 0; i < 60; i++) {
			if (ignore.contains(i) || inventory[i]) {
				itemData.remove(i);
			} else {
				Item item;
				if (itemData.containsKey(i)) {
					item = new Item(i, itemData.get(i));
					itemData.remove(i);
				} else {
					item = new Item(i);
				}
				// for efficiency, if 5 empty spaces in a row are found, detection terminates
				if (item.getType() == -1) {
					count++;
					if (count == 5) return;
				} else {
					count = 0;
				}
			}
		}
	}
	
	public void transferItemsToStash() throws Throwable {
		for (int tab : tabs.keySet()) {
			if (tab != -2 && !(tabs.get(tab).isEmpty())) {
				MKControl.openTab(tab);
				MKControl.ctrlClickAt(tabs.get(tab));
			}
		}
	}
	
	public void sellItems() throws Throwable {
		LinkedList<Item> sellable = tabs.get(-2);
		MKControl.sellItems(sellable);
		for (Item item : sellable) {
			removeItem(item);
		}
		tabs.put(-2, new LinkedList<Item>());
	}
	
	public void addItem(Item item) {
		for (Integer i : item.occupying) {
			inventory[i] = true;
		}
		tabs.get(item.getType()).add(item);
	}
	
	public void removeItem(Item item) {
		for (Integer i : item.occupying) {
			inventory[i] = false;
		}
	}
	
	public void addToItemData(int i, String s) {
		itemData.put(i, s);
	}
	
	public String readItemData(int i) {
		return itemData.get(i);
	}
	
	public void setIgnore(LinkedList<Integer> list) {
		ignore = list;
	}
}
