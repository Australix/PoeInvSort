*** Installation Instructions ***

1. Navigate to your Lutbot script location. This is usually .../Documents/AutoHotKey/LutTools/
2. Put PoeInvSort.jar in the same folder
3. Open the script using a text editor
4. Edit a partyCommand to execute the jar file instead. Example: 
partyCommand3:
	Run %A_ScriptDir%\PoeInvSort.jar
	return
5. Run Lutbot. Open the settings. 
6. Change the key combination for the partyCommand you changed to whatever you want.

Done!

*** Janky workarounds ***
 - The program can only detect your default display's scaling factor for accurate mouse movement.
Make sure the display you play PoE on is your default (in display options), or if you use
multiple monitors make sure one of them is defualt and they have the same scaling factor. 
 - Not tested across multiple monitors.

*** Settings Description ***
 - Slower execution can help if you are experiencing bugs or are on a weaker computer. 
 - Tabs are indexed starting from 0. 
 - One tab can be used for multiple categories. 
 - Premium tabs for e.g. currency are not required. 
 - Inventory slots are indexed starting from 0, top-to-bottom first, then left-to-right.
   Imagine ctrl+clicking currency from your stash to your inventory. 
 - Ignored slots will not be sorted. Put your portals/wisdoms here. 

TO DO
customizable sorts like pack size, reflect
	dictionary(map) of tab numbers and how to sort for them
sell items to vendor