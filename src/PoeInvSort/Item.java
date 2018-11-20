package PoeInvSort;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Item {
	private String data;
	private String sockets;
	private int type = -3; //"null"
	private boolean sized = false;
	List<Integer> occupying = new LinkedList<Integer>();
	
	String rarity;
	String name;
	
	// constructor
	public Item(int loc) throws Throwable {
		data = MKControl.copyItemInfo(loc);
		occupying.add(loc);
		initData();
		// updates size/type of item
		getType();
		if (type != -1) {
			if (!sized) sizeItem();
			Main.inventory.addItem(this);
		}
	}
	// constructor used if data has already been copied
	public Item(int loc, String copiedData) throws Throwable {
		data = copiedData;
		occupying.add(loc);
		initData();
		// updates size/type of item
		getType();
		if (type != -1) {
			if (!sized) sizeItem();
			Main.inventory.addItem(this);
		}
	}
	
	private void initData() {
		if (!data.equals("")) {
			String[] data = this.data.split("\\r?\\n");
			rarity = data[0].substring(8);
			name = data[1];
			if (!data[2].contains("---")) name = name + " " + data[2];
			getSockets();
		}
	}
	
	public String getSockets() {
		if(sockets == null) {
			String[] data2 = data.split("\\r?\\n");
			sockets = "";
			for (int i = 5; i < data2.length; i++) {
				if (data2[i].startsWith("Sockets")) {
					sockets = data2[i].substring(9);
				}
			}
		}
		return sockets;
	}

	public void sizeItem() throws Throwable {
		int loc = occupying.get(0);
		if (locCompare(loc+8)) { // 2x4
			occupying.addAll(Arrays.asList(loc+1, loc+2, loc+3, loc+5, loc+6, loc+7, loc+8));
		} else if (locCompare(loc+7)) { // 2x3
			occupying.addAll(Arrays.asList(loc+1, loc+2, loc+5, loc+6, loc+7));
		} else if (locCompare(loc+2)) { // 1x3
			occupying.addAll(Arrays.asList(loc+1, loc+2));
		} else if (locCompare(loc+6)) { // 2x2
			occupying.addAll(Arrays.asList(loc+1, loc+5, loc+6));
		} else if (locCompare(loc+5)) { // 2x1
			occupying.add(loc+5);
		} else if (locCompare(loc+1)) { // 1x2
			occupying.add(loc+1);
		} // 1x1 does not trigger any of them
		sized = true;
	}
	
	private boolean locCompare(int newLoc) throws Throwable {
		String otherData = Main.inventory.readItemData(newLoc);
		if (otherData == null) otherData = MKControl.copyItemInfo(newLoc);
		if (otherData.equals(data)) {
			return true;
		} else {
			Main.inventory.addToItemData(newLoc, otherData);
			return false;
		}
	}
	
	public int getType() {
		if (type != -3) return type;
		if (data.equals("")) {
			type = -1;
			sized = true;
			return type;
		}
		for (SortingRule s : Main.sortRules) {
			if (s.isMatch(this)) {
				if (s.guessSize(this) != 0) {
					sized = true;
				}
				type = s.tab;
				return type;
			}
		}
		return -1; // anything not accounted for in filter
	}
	
	public String getData() {
		return data;
	}
}
