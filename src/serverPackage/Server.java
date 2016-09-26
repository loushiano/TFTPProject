package serverPackage;


// This class is the server side of assignment 1.
// The server receives from the intermediate host a packet containing a read/write/invalid request,
// reads and validates it, and sends a response back to the intermediate host
// Last edited 16th July, 2016

import java.io.*;
import java.net.*;
import java.util.Arrays;

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

		for (;;){
		
			 byte data[] = new byte[100];
				receivePacket = new DatagramPacket(data, data.length);
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
				
				ConnectionManager connectionManagerThread = new ConnectionManager(receiveSocket, receivePacket, data);
				connectionManagerThread.start();
		}
	}



	public static void main( String args[] )
	{
		Server c = new Server();
		try {
			c.receiveAndRespond();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
