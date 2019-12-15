package PoeInvSort;

import java.util.HashMap;
import java.util.LinkedList;

import PoeInvSort.Item.Size;

public class Inventory {
	private boolean[] itemPresentAt = new boolean[60];
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
			if (itemPresentAt[i]) {
				itemData.remove(i);
				count = 0;
			} else {
				String copiedData = itemData.get(i);
				if (copiedData == null) {
					copiedData = MKControl.copyItemInfo(i);
				} else {
					itemData.remove(i);
				}
				// for efficiency, if 10 empty spaces in a row are found, detection terminates
				if (copiedData.equals("")) {
					count++;
					if (count == 10) return;
				} else {
					count = 0;
					
					// create new item
					Item item = new Item(i, copiedData);
					// sort and size item
					for (SortingRule s : Main.sortRules) {
						if (s.isMatch(item)) {
							item.size = s.guessSize(item);
							item.sizeItem();
							item.type = s.tab;
							break;
						}
					}
					updateItemPresence(item, true);
					if (item.type != -1) {
						tabs.get(item.type).add(item);
					}
				}
			}
		}
	}
	
	public void transferItemsToStash() throws Throwable {
		for (int tab : tabs.keySet()) {
			if (tab >= 0 && !(tabs.get(tab).isEmpty())) {
				MKControl.openTab(tab);
				MKControl.ctrlClickAt(tabs.get(tab));
			}
		}
	}
	
	public void sellItems() throws Throwable {
		LinkedList<Item> sellable = tabs.get(-2);
		MKControl.sellItems(sellable);
		for (Item item : sellable) {
			updateItemPresence(item, false);
		}
		tabs.put(-2, new LinkedList<Item>());
	}
	
	public void updateItemPresence(Item item, boolean b) {
		int loc = item.location;
		if (item.size == Size.Unsized || item.size == Size._1x1) {
			itemPresentAt[loc  ] = b;
		} else if (item.size == Size._1x2) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+1] = b;
		} else if (item.size == Size._1x3) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+1] = b;
			itemPresentAt[loc+2] = b;
		} else if (item.size == Size._1x4) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+1] = b;
			itemPresentAt[loc+2] = b;
			itemPresentAt[loc+3] = b;
		} else if (item.size == Size._2x1) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+5] = b;
		} else if (item.size == Size._2x2) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+1] = b;
			itemPresentAt[loc+5] = b;
			itemPresentAt[loc+6] = b;
		} else if (item.size == Size._2x3) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+1] = b;
			itemPresentAt[loc+2] = b;
			itemPresentAt[loc+5] = b;
			itemPresentAt[loc+6] = b;
			itemPresentAt[loc+7] = b;
		} else if (item.size == Size._2x4) {
			itemPresentAt[loc  ] = b;
			itemPresentAt[loc+1] = b;
			itemPresentAt[loc+2] = b;
			itemPresentAt[loc+3] = b;
			itemPresentAt[loc+5] = b;
			itemPresentAt[loc+6] = b;
			itemPresentAt[loc+7] = b;
			itemPresentAt[loc+8] = b;
		}
	}
	
	public void addToItemData(int i, String s) {
		itemData.put(i, s);
	}
	
	public String readItemData(int i) {
		return itemData.get(i);
	}
	
	public void setIgnore(LinkedList<Integer> list) {
		for (int i : list) {
			itemPresentAt[i] = true;
		}
	}
}
