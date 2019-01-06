package com.acloudysky.ec2;

import java.util.Scanner;

/**
 * Defines the attributes and methods required to implement the {@link SimpleUI} class.
 * It instantiates the Scanner class to process user's input.
 * @author Michael Miele
 *
 */
public abstract class UserInterface {

	/**
	 * Platform specific separator.
	 */
	protected static final String newline = System.getProperty("line.separator");
	
	/**
	 * Scanner object to interact with the application's user. 
	 * For more information, see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html" target="_blank">Class Scanner</a>.
	 */
	protected static Scanner _input;
	
	/**
	 * Instantiates a new Scanner that produces 
	 * values scanned from the specified input stream. 
	 */
	UserInterface() {
		
		// Instantiate Scanner object to read user's input.
		_input = new Scanner(System.in);

	}
	
	
	 /** 
	  * Prompts user for integer input. Read user's input. 
	  * @return The user's input
	 */
	protected int readUserInputInt() {
		System.out.print(">>");
		int result = Integer.parseInt(_input.nextLine());
		return result;
	}
	
	/** 
	  * Prompts user for string input. Read user's input. 
	  * @return The user's input
	 */
	protected String readUserInputString() {
		System.out.print(">>");
		String result = String.valueOf(_input.nextLine());
		return result;
	}
	
	/**
	 * Processes user's input.
	 * @see UserInterface#processUserInput()
	 */
	public abstract void processUserInput ();
	
}
