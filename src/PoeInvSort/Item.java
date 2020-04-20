package PoeInvSort;

public class Item {
	private String data;
	private String sockets;
	public String rarity;
	public String name;
	
	public int type = -1; // -1 = ignore by sorter
	
	int location;
	Size size = Size.Unsized;
	
	enum Size {
		Unsized, _1x1, _1x2, _1x3, _1x4, _2x1, _2x2, _2x3, _2x4;
	}

	// constructor
	public Item(int loc, String copiedData) {
		data = copiedData;
		location = loc;
		initData();
	}
	
	private void initData() {
		String[] data = this.data.split("\\r?\\n");
		rarity = data[0].substring(8);
		name = data[1];
		if (!data[2].contains("---")) name = name + " " + data[2];
		
		sockets = "";
		for (int i = 5; i < data.length; i++) {
			if (data[i].startsWith("Sockets")) {
				sockets = data[i].substring(9);
			}
		}
	}
	
	public String getSockets() {
		return sockets;
	}

	public void sizeItem() {
		if (size == Size.Unsized) {
			if        ((location%5 < 2) && locCompare(location+8)) {
				size = Size._2x4;
			} else if ((location%5 < 2) && locCompare(location+3)) {
				size = Size._1x4;
			} else if ((location%5 < 3) && locCompare(location+7)) {
				size = Size._2x3;
			} else if ((location%5 < 3) && locCompare(location+2)) {
				size = Size._1x3;
			} else if ((location%5 < 4) && locCompare(location+6)) {
				size = Size._2x2;
			} else if ((location%5 < 4) && locCompare(location+1)) {
				size = Size._1x2;
			} else if (                    locCompare(location+5)) {
				size = Size._2x1;
			} else {
				size = Size._1x1;
			}
		}
	}
	
	private boolean locCompare(int newLoc) {
		if (newLoc > 59) return false;
		String otherData = Main.inventory.readItemData(newLoc);
		if (otherData == null) otherData = MKControl.copyItemInfo(newLoc);
		if (otherData.equals(data)) {
			return true;
		} else {
			Main.inventory.addToItemData(newLoc, otherData);
			return false;
		}
	}

	public String getData() {
		return data;
	}
}
