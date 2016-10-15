package Clientpackage;



import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.FileNotFoundException;

import utilities.Constants;

import java.io.FileInputStream;

import java.nio.file.AccessDeniedException;

import java.io.File;

/*

 * Author: Sahaj Arora

 * This class provides user interface for a client to interact with the system.

 */



public class ClientUI {



	

	

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

	                	FileInputStream fis = null;

	                	boolean correctPath = true;

	                	AccessDeniedException adFile = null;

	                	//enter the infite loop and check if we have the right path.

	                	

	                	System.out.print("Enter a relative file path to read from the server: ");

	                	input = br.readLine();   



	                	filePath = input;

	                	System.out.println();

	                	

	                	

	                	for(;;){

	                		

		                	System.out.print("Enter a full file path to which u want to write the file on: ");

	                		input = br.readLine();   

	                		correctPath = true;

	                		try{

	                			//here we will get the file path.

	                			fis = new FileInputStream(input);

	                		}catch(FileNotFoundException e ){

	                			correctPath = false;

	                			System.out.println("File Not Found");

	                			System.out.println("Please enter the correct file path");

	                		}

	                		

	                		File file = new File(input);

	                		

	                		if(file.canWrite() && correctPath){

	                			System.out.println("You are allowed to write to this file");

	                		}else if (correctPath){

	                			System.out.println("You are not allowed to write to this file");

	                			correctPath = false;

	                		}

	        

	                		if(correctPath){

	                			break;

	                		}

	                	}



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

	                	

	            

	                	Thread thread =new Thread(new ClientThread(requestType, filePath,filewritepath,filePath2, vqMode, tnMode));

	                	thread.start();

	                   // WRITE REQUEST ---------------------------------------------------------------------------------- 

	                } else if(input.equals(Constants.CMD_WRQ)){

	                	

	                	requestType = Constants.WRITE_REQUEST;

	                	FileInputStream fis = null;

	                	boolean correctPath = true;

	                	

	                	//enter the infite loop and check if we have the right path.

	                	for(;;){

	                		

	                		System.out.print("Enter a full file path to read from: ");

	                		input = br.readLine();   

	                		correctPath = true;

	                		try{

	                			//here we will get the file path.

	                			fis = new FileInputStream(input);

	                		}catch(FileNotFoundException e ){

	                			correctPath = false;

	                			System.out.println("File Not Found");

	                			System.out.println("Please enter the correct file path");

	                		}



	                		if(correctPath){

	                			break;

	                		}

	                		

	                	}

	                

	                	

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

	                	Thread thread =new Thread(new ClientThread(requestType, filePath,filewritepath,filePath2, vqMode, tnMode));

	                	thread.start();

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



