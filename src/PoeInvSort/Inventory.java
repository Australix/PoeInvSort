package PoeInvSort;

import java.util.HashMap;
import java.util.LinkedList;

import PoeInvSort.Item.Size;

public class Inventory {
	private boolean[] itemPresentAt = new boolean[60];
	private HashMap<Integer, LinkedList<Item>> tabs = 
			new HashMap<Integer, LinkedList<Item>>();
	
	public void processInventory() {
		scanInventory();
		transferItemsToStash();
		MKControl.openTab(0);
	}
	
	public void scanInventory() {
		int count = 0;
		for (int i = 0; i < 60; i++) {
			if (itemPresentAt[i]) {
				count = 0;
			} else {
				String copiedData = MKControl.copyItemInfo(i);
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
							item.type = s.tab;
							break;
						}
					}
					updateItemPresence(item, true);
					if (item.type >= 0) {
						LinkedList<Item> tab = tabs.get(item.type);
						if (tab == null) {
							tab = new LinkedList<Item>();
							tabs.put(item.type, tab);
						}
						tab.add(item);
					}
				}
			}
		}
	}
	
	public void transferItemsToStash() {
		for (int tab : tabs.keySet()) {
			if (tab >= 0 && !(tabs.get(tab).isEmpty())) {
				MKControl.openTab(tab);
				MKControl.ctrlClickAt(tabs.get(tab));
			}
		}
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
	
	public void setIgnore(LinkedList<Integer> list) {
		for (int i : list) {
			itemPresentAt[i] = true;
		}
	}
}
