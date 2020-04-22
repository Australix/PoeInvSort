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

	public String getData() {
		return data;
	}
}
