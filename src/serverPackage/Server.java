package serverPackage;


// This class is the server side of assignment 1.
// The server receives from the intermediate host a packet containing a read/write/invalid request,
// reads and validates it, and sends a response back to the intermediate host
// Last edited 16th July, 2016

import java.io.*;
import java.net.*;
import java.util.Arrays;

import utilities.Constants;
import utilities.Utility;

public class Server {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	private boolean isReadRequest, isWriteRequest;
	
	
	
	public Server()
	{
		try {
			// Construct a datagram socket and bind it to any available 
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.
			
			
			// Construct a datagram socket and bind it to port 69 
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(69);

			// to test socket timeout (2 seconds)
			//receiveSocket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
		
		
		
	}

	/**
	 * Receive request, validate it, and send a response back.
	 * @throws Exception
	 */
	public void receiveAndRespond() throws Exception
	{
			String filepath=null;
		for (;;){
		
			 byte data[] = new byte[100];
				receivePacket = new DatagramPacket(data, data.length);
				if(Utility.shutDown){
					System.exit(1);
				}
				System.out.println("Server: Waiting for Packet.\n");

				// Block until a datagram packet is received from receiveSocket.
				try {        
					System.out.println("Waiting..."); // so we know we're waiting
					receiveSocket.receive(receivePacket);
				} catch (IOException e) {
					System.out.print("IO Exception: likely:");
					System.out.println("Receive Socket Timed Out.\n" + e);
					e.printStackTrace();
					System.exit(1);
				}
					filepath=getPath(receivePacket.getData());
					
				ConnectionManager connectionManagerThread = new ConnectionManager(receiveSocket, receivePacket, data,filepath);
				connectionManagerThread.start();
		}
	}



	private String getPath(byte[] data) {
		byte stringForm[]=new byte[100];
		int j=0;
		for(int i=2;i<100;i++){
			if(data[i]==0){
				break;
			} 
			stringForm[j]=data[i];
			j++;
			
		}
		byte stringForm2[]=new byte[j];
		System.arraycopy(stringForm, 0,stringForm2,0,j);
		return new String(stringForm2,0,j);
	}

	public static void main( String args[] )
	{
		Server c = new Server();
		Thread su=new ServerUI();
		su.start();
		try {
			c.receiveAndRespond();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	class ServerUI extends Thread
	{
	    /**
	     * The text area where this thread's output will be displayed.
	     */
	    

	    public ServerUI() {
	      
	    }

	    public void run() {
	    	 BufferedReader br = null;
	    	 String mode=Constants.VERBOSE;
	    	 try{
	    		 br = new BufferedReader(new InputStreamReader(System.in));
	    	 while (true) {

	            	System.out.println("Default Mode is " + Constants.VERBOSE);
	            	System.out.println();
	            	System.out.println("Type help for a list of available commands.");
	            	System.out.println();
	                System.out.println("Enter command: ");
	                String input = br.readLine();
	                if (input.equals("help")){
	                	System.out.println();
	                	System.out.println("List of Commands:");
	                	System.out.println("mode - Display the current mode (verbose/quiet)");
	                	System.out.println("changeMode - Change the mode (verbose/quiet)");
	                	System.out.println("shutdown - Shut down Server");
	                	System.out.println();
	                }else if (input.equals("mode")) {
	                	System.out.println("Current mode: " +mode );
	                	System.out.println();
	                }else if (input.equals("change mode")){
	                	System.out.print("Set mode (verbose/quiet): ");
	                    input = br.readLine();
	                    while (!(input.equals(Constants.VERBOSE) || (input.equals(Constants.QUIET)))){
	                		System.out.print("Please enter correct mode (verbose or quiet): ");
		                	input = br.readLine();
	                	}
	                    
	                    mode = input;
	                    System.out.println("Mode set to " + input);
	                    System.out.println();
	                    
	                }
	                else if(input.equals("shut down")){
	                	Utility.shutDown=true;
	                }else{
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
	

