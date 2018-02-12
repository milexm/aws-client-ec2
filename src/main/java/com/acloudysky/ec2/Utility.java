package com.acloudysky.ec2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.amazonaws.regions.Regions;


/**
* Defines utility methods and variables to support the application operations
* such as menu creation, regions list initialization and so on.
* @author Michael Miele
*
*/
public class Utility implements IUtility {
	
	/**
	 * Gets the application menu.
	 * @return List containing the menu.
	 */
	public static ArrayList<String> getMenuEntries() {
			return menuEntries;
	}
		
	
	public static void displayRegions() {
		Set<?> set = ec2Regions.entrySet();
	    Iterator<?> iterator = set.iterator();
		while(iterator.hasNext()) {
	         Map.Entry<?, ?> mentry = (Map.Entry<?,?>)iterator.next();
	         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
	         System.out.println(mentry.getValue());
		} 
	}
	
	public static Regions getRegion(String key) {
		Regions currentRegion = null;
		Set<?> set = ec2Regions.entrySet();
	    Iterator<?> iterator = set.iterator();
		while(iterator.hasNext()) {
	       Map.Entry<?, ?> mentry = (Map.Entry<?,?>)iterator.next();
	       
	       // Test
	       // System.out.println(String.format("Key: %s Value: %s", mentry.getKey(), mentry.getValue()));
	       
	       // Check if the key exists in the list.
	       int keyExist = key.trim().toLowerCase().compareTo(mentry.getKey().toString());
	       
	       if (keyExist == 0) {
	    	   // Assign the selected region. 
	    	   currentRegion = (Regions) mentry.getValue();
	    	   break;
	       }	 
		} 
		
		if (currentRegion == null)
			System.out.println(String.format("Selected region %s not allowed!", key.trim().toLowerCase()));
		return currentRegion;
	}
	
	 /**
	  * Displays the menu.
	  * @param entry The array containing the menu entries. 
	  */
	 public static void displayMenu(ArrayList<String> entry) {
		
		// Display menu header.
		System.out.println(dividerLine("*", DIVIDER_LENGTH));
		
		// Display menu entries.
	 	Iterator<String> i = entry.iterator();
	 	while (i.hasNext()) {
	 		System.out.println(i.next());
	 	}	
	 	
	 	// Display menu footer.
	 	System.out.println(dividerLine("*", DIVIDER_LENGTH));
	 }
	 
	 /**
	  * Displays welcome message.
	  * @param message The message to display.
	  */
	 public static void displayWelcomeMessage(String message)
	 {
	     System.out.println(dividerLine("*", DIVIDER_LENGTH));
	     String welcome = "Welcome to " + message; 
	     System.out.println(headerLine(welcome, DIVIDER_LENGTH));
	     System.out.println(dividerLine("*", DIVIDER_LENGTH));
	 }
	
	 /**
	  * Displays good bye message.
	  * @param message The message to display.
	  */
	 public static void displayGoodbyeMessage(String message)
	 {
		 headerLine(message, DIVIDER_LENGTH);
	     System.out.println(dividerLine("*", DIVIDER_LENGTH));
	     String bye = "Thank you for using " + message; 
	     System.out.println(headerLine(bye, DIVIDER_LENGTH));
	     System.out.println(dividerLine("*", DIVIDER_LENGTH));
	 }
	 
	 /**
	  * Gets the name of the OS and the user home directory.
	  * @return The array containing the OS name and the user home directory.
	  */
	 public static ArrayList<String> getEnvironment() {
		 return environment;
	 }
	 
	 /*************************
	  ** Internal utilities. **
	  *************************/
	 
	 /**
	  * Create the header to display.
	  * @param headerText The text to display in the header.
	  * @param length The length of the header.  
	  * @return Formatted header line.
	  */
	 private static String headerLine(String headerText, int length)
	 {
	     String header = "";
	     header = header.concat("***");
	     int blankSpaces = (length - (header.length() + headerText.length()))/2;
	     
	     for(int i = 2; i < blankSpaces; i++)
	     	header = header.concat(" ");
	     header = header.concat(headerText);
	     for(int i = header.length(); i < length - 3; i++)
	     	header = header.concat(" ");
	     header = header.concat("***");
	     return header;
	 }
	 
	 /**
	  * Create the divider line.
	  * @param c The character to use to create the divider line.
	  * @param length The length of the divider line.
	  * @return Formatted divider line.
	  */
	 private static String dividerLine(String c, int length)
	 {
	     String divider = "";
	     for(int i = 0; i < length; i++)
	         divider = divider.concat(c);
	
	     return divider;
	 }
}
