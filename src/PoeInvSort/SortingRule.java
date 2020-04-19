package PoeInvSort;

import PoeInvSort.Item.Size;

public class SortingRule {
	int tab;
	String rarity;
	String name;
	String description;
	
	// Constructor. Accepts one sorting rule line from settings file as input. 
	public SortingRule(String input) {
		String[] data = input.split(":");
		tab = Integer.parseInt(data[0]);
		rarity = data[1];
		name = data[2];
		description = data[3];
	}

	
	// Array of tags used to detect Fragments. 
	private static final String[] fragmentTags = {
			"Offering to the Goddess",
			"Sacrifice at ",
			"Fragment of ",
			"Splinter of ",
			"Mortal ",
			"'s Breachstone",
			"'s Key",
			"Divine Vessel"
	};

	// Function used to detect Fragments. 
	private boolean isFragment(String itemName) {
		if (containsAny(itemName, fragmentTags)) return true;
		String[] tokens = itemName.split(" ");
		if (tokens.length >= 3) {
			if (tokens[0].equals("Timeless") && tokens[tokens.length-1].equals("Splinter")) return true;
			if (tokens[0].equals("Timeless") && tokens[tokens.length-1].equals("Emblem")) return true;
			if (tokens[2].equals("Scarab")) return true;
		}
		return false;
	}
	
	// Function used to detect items that can be vendored for a Chromatic Orb.
	private boolean isChromatic(String sockets) {
		// A line in the item description says something such as:
		// Sockets: R-G-B-B B-B
		// Dashes represent links, spaces represent no link. 
		String[] tokens = sockets.split(" ");
		for (String token : tokens) { // for each set of linked sockets...
			// 6-links cannot be vendored for a chromatic
			if (token.length() == 11) return false;
			if (token.length() < 5) continue; // must be at least a 3-link
			boolean hasRed = false;
			boolean hasGreen = false;
			boolean hasBlue = false;
			for (int index = 0; index < token.length(); index += 2) {
				char c = token.charAt(index);
				if (c == 'R') hasRed = true;
				if (c == 'G') hasGreen = true;
				if (c == 'B') hasBlue = true;
			}
			if (hasRed && hasGreen && hasBlue) return true;
		}
		return false;
	}
	
	public boolean isMatch(Item item) {
		// compare rarity or #fragment or #essence
		if (!rarity.equals("*")) {
			if (rarity.equals("#fragment")) {
				if (!isFragment(item.name)) return false;
			}
			else if (rarity.equals("#essence")) {
				if (!(item.name.contains("Essence of ")
						|| item.name.startsWith("Remnant of "))) return false;
			}
			else if (rarity.equals("#oil")) {
				if (!item.rarity.equals("Currency") || !item.name.contains("Oil")) return false;
			}
			else if (rarity.equals("#fossil")) {
				if (!item.rarity.equals("Currency") || !item.name.contains("Fossil")) return false;
			}
			else if (rarity.equals("#resonator")) {
				if (!item.rarity.equals("Currency") || !item.name.contains("Resonator")) return false;
			}
			else if (!item.rarity.contains(rarity)) {
				return false;
			}
		}
		
		// compare name
		if (!name.equals("*") && !item.name.contains(name)) return false;
		
		// compare description or #chromatic or #6socket
		if (!description.equals("*")) {
			if (description.equals("#chromatic")) {
				if (!isChromatic(item.getSockets())) return false;
			}
			else if (description.equals("#6socket")) {
				if (!(item.getSockets().length() == 12)) {
					return false; 
				}
			}
			else if (!item.getData().contains(description)) {
				return false;
			}
		}
		return true;
	}
	
	public Size guessSize(Item item) {
		if (rarity.equals("Currency")) return Size._1x1;
		if (rarity.equals("#fragment")) return Size._1x1;
		if (rarity.contains("Divination")) return Size._1x1;
		if (rarity.equals("#essence")) return Size._1x1;
		if (rarity.equals("#oil")) return Size._1x1;
		if (rarity.equals("#fossil")) return Size._1x1;
		if (rarity.equals("#resonator")) {
			if (item.name.startsWith("Primitive")) return Size._1x1;
			if (item.name.startsWith("Potent")) return Size._1x2;
			if (item.name.startsWith("Powerful")) return Size._2x2;
			if (item.name.startsWith("Prime")) return Size._2x2;
		}
		if (name.contains("Map")) return Size._1x1;
		return Size.Unsized;
	}
	
	private static boolean containsAny(String input, String[] keywords) {
		for (String s : keywords) {
			if (input.contains(s)) return true;
		}
		return false;
	}
}
