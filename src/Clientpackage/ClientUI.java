package Clientpackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import utilities.Constants;

/*
 * Author: Sahaj Arora
 * This class provides user interface for a client to interact with the system.
 */

public class ClientUI {
	static Client c = new Client();
	
	 public static void main(String[] args) {

	        BufferedReader br = null;
	        
	        String requestType = "", filePath,filePath2, vqMode,filewritepath = null;
	
	        String tnMode = Constants.NORMAL;

	        try {

	            br = new BufferedReader(new InputStreamReader(System.in));

	            while (true) {

	            	System.out.println("Default Mode is " + tnMode);
	            	System.out.println();
	            	System.out.println("Type help for a list of available commands.");
	            	System.out.println();
	                System.out.print("Enter command: ");
	                String input = br.readLine();
	                
	                if (input.equals(Constants.CMD_HELP)){
	                	System.out.println();
	                	System.out.println("List of Commands:");
	                	System.out.println("wrq - Send a write request");
	                	System.out.println("rrq - Send a read request");
	                	System.out.println("mode - Display the current mode (Normal/Test)");
	                	System.out.println("changeMode - Change the mode (Normal/Test)");
	                	System.out.println("shutdown - Shut down Client");
	                	System.out.println();
	                }
	                
	                else if (input.equals(Constants.CMD_MODE)) {
	                	System.out.println("Current mode: " + tnMode);
	                	System.out.println();
	                }
	                
	                else if (input.equals(Constants.CMD_CHANGE_MODE)){
	                	System.out.print("Set mode (normal/test): ");
	                    input = br.readLine();
	                    while (!(input.equals(Constants.NORMAL) || (input.equals(Constants.TEST)))){
	                		System.out.print("Please enter correct mode (normal or test): ");
		                	input = br.readLine();
	                	}
	                    
	                    tnMode = input;
	                    System.out.println("Mode set to " + input);
	                    System.out.println();
	                    
	                }
	                
	                else if (input.equals(Constants.CMD_RRQ) ){	                	
	                	
	                	requestType = Constants.READ_REQUEST;
	                		
      	
	                	System.out.print("Enter a relative file path: ");
	                	input = br.readLine();
	                	filePath = input;
	                	System.out.println();
	                	System.out.print("Enter a full file path to which u want to write the file on: ");
	                	input = br.readLine();
	                	filePath2 = input;
	                	System.out.println();
	                	System.out.print("Enter mode (verbose or quiet): ");
	                	input = br.readLine();
	                	
	                	while (!(input.equals(Constants.VERBOSE) || (input.equals(Constants.QUIET)))){
	                		System.out.print("Please enter correct mode (verbose or quiet): ");
		                	input = br.readLine();
	                	}
	                	
	                	vqMode = input;
	                	System.out.println();
	                	
	            
	                	c.sendAndReceive(requestType, filePath,filewritepath,filePath2, vqMode, tnMode);
	                	
	                    
	                } else if(input.equals(Constants.CMD_WRQ)){
	                	requestType = Constants.WRITE_REQUEST;
	                	System.out.print("Enter a full file path to read from: ");
	                	input = br.readLine();
	                	filePath = input;
	                	System.out.println();
	                	System.out.print("Enter a relative file path to write To:");
	                	input = br.readLine();
	                	filewritepath =input;
	                	
	                	System.out.println();
	                	System.out.print("Enter mode (verbose or quiet): ");
	                	input = br.readLine();
	                	
	                	while (!(input.equals(Constants.VERBOSE) || (input.equals(Constants.QUIET)))){
	                		System.out.print("Please enter correct mode (verbose or quiet): ");
		                	input = br.readLine();
	                	}
	                	
	                	vqMode = input;
	                	System.out.println();
	                	
	                	filePath2=null;
	                	c.sendAndReceive(requestType, filePath,filewritepath,filePath2, vqMode, tnMode);
	                	
	                }
	                
	                
	                else if (Constants.CMD_SHUTDOWN.equals(input)) {
	                    System.out.println("Client shut down.");
	                    System.exit(0);
	                }

	                else {
	                	System.out.println("Command not recognized. Please try again.");
	                }
	                
	               
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	    }
}
