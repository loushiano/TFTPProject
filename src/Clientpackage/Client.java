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

import utilities.Constants;
import utilities.Utility;

public class Client {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;


	private static final byte READ = 1, WRITE = 2, INVALID = 5;
	public int portNum;
	public boolean verboseMode;
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



		String fileName =filepath;
		byte[] fileNameBinary = fileName.getBytes();

		String mode =Constants.MODE ;
		byte[] modeBinary = mode.getBytes();

		byte[] request = new byte[2 + fileNameBinary.length + 1 + modeBinary.length 
		                          + 1];

		request[0] = 0;
		//check if the client wants a read or a write request
		if(reqType.equals(Constants.READ_REQUEST)){
			request[1] =1;
		}else{
			request[1]=2;
		}
		//check if the client wants the sending to be in test or normal mode
		if(tnMode.equals(Constants.TEST)){
			portNum=23;
		}else{
			portNum=69;
		}
		//check if the client wants the data to be transferred in verbose or quiet mode
		if(vqMode.equals(Constants.VERBOSE)){
			verboseMode=true;
		}else{
			verboseMode=false;
		}
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
					InetAddress.getLocalHost(),portNum);//IRAQI: we put 69 because we do not have host now
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(verboseMode){
			System.out.println("Client: Sending request:");
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			int len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(new String(sendPacket.getData(),0,len)); // or could print "s"
			System.out.print("Containing Bytes: ");
			System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(),0, len)));
		}
		// Send the datagram packet to the server via the send/receive socket. 

		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: request sent.\n");

		//now after sending the request we need to get back either data or acknowledgement 
		//depending on the type of request we sent. 
		byte[] ACK = {0,4,0,1};
		byte[] sendingData = new byte[516];
		sendingData[0] = 0;
		sendingData[1] = 3;
		sendingData[2] = 0;
		sendingData[3] = 0;
		byte[] data1 = new byte[512];
		byte[] ACK1 = new byte[4];
		byte [] opblock=new byte[4];
		int len,blockNum;




		//in case of read request we send the acknowledgement and receive the data 
		//from the designated file
		if (true){

			byte data[] = new byte[516];
			receivePacket = new DatagramPacket(data, data.length);
			//this flag to see if the data coming is of 512 bytes or less
			boolean flag=true;
			while(flag)
				try {
					// Block until a datagram is received via sendReceiveSocket.

					sendReceiveSocket.receive(receivePacket);
				} catch(IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			System.out.println();
			if(receivePacket.getLength()<516){
				flag=false;
			}
			// Process the received datagram.
			System.out.println("Data from server received:");
			if(verboseMode){
				System.out.println("From host: " + receivePacket.getAddress());
				System.out.println("Host port: " + receivePacket.getPort());
				System.out.println("Length: " +receivePacket.getLength());
				System.out.print("Containing: ");


				// Form a String from the byte array.
				String received = new String(data,0,receivePacket.getLength());   
				System.out.println(received);
			}
			System.arraycopy(receivePacket.getData(),0,opblock,0,4);
			System.out.print("Containing Bytes: ");
			System.out.println("opcode: "+Arrays.toString(Utility.getBytes(receivePacket.getData(),0,2)));
			System.out.println("block #: " +Utility.getByteInt(opblock));
			System.out.println("data: "+Arrays.toString(Utility.getBytes(receivePacket.getData(),4,receivePacket.getLength())));

			ACK[2] = receivePacket.getData()[2];
			ACK[3] = receivePacket.getData()[3];
			DatagramPacket sendPacketACK = new DatagramPacket(ACK, ACK.length);

			try {
				sendReceiveSocket.send(sendPacketACK);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println( "Client sent acknowledgement ");
			if(verboseMode){
				System.out.println("To Server: " + receivePacket.getAddress());
				System.out.println("Destination Server port: " + receivePacket.getPort());
				len = sendPacketACK.getLength();
				System.out.println("Length: " + len);
				System.out.print("Containing: ");
				System.arraycopy(sendPacketACK.getData(),0,opblock,0,4);
				System.out.println(new String(sendPacketACK.getData(),0,len));
				System.out.print("Containing Bytes: ");
				System.out.println("opcode: "+Arrays.toString(Utility.getBytes(sendPacketACK.getData(),0,2)));
				System.out.println("block #: " +Utility.getByteInt(opblock));
			}


		} else if(request[1]==WRITE){


			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(filepath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			DatagramPacket receivePacketACK = new DatagramPacket(ACK1, ACK1.length);
			try {
				sendReceiveSocket.receive(receivePacketACK);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Acknowledgement received");
			if(verboseMode){
				System.out.println("From host: " + receivePacketACK.getAddress());
				System.out.println("Host port: " + receivePacketACK.getPort());
				len = receivePacketACK.getLength();
				System.out.println("Length: " + len);
				System.out.print("Containing: " );

				// Form a String from the byte array.
				String received = new String(data1,0,len);   
				System.out.println(received + "\n");}
			System.arraycopy(receivePacketACK.getData(),0,opblock,0,4);
			System.out.print("Containing Bytes: ");
			System.out.print("opcode: ");
			System.out.print(Arrays.toString(Utility.getBytes(receivePacketACK.getData(),0,2)));
			System.out.println("block # "+Utility.getByteInt(opblock) );

			int n;

			/* Read the file in 512 byte chunks. */

			try {
				while ((n = in.read(data1)) != -1) {
					/* 
					 * We just read "n" bytes into array data. 
					 * Now write them to the output file. 
					 */
					System.arraycopy(sendingData, 0,opblock,0,4);
					blockNum=Utility.increment(opblock);
					System.arraycopy(opblock,0,sendingData,0,4);
					System.arraycopy(data1,0,sendingData,4,data1.length);
					sendPacket = new DatagramPacket(sendingData, sendingData.length,
							receivePacket.getAddress(), receivePacket.getPort());



					//send data
					try {
						sendReceiveSocket.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
					System.out.println( "Cleint sent Data ");
					if(verboseMode){
						System.out.println("To host: " + sendPacket.getAddress());
						System.out.println("Destination host port: " + sendPacket.getPort());
						len = sendPacket.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: ");
						System.out.println(new String(sendPacket.getData(),0,len));
					}
					System.out.print("Containing Bytes: ");
					System.out.print("opcode: ");
					System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(),0,2 )));
					System.out.print(" block#:" +blockNum);
					System.out.println("data: "+Arrays.toString(Utility.getBytes(sendPacket.getData(),4,sendPacket.getLength())));

					//get acknowledgement
					try {
						sendReceiveSocket.receive(receivePacketACK);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}

					System.out.println("Acknowledgement received");
					if(verboseMode){
						System.out.println("From host: " + receivePacketACK.getAddress());
						System.out.println("Host port: " + receivePacketACK.getPort());
						len = receivePacketACK.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: " );

						// Form a String from the byte array.
						String received = new String(data1,0,len);   
						System.out.println(received + "\n");
					}
					System.out.print("Containing Bytes: ");
					System.arraycopy(receivePacketACK.getData(),0,opblock,0,4);
					System.out.print("opcode: ");
					System.out.println(Arrays.toString(Utility.getBytes(receivePacketACK.getData(),0,2)));
					System.out.println("block#: " + Utility.getByteInt(opblock));





				}

			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}











		}//end elseif 


	}//endof the method
	//this method increments the block number and prints it in a way that escapes the 2's comp
	//modification in java

}   


