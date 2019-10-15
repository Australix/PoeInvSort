# PoeInvSort
Java executable that performs mouse and keyboard movements to sort the items you are carrying into appropriate tabs in your stash for the popular ARPG Path of Exile. 

## The Reasoning
Every time you are finished with a zone or two, you are at your stash with a full inventory, and with very little deviation, you put the same items you find into the same locations. Why are you doing all these boring, repetitive actions that require no thought? One good reason would be that it is technically against Terms of Service to use this program, but if you've found this you probably don't care all that much about that. 

## The Mechanics
PoeInvSort works by parsing a Settings.txt file in the .jar archive for some basic settings as well as a set of filters that does not work all that differently from item filters you use in-game (it is admittedly much less robust). It then takes advantage of the ability to copy item data by hovering over items and using Ctrl+C to apply the filters defined in the settings file and determine what tab in your stash you want the items to go to. 

## Installation
PoeInvSort is a jar file that executes once, then closes. To use the program without adding a delay, you would want to execute this file using some sort of macro. I use Lutbot so these instructions will mostly pertain to hacking it into there, but it shouldn't be difficult to add to a different Autohotkey macro. 

1. Navigate to your Lutbot script location. This is usually .../Documents/AutoHotKey/LutTools/heavy.ahk. I have not tried adding this to the lite verison. 
2. Put PoeInvSort.jar in the same folder.
3. Open the script using a text editor.
4. Edit a partyCommand to execute the jar file instead. Example: 
partyCommand1:
	Run %A_ScriptDir%\PoeInvSort.jar
	return
5. Run Lutbot. Open the settings. 
6. Change the key combination for the partyCommand you changed to whatever you want.

Done!

## Editing the Settings
PoeInvSort has a settings file inside the jar file. The jar can be opened like a zip file with winRAR or a similar program to reveal a standard directory structure. The settings are in PoeInvSort/resources/Settings.txt.

### Settings Descriptions
If Slower Execution is activated, all movements and controls are performed 3x slower. Movements are normally performed very quickly, so this can help if it seems like the program is missing some items or otherwise not working correctly.

Inventory slots can be deliberately ignored by adding them below the next option. This is useful for keeping items such as scrolls, flask swaps, and gem swaps in your inventory. Inventory slots are indexed 0-59, starting from the top left at 0, moving vertically down to 4, and then starting the next column at 5, etc. 

Auto-vendoring items is out of date. It may be re-done later, but is for the most part unreliable due to the "blind" nature of this program. 

The "lines of text" setting is related to the auto-vendoring feature.

#### Item filters
Each filter is defined as 4 parameters separated by three colons (:). For each of these parameters except the first, a * can be placed to ignore that specific parameter. Item filters listed earlier have priority over the ones listed later. 

The first parameter should be able to be parsed as an integer, which corresponds to the tab you want this category of item to be placed in. Tabs are zero-indexed, meaning the first tab of your stash is tab 0, the second is 1, etc. If you'd like this category of item to not be sorted, use a negative number as the desired stash tab. 

The second parameter sorts items by rarity. Each item has a rarity, shown in the first line of the item description. In addition to Normal, Magic, Rare, and Unique, the game defines Currency and Divination Card as rarities. I have also created custom tags that can be used here to sort for special stash tabs (although the special stash tabs are not required for the program to function):
1. #fragment includes all items that can be placed in the fragment tab.
2. #fossil and #resonator includes all fossils and resonators, which have dedicated slots in the delve tab.
3. #essence includes all items that have dedicated slots in the essence tab (including Remnant of Corruption).
4. #oil includes all blight oils, although these also count as currency items and can be sorted using the Currency rarity tag. 

The third parameter sorts items by item name. This includes anything above the first row of dashes in the item description, not including the rarity. As an example, "Map" can be used here to put all maps in your map tab. 

Finally, the fourth parameter sorts items by looking for the given string in the rest of the item description. This part is very undeveloped compared to the in-game item filters, as it is a simple string search. There are also some custom tags for this parameter:
1. #6socket includes all items that have 6 sockets.
2. #chromatic includes all items that have red, green, and blue sockets linked together excluding 6-links. 

The Settings.txt file has the configuration of these rules that I use. 
