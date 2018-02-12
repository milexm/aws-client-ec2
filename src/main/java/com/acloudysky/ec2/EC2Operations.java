package com.acloudysky.ec2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.BlockDeviceMapping;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.EbsBlockDevice;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/***
 * Performs EC2 operations selected by the user. 
 * The various methods call the related AWS EC2 library functions that in turn call the 
 * REST APIs which interact with the EC2 service. 
 * @author Michael Miele
 *
 */ 
public class EC2Operations {

	private static AmazonEC2 ec2Client;

	
	/*
	 * Utilities *
	 */
	private static Object getInstanceName(Instance instance) { 
        for (Tag tag : instance.getTags()) { 
            if (tag.getKey().equalsIgnoreCase("name")) { 
                return tag.getValue(); 
            } 
        } 
        return null; 
    } 
	
	private static Object getInstanceOwner(Instance instance) { 
        for (Tag tag : instance.getTags()) { 
            if (tag.getKey().equalsIgnoreCase("owner")) { 
                return tag.getValue(); 
            } 
        } 
        return null; 
    } 
	
	/*
	 * Gathers EC2 and format information for the passed instance
	 * @param instance The instance whose info must be displayed.
	 */
	private static void getInstanceInformation(Instance instance, StringBuffer buffer){
		
		buffer.append(String.format("%n"));
    	buffer.append(String.format("%nInstance Name:         %s%n", getInstanceName(instance)));
    	buffer.append(String.format("Key Name:              %s%n", instance.getKeyName()));
    	buffer.append(String.format("Instance ID:           %s%n", instance.getInstanceId()));
    	buffer.append(String.format("Image ID:              %s%n", instance.getImageId()));
    	buffer.append(String.format("Kernel ID:             %s%n", instance.getKernelId()));
    	buffer.append(String.format("Instance Type:         %s%n", instance.getInstanceType()));
    	buffer.append(String.format("Instance Architecture: %s%n", instance.getArchitecture()));
    	buffer.append(String.format("Instance State:        %s%n", instance.getState().getName()));
    	// buffer.append(String.format("Public DNS Name:       %s%n", instance.getPublicDnsName()));
    	buffer.append(String.format("Hypervisor:            %s%n", instance.getHypervisor()));
    	buffer.append(String.format("Owner:                 %s%n", getInstanceOwner(instance)));
	}
	
	/*
	 * Methods * 
	 */
	
	/**
	 * Initializes client.
	 * @param client Authenticated EC2 client.
	 */
	public static void InitEC2Operations(AmazonEC2 client) {
		
		// Initialize authorized client.
		ec2Client = client;
		
	}
	

	/*
	 * Displays information for those instances associated with the specified key pair.
	 * @param instances A list of EC2 instances.
	 */
	private static void displayInstancesInformation(List<Instance> instances){
		StringBuffer buffer = new StringBuffer();
	   
		for (Instance instance : instances)
			getInstanceInformation(instance, buffer);   	
		
		// Display instance information.
		System.out.println(buffer.toString());
	}
	
	
	/*
	 * EC2 Operations *
	 */
	
	/**
	   * Creates a new EC2 instance
	   * @param ami_id The image Id (example, ami-e3106686)
	   * @param instanceType The instance type (example, t1.micro)
	   * @param instanceNumber The number of instances
	   * @param keyname The name of the security key (example, Doc_Key)
	   * @param subnetId The id of the subnet (example, subnet-827929e9)
	   * @param availZone The availability zone (example, us-east-1d)
	   * @param ec2Client The authorized EC2 client
	   */
	  public static void createInstances(AmazonEC2 ec2Client, 
			  String ami_id, String instanceType, Integer instanceNumber, 
			  String keyname, String subnetId, String availZone) {
	   
		  // The collection of instances.
		  List<Instance> instances = new ArrayList<Instance>();

		   try {
			   	// Initialize the instance request.	
			   	RunInstancesRequest instanceRequest = new RunInstancesRequest();
			    	
			   	// Set the type of instance to create.
			   	instanceRequest.setInstanceType(instanceType);
			   	
			   	// Set the image Id.
			    instanceRequest.setImageId(ami_id);
			    
			    // Set max and min instances.
			    instanceRequest.setMaxCount(instanceNumber);
			    instanceRequest.setMinCount(instanceNumber);
			    
			    // Add the security group to the request.
			    ArrayList<String> securityGroups = new ArrayList<String>();
			    instanceRequest.setSecurityGroupIds(securityGroups);
			    instanceRequest.setKeyName(keyname);
			    
			    // Set the subnet Id
			    instanceRequest.setSubnetId(subnetId);
			    
			    //*************************** Add the block device mapping ************************//
				
			    // Goal: Setup block device mappings to ensure that we will not delete
			    // the root partition on termination.
	
			    // Create the block device mapping to describe the root partition.
			    BlockDeviceMapping blockDeviceMapping = new BlockDeviceMapping();
			    blockDeviceMapping.setDeviceName("/dev/sda1");
	
			    // Set the delete on termination flag to true.
			    EbsBlockDevice ebs = new EbsBlockDevice();
			    ebs.setDeleteOnTermination(Boolean.TRUE);
			    ebs.setVolumeSize(3);
			    blockDeviceMapping.setEbs(ebs);
	
			    // Add the block device mapping to the block list.
			    ArrayList<BlockDeviceMapping> blockList = new ArrayList<BlockDeviceMapping>();
			    blockList.add(blockDeviceMapping);
	
			    // Set the block device mapping configuration in the launch specifications.
			    instanceRequest.setBlockDeviceMappings(blockList);
	
			    //*************************** Add the availability zone ************************//
			    // Setup the availability zone to use. Note we could retrieve the availability
			    // zones using the ec2.describeAvailabilityZones() API. For this demo we will just use
			    // us-east-1b.
			    Placement placement = new Placement(availZone);
			    instanceRequest.setPlacement(placement);
			    
			    
	
			    // Create the instance(s).
			    RunInstancesResult result = ec2Client.runInstances(instanceRequest);
			    
			    // Store the created instance(s).
			    instances.addAll(result.getReservation().getInstances());
			
			    // Name the instances and assign the owner.
			    int idx = 0;
		    	for (Instance instance : instances) {
		    		CreateTagsRequest createTagsRequest = new CreateTagsRequest();
		    		createTagsRequest.withResources(instance.getInstanceId()) 
		    		.withTags(new Tag("Name", "DocLabLinux" + idx)) 
		    		.withTags(new Tag("owner", "Michael"));
		    		ec2Client.createTags(createTagsRequest); }	
		    }
		   catch (AmazonServiceException e) {
		        // Write out any exceptions that may have occurred.
		        System.out.println("Error cancelling instances");
		        System.out.println("Caught Exception: " + e.getMessage());
		        System.out.println("Reponse Status Code: " + e.getStatusCode());
		        System.out.println("Error Code: " + e.getErrorCode());
		        System.out.println("Request ID: " + e.getRequestId());
		    }
		    
		    // Display instance information.
		    StringBuffer buffer = new StringBuffer();
		    
		    for ( Instance instance : instances) 
		    	getInstanceInformation(instance, buffer);
		
		    // Display instances information.
		    System.out.println(buffer.toString());
	    
	  }
	
	
	/**
	 * Gets information for the instance with the specified Id.
	 * @param instanceId The id of the instance
	 */
	public static void getInstanceInformation(String instanceId) {
   	  
		DescribeInstancesRequest describeInstancesRequest=new DescribeInstancesRequest();
   	  	List<String> list=new ArrayList<String>();
   	  	list.add(instanceId);
   	  	describeInstancesRequest.setInstanceIds(list);
   	  	DescribeInstancesResult result =ec2Client.describeInstances(describeInstancesRequest);
   	  
   	  	List<Reservation> reservations=result.getReservations();
   	  
   	  	// System.out.println("Retrieved following reservations:");
   	  	int runningInstanceGroups=0;
   	  	int runningInstances=0;
   	  	StringBuffer buffer = new StringBuffer();
   	  	for (Reservation reservation : reservations) {
   	  		List<Instance> instances=reservation.getInstances();
   	  	runningInstanceGroups++;
   	    runningInstances+=instances.size();
   	 
   	    for ( Instance instance : instances) 
   	    	getInstanceInformation(instance, buffer);
   	    
   	    buffer.append(String.format("Running Instance Groups:       %d%n", runningInstanceGroups));
   	    buffer.append(String.format("Running Instances:             %d%n", runningInstances));
   	  }
   	  
   	  System.out.println(buffer.toString());
	}
	
	
	
	/**
	 * Gets available instances associated with a specific key pair.
	 * Displays related information.
	 * @param keyName The key pair name associated with the instances.
	 */
	public static void getInstancesInformation(String keyName) {
		List<Instance> resultList = new ArrayList<Instance>();
		DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances();
		List<Reservation> reservations = describeInstancesResult.getReservations();
		for (Iterator<Reservation> iterator = reservations.iterator(); iterator.hasNext();) {
			Reservation reservation = iterator.next();
			for (Instance instance : reservation.getInstances()) {
				if (instance.getKeyName().equals(keyName))
					resultList.add(instance);
			}
		}
		displayInstancesInformation(resultList);
		
	}	
	
	 /**
     * Lists the availability zones in a region, and the instances running in those zones.
     */
    public static void getAvailabilityZones() {
    	StringBuffer buffer = new StringBuffer();
 
    	// Get information about availability zones.
    	DescribeAvailabilityZonesResult availabilityZonesResult = ec2Client.describeAvailabilityZones();
        List<AvailabilityZone> availabilityZones = availabilityZonesResult.getAvailabilityZones();
 
        buffer.append(String.format("You have access to %d availability zones:%n",  availabilityZones.size()));
        for (AvailabilityZone zone : availabilityZones) 
        	buffer.append(String.format(" - %s (%s) %n",  zone.getZoneName(), zone.getRegionName()));
           
     
        // Get information about running instances.
        DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances();
        Set<Instance> instances = new HashSet<Instance>();
        for (Reservation reservation : describeInstancesResult.getReservations()) {
             instances.addAll(reservation.getInstances());
        }
        buffer.append(String.format("%nYou have %d Amazon EC2 instance(s) running.", instances.size()));
        
        System.out.println(buffer.toString());
        

    }
	
    /**
     * Sets the instance name and owner tag attributes.
     * @param instanceId The Id of the instance.
     * @param name The name to assign to the instance.
     * @param owner The name of the owner.
     */
	public static void setInstanceAttributes(String instanceId, String name, String owner) {
		
		List<Instance> resultList = new ArrayList<Instance>();
		DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances();
		List<Reservation> reservations = describeInstancesResult.getReservations();
		
		// Obtain the instances with the specified Id
		for (Iterator<Reservation> iterator = reservations.iterator(); iterator.hasNext();) {
			Reservation reservation = iterator.next();
			for (Instance instance : reservation.getInstances()) {
				if (instance.getInstanceId().equals(instanceId))
					resultList.add(instance);
			}
		}
	
    	// Assign the names to the instances and the owner.
    	int idx = 0;
    	for (Instance instance : resultList) {
    		if (instance.getInstanceId().equals(instanceId)) {
    			CreateTagsRequest createTagsRequest = new CreateTagsRequest();
    			createTagsRequest.withResources(instance.getInstanceId()) 
    			.withTags(new Tag("Name", name + idx)) 
    			.withTags(new Tag("owner", owner));
    			ec2Client.createTags(createTagsRequest);
    		}	
    	  idx++;
    	}
    	
    	// Display instance information.
    	displayInstancesInformation(resultList);
	}
	
	
	/**
     * Terminates one or more instances. This operation is idempotent i.e., if an instance 
     * is terminated more than once, each call succeeds.
     * <p>
     * <b>Note</b>. Terminated instances remain visible after termination (for approximately one hour).
     * By default, Amazon EC2 deletes all EBS volumes that were attached when the instance was launched. 
     * Volumes attached after the instance was launch continue running.
     * </p>
     * <p>
     * For more information, see <a
     * href=
     * "http://www.programcreek.com/java-api-examples/index.php?api=com.amazonaws.services.ec2.model.TerminateInstancesResult"
     * >Java Code Examples for com.amazonaws.services.ec2.model.TerminateInstancesResult</a>.
     * </p>
     * <p>
     * For more information about troubleshooting, see <a href=
     * "http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/TroubleshootingInstancesShuttingDown.html"
     * >Troubleshooting Terminating Your Instance</a> in the <i>Amazon Elastic
     * Compute Cloud User Guide</i>.
     * </p>
     * 
     * @param instanceId The Id of the instance to terminate.
     * @return True  of the termination succeeded.   
     */
	public static boolean terminateInstances(String instanceId) {
	    TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest();
	    terminateRequest.withInstanceIds(instanceId);

	    if(ec2Client == null){
	        throw new RuntimeException("The Ec2 client is not initialized");
	    }
	    TerminateInstancesResult result = ec2Client.terminateInstances(terminateRequest);
	    List<InstanceStateChange> stateChanges = result.getTerminatingInstances();
	    boolean terminatedInstance = false;
	    for (InstanceStateChange stateChange : stateChanges) {
	        if (instanceId.equals(stateChange.getInstanceId())) {
	            terminatedInstance = true;

	            InstanceState currentState = stateChange.getCurrentState();
	            if (currentState.getCode() != 32 && currentState.getCode() != 48) {
	            	 System.out.println(String.format(
	                        "Machine state for id %s should be terminated (48) or shutting down (32) but was %s instead",
	                        instanceId, currentState.getCode()));
	                return false;
	            }
	        }
	    }

	    if (!terminatedInstance) {
	    	 
	    	 String msg = "Matching terminated instance was not found for instance "; 
		     System.out.println(String.format("%s %s", msg, instanceId));
	        return false;
	    }

	    return true;
	}
	
																																																																			
}
