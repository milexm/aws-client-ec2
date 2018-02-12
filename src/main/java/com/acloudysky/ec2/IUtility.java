package com.acloudysky.ec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.amazonaws.regions.Regions;
  
/**
 * Defines fields and methods to implement the Utility class.
 * @see Utility
 * @author Michael Miele
 *
 */
public interface IUtility {
	
	// The divider length used in displaying menu and other.
	int DIVIDER_LENGTH = 66;
		
	// OS specific new line.
	String newline = System.getProperty("line.separator");
	
	
	/**
	 * Environment parameters such as OS name, user's home directory. 
	 */
	ArrayList<String> environment = new  ArrayList<String>(
			Arrays.asList(
					System.getProperty("os.name"), 		// OS name.
					System.getProperty("user.home")		// User home directory.
			)
	);
	
	/**
	 * Application menu entries.
	 */
	ArrayList<String> menuEntries = new  ArrayList<String>(
			Arrays.asList(
							"ci - Create EC2 Instance(s)", 
							"ia - Set Instance(s) attributes",
							"az - Get availaibilty zones",
							"ik - Get instance information using its Key pair",
							"ii - Get instance information using its ID",
							"ti - Terminate EC2 Instance(s)",
							"x  - Quit the application"
						)
	);
	
	/**
	 * AWS EC2 US regions  
	 * <p>
	 * <b>Note</b>: Only US regions are used, for simplicity.
	 * </p>
	 */
	HashMap<String, Enum<Regions>> ec2Regions = new HashMap<String, Enum<Regions>>()
	{ 
		// Avoid compiler warning. 
		private static final long serialVersionUID = 1L;
		// Initialize the ec2Regions. 
		{
			put("us-east-1", Regions.US_EAST_1); // US East (N. Virginia) 
			put("us-west-1", Regions.US_WEST_1); // US West (N. California)
			put("us-west-2", Regions.US_WEST_2); // US West (Oregon)	
		}
	};
	
	
}
