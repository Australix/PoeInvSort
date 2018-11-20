package PoeInvSort;

import java.util.Arrays;
import java.util.List;

public class SortingRule {
	int tab;
	String rarity;
	String name;
	String description;
	
	public SortingRule(String input) {
		String[] data = input.split(":");
		tab = Integer.parseInt(data[0]);
		rarity = data[1];
		name = data[2];
		description = data[3];
	}
	
	public boolean isMatch(Item item) {
		// compare rarity or #fragment or #essence
		if (!rarity.equals("*")) {
			if (rarity.equals("#fragment")) {
				if (containsAny(item.name, Arrays.asList("Offering to the ", 
					"Sacrifice at ", "Fragment of the ", "Blessing of ", "Splinter of ", 
					"Mortal ", "'s Breachstone", "'s Key", "Divine Vessel"))) {
					return true;
				} else return false;
			}
			if (rarity.equals("#essence")) {
				if (item.name.contains("Essence") || item.name.startsWith("Remnant of")) {
					return true;
				} else return false;
			}
			if (!item.rarity.contains(rarity)) return false;
		}
		// compare name
		if (!name.equals("*") && !item.name.contains(name)) return false;
		// compare description or #chromatic or #6socket
		if (!description.equals("*")) {
			if (description.equals("#chromatic")) {
				if (!containsAny(item.getSockets(), Arrays.asList("R-G-B", "G-R-B", 
					"B-G-R", "R-B-G", "G-B-R", "B-R-G"))) {
					return false;
				}
			}
			else if (description.equals("#6socket")) {
				if (!(item.getSockets().length() == 12)) {
					return false; 
				}
			}
			else if (!item.getData().contains(description)) return false;
		}
		return true;
	}
	
	public int guessSize(Item item) {
		if (rarity.equals("Currency")) return 1;
		if (rarity.equals("#fragment") && !item.name.equals("Divine Vessel")) return 1;
		if (rarity.contains("Divination")) return 1;
		if (rarity.equals("#essence")) return 1;
		if (name.contains("Map")) return 1;
		return 0;
	}
	
	private static boolean containsAny(String input, List<String> keywords) {
		for (String s : keywords) {
			if (input.contains(s)) return true;
		}
		return false;
	}
}
