package com.acloudysky.ec2;


import java.io.BufferedReader;
import java.io.IOException;

import com.acloudysky.ec2.Utility;
import com.amazonaws.services.ec2.AmazonEC2;

/**
* Displays the menu of choices for the user. 
* It processes the user’s input and calls the proper function based on the user’s selection. 
* <p>
* <b>Note</b>. Each function calls the related AWS EC2 Java library method, which in turn calls the related REST API.
* @author Michael Miele
*
*/
public class SimpleUI extends UserInterface {

	// Utility class object.
	// private static Utility util =  new Utility();

	private String keyName;
	private AmazonEC2 ec2Client = null;
	
	private String instanceId;
	private String instanceName; 
	private String instanceOwner;
	
	/**
	 * Initializes the SimpleUI class along with its superclass.
	 * @param client Authenticated AWS client.  
	 * @see UserInterface
	 */
	public SimpleUI(AmazonEC2 client) {
		super();
		// util.createMenu();
		ec2Client = client;
		// Display menu.
		Utility.displayMenu(Utility.getMenuEntries());
	}

	/**
	 * Read user input.
	 */
	private static String readUserInput(String msg) {
		
		// Open standard input.
		BufferedReader br = new BufferedReader(new java.io.InputStreamReader(System.in));

		String selection = null;
		
		//  Read the selection from the command-line; need to use try/catch with the
		//  readLine() method
		try {
			if (msg == null)
				System.out.print("\n>>> ");
			else
				System.out.print("\n" + msg);
			selection = br.readLine();
		} catch (IOException e) {
			System.out.println("IO error trying to read your input!");
			System.out.println(String.format("%s", e.getMessage()));
			System.exit(1);
		}
		
		return selection;

	}
	
	
	/**
	 * Executes the selected operation.
	 */
	private void performOperation(String operation) {
		

		switch(operation)
		{
			case "ci":
				try{
					// Create EC2 instance(s).
					do {
						// Obtain key pair name, for example DocLabKeys_OR
						keyName = readUserInput("EC2 key pair name: ");	
					} while(keyName.isEmpty());
					EC2Operations.createInstances(ec2Client, 
							"ami-7172b611", "t2.micro", 1, keyName, 
							"subnet-06223c5f", "us-west-2c");
					
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			
			case "ti":
				try{
					// Terminate EC2 instance(s).
					do {
						instanceId = readUserInput("Instance Id: ");
					} while(instanceId.isEmpty());
					Boolean terminated = EC2Operations.terminateInstances(instanceId);
					if (terminated) {
						System.out.println(String.format("%s", "Instances terminated"));
					}
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
		
			case "az":
				// Operation 3
				try{
					// List the availability zone for the client.
					EC2Operations.getAvailabilityZones();
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
				
			case "ik": {
				try{
					// Get info for the instances with specified key pair.
					do {
						keyName = readUserInput("EC2 key pair name: ");	
					} while(keyName.isEmpty());
					EC2Operations.getInstancesInformation(keyName);
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			case "ii": {
				try{
					// Get info for the instance with specified Id.
					do {
						System.out.println(String.format("%s", "Get the instance information, to obtain the Id. "));
						instanceId = readUserInput("Instance ID: ");	
					} while(instanceId.isEmpty());
					EC2Operations.getInstanceInformation(instanceId);
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}		
			
			case "ia": {
				try{
					// Set instance attributes.
					do {
						instanceId = readUserInput("Instance Id: ");	
						instanceName = readUserInput("Instance name: ");	
						instanceOwner = readUserInput("Instance owner: ");	
					} while(instanceId.isEmpty()|| instanceName.isEmpty() || instanceOwner.isEmpty());
					EC2Operations.setInstanceAttributes(instanceId, instanceName, instanceOwner);
				}
				catch (Exception e){
					System.out.println(String.format("%s", e.getMessage()));
				}
				break;
			}
			
			default:
				// Enter allowed value
				System.out.println(String.format("Select one of the allowed values from the menu"));
		}
		
	} 
	
	/**
	 * Processes user's input until the user exits the loop.
	 * Displays results.
	 */
	@Override
	public void processUserInput() {
		
		String normalizedUserSelection = null;
		String userSelection = null;
		
		while (true) {
			
			
			// Get user input.
			userSelection = readUserInput(null);	
			
			// Normalize user's input.
			normalizedUserSelection = userSelection.trim().toLowerCase();
			
			try{
				// Exit the application.
				if ("x".equals(normalizedUserSelection)){
					break;
				}
				else
					if ("m".equals(normalizedUserSelection)) {
						// Display menu
						Utility.displayMenu(Utility.getMenuEntries());
						continue;
					}
				
			}
			catch (Exception e){
				// System.out.println(e.toString());
				System.out.println(String.format("Input %s is not allowed%n", userSelection));
				continue;
			}
	
			// Perform operation.
			performOperation(normalizedUserSelection);
	
			
		}
		
	}

}
