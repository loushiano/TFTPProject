package intermediateHost;

/*Iteration 1
 * Name: Ibrahim Ali fawaz
 * Student No. 100986043
 */

//IntermediateHost.java
//Intermediate Host receives packet from the client and forwards it to the server. It then
//receives a packet from the server and forwards to the client.

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import serverPackage.Server;

import utilities.Utility;

public class ErrorSimulator {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket, sendToClientSocket,sendReceiveSocket,receiveClientSocket;
	int clientPort; //client port where the request comes from
	private boolean flag;
	
	public ErrorSimulator(){
		clientPort = 0;
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);
			sendReceiveSocket=new DatagramSocket();
		} catch (SocketException se) {
	         se.printStackTrace();
	         System.exit(1);
	    }
	}
	
	public void receiveSendPacket(int testCode,int packetNum,int AckData){
		
			// Construct a DatagramPacket for receiving packets up 
		      // to 100 bytes long (the length of the byte array).

		      byte data[] = new byte[1400];
		      receivePacket = new DatagramPacket(data, data.length);
		      System.out.println("IntermediateHost: Waiting for Packet.\n");

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

		     
		      // Process the received datagram.
		      Thread simulationManager =new SimulationManager(receivePacket,testCode,packetNum,AckData);
		      simulationManager.start();
		      
	}
	
	public static void main( String args[] )
	{
		ErrorSimulator e = new ErrorSimulator();
		//Thread waitingThread=new WaitingThread();
		//waitingThread.start();
		ErrorSimulatorUI ui=new ErrorSimulatorUI();
		ArrayList<Integer> results=ui.run();
		e.receiveSendPacket(results.get(0),results.get(1),results.get(2));
	}
	
	
	
}