package com.acloudysky.ec2;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.acloudysky.auth.AuthenticateAwsServiceClient;


/**
 * Instantiates the authenticated EC2 service client, initializes the operations and the UI classes.  
 * Before running the code, you need to set up your AWS security credentials. You can do this by creating a 
 * file named "credentials" at ~/.aws/ (C:\Users\USER_NAME\.aws\ for Windows users) and saving the following lines in 
 * the file:
 *<pre>
 *[default]
 *  aws_access_key_id = your access key id
 *  aws_secret_access_key = your secret key
 *</pre>
 *<p>
 * For more information, see <a href="http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html" target="_blank">Providing AWS Credentials in the AWS SDK for Java</a> 
 * and <a href="https://console.aws.amazon.com/iam/home?#security_credential" target="_blank">Welcome to Identity and Access Management</a>.
 * </p>
 * <b>WARNING</b>: To avoid accidental leakage of your credentials, DO NOT keep the credentials file in your source directory.
 * @author Michael Miele
 */
public class Main {

	// Debug flag to use for testing purposes.
	public static boolean DEBUG = false;
		
	private static SimpleUI sui;
	
	// Authenticated EC2 client.
	private static AmazonEC2 ec2Client = null;
	
	// Selected region. String value such as "us-west-2".
	private static String region = null;
	
	// Selected EC2 region. Enumerated value.
	private static Regions currentRegion; 
	
	/**
	 * Instantiates the EC2 client and initializes the operation class. 
	 * Instantiates the SimpleUI class to display the selection menu and process the user's input. 
	 * @see SimpleUI#SimpleUI(AmazonEC2) 
	 * @see EC2Operations#InitEC2Operations(AmazonEC2)
	 * @param args; 
	 * 		args[0] = The EC2 region, for instance "us-west-2". Notice only US regions are allowed. 
	 * 
	 */
	public static void main(String[] args) {
		
		// Display application menu.
		Utility.displayWelcomeMessage("AWS EC2");
		
		// Read input parameters.
		try {
				region = args[0];
				// System.out.println(region);
		}
		catch (Exception e) {
			System.out.println("IO error trying to read application input! Assigning default values.");
			// Assign default values if none are passed.
			if (args.length==0) {
				region = "us-west-2";
			}
			else {
				System.out.println("IO error trying to read application input!");
				System.exit(1); 
			}
		}
		
		try {
				// Instantiate the AuthenticateAwsServiceClient class. 
				AuthenticateAwsServiceClient authClient = new AuthenticateAwsServiceClient();
				
				// Get the region enum value. 
				currentRegion = Utility.getRegion(region);
				
				// Get the authenticated client. 
				if (currentRegion != null)
					ec2Client = authClient.getAuthenticatedEC2Client(currentRegion);
				
		} 
        catch (AmazonServiceException ase) {
	        	StringBuffer err = new StringBuffer();
	        	
	        	err.append(("Caught an AmazonServiceException, which means your request made it "
	                      + "to Amazon EC2, but was rejected with an error response for some reason."));
	       	   	err.append(String.format("%n Error Message:  %s %n", ase.getMessage()));
	       	   	err.append(String.format(" HTTP Status Code: %s %n", ase.getStatusCode()));
	       	   	err.append(String.format(" AWS Error Code: %s %n", ase.getErrorCode()));
	       	   	err.append(String.format(" Error Type: %s %n", ase.getErrorType()));
	       	   	err.append(String.format(" Request ID: %s %n", ase.getRequestId()));
	        	
	       	   	System.out.println(err.toString());
    	} 
		catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means the client encountered "
	                    + "a serious internal problem while trying to communicate with EC2 , "
	                    + "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
		}
		
		if (ec2Client != null) {
			
			// Initialize the EC2Operations class to handle EC2 REST API calls.
        	EC2Operations.InitEC2Operations(ec2Client);
			
        	if (DEBUG)
        		System.out.println("Main: Ec2 client " + ec2Client.toString());
        	
        	
			// Instantiate SmpleUI class.
			sui = new SimpleUI(ec2Client);
		
			// Process user's input.
			sui.processUserInput();
		}
		else 
			String.format("Error %s", "Main: authorized EC2 client object is null.");
		
		Utility.displayGoodbyeMessage("AWS EC2");	
	}

}
