package Clientpackage;

/*Assignment 1
 * Name: Sahaj Arora
 * Student No. 100961220
 */

// Client.java
// This class is the client side for Assignment 1. The client sends read/write/invalid requests to the IntermediateHost
// 11 times.
// Last edited 16th September, 2016

import java.io.*;
import java.net.*;
import java.util.Arrays;

import utilities.Utility;

public class Client {

   DatagramPacket sendPacket, receivePacket;
   DatagramSocket sendReceiveSocket;
   
   private static final byte READ = 1, WRITE = 2, INVALID = 5;

   public Client()
   {
      try {
         // Construct a datagram socket and bind it to any available 
         // port on the local host machine. This socket will be used to
         // send and receive UDP Datagram packets.
         sendReceiveSocket = new DatagramSocket();
      } catch (SocketException se) {   // Can't create the socket.
         se.printStackTrace();
         System.exit(1);
      }
   }

   /**
    * Send and receive request
    * @param type Type of request - Read/Write/Invalid
    * @param iteration Current iteration Number.
    */
   public void sendAndReceive(String reqType,String filepath,String vqMode,String tnMode)
   {
      // Prepare a DatagramPacket and send it via sendReceiveSocket
      // to port 23 on the destination host.
	   
	  
	   
	   String fileName = "testFile.txt";
	   byte[] fileNameBinary = fileName.getBytes();
	   
	   String mode = "netascii";
	   byte[] modeBinary = mode.getBytes();
	   
	   byte[] request = new byte[2 + fileNameBinary.length + 1 + modeBinary.length 
	                     + 1];
	   
	   request[0] = 0;
	   request[1] = 2;
	   
	   int j = 0;
	   //Store bytes of fileName in the request array
	   for (int i = 2; i < (fileNameBinary.length+2); i++){
		   
		   request[i] = fileNameBinary[j];
		   j++;
	   }
	   
	   int lengthTillFirstZero = 2 + fileNameBinary.length;
	   
	   request[lengthTillFirstZero] = 0;
	   
	   int tempCounter = 0;
	   
	   //Store bytes of mode (netascii) in the request array
	   for (int x = lengthTillFirstZero + 1; 
			   x < lengthTillFirstZero + modeBinary.length + 1; x++){
		   
		   request[x] = modeBinary[tempCounter];
		   tempCounter++;
	   }
	   
	   //Ending 0 byte
	   request[lengthTillFirstZero + modeBinary.length + 1] = 0;
	   
	   System.out.println("Client: Sending a packet containing:\n" + request);
	   

      // Construct a datagram packet that is to be sent to a specified port 
      // on a specified host.
      // The arguments are:
      //  msg - the message contained in the packet (the byte array)
      //  msg.length - the length of the byte array
      //  InetAddress.getLocalHost() - the Internet address of the 
      //     destination host.
      //     In this example, we want the destination to be the same as
      //     the source (i.e., we want to run the client and server on the
      //     same computer). InetAddress.getLocalHost() returns the Internet
      //     address of the local host.
      //  23 - the destination port number on the destination host.
      try {
         sendPacket = new DatagramPacket(request, request.length,
                                         InetAddress.getLocalHost(), 23);
      } catch (UnknownHostException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Client: Sending packet:");
      System.out.println("To host: " + sendPacket.getAddress());
      System.out.println("Destination host port: " + sendPacket.getPort());
      int len = sendPacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");
      System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"
      System.out.print("Containing Bytes: ");
      System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(), len)));
      
      // Send the datagram packet to the server via the send/receive socket. 

      try {
         sendReceiveSocket.send(sendPacket);
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      System.out.println("Client: Packet sent.\n");

      // Construct a DatagramPacket for receiving packets up 
      // to 100 bytes long (the length of the byte array).

      byte data[] = new byte[100];
      receivePacket = new DatagramPacket(data, data.length);

      try {
         // Block until a datagram is received via sendReceiveSocket.  
         sendReceiveSocket.receive(receivePacket);
      } catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      // Process the received datagram.
      System.out.println("Client: Packet received:");
      System.out.println("From host: " + receivePacket.getAddress());
      System.out.println("Host port: " + receivePacket.getPort());
      len = receivePacket.getLength();
      System.out.println("Length: " + len);
      System.out.print("Containing: ");

      // Form a String from the byte array.
      String received = new String(data,0,len);   
      System.out.println(received);
      
      System.out.print("Containing Bytes: ");
      System.out.println(Arrays.toString(Utility.getBytes(receivePacket.getData(), len)));
   
   }
   
  

   public static void main(String args[])
   {
      Client c = new Client();
      
      // 11 iterations to send and receive requests
      for (int i = 1; i < 12; i++){
    	  
    	  System.out.println("");
    	  System.out.println("ITERATION " + i + " ------->>>>>>>>>");
    	  byte type = 0; 
    	  
    	  if (i == 11){
    		  type = INVALID; //invalid type : can only be 1 or 2
    	  }
    	  else if (i%2 == 0) type = READ; //Read request for even iterations
    	  else if (i%2 != 0) type = WRITE; //Write request for odd iterations
    	  
    	  c.sendAndReceive("rrq","text.txt","verbose","test");
      }
   }
}