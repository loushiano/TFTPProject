package Clientpackage;

/*Assignment 1

 * Name: Sahaj Arora

 * Student No. 100961220

 */

// Client.java

// This class is the client side for Assignment 1. The client sends read/write/invalid requests to the IntermediateHost

// 11 times.

// Last edited 16th September, 2016
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import utilities.Constants;
import utilities.Utility;

public class Client {

	DatagramPacket sendPacket, receivePacket;

	DatagramSocket sendReceiveSocket;

	private static final int TIMEOUT = 5000;

	private static final byte READ = 1, WRITE = 2, INVALID = 5;

	public int portNum;

	public boolean verboseMode;

	private boolean flag3;
	DatagramPacket receivePacketACK;

	byte[] errorType;
	byte[] previousACK, previousDATA;

	private byte[] opblock1;
	private byte[] ACK1;
	private byte[] sendingData;
	private int len, blockNum;
	private byte[] data1,opblock;

	private ArrayList<Integer> previousACKs;

	public Client()

	{
		errorType = new byte[2];
		previousACK = new byte[4];
		previousDATA = new byte[516];
		previousACKs =new ArrayList<Integer>();

		try {

			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.

			sendReceiveSocket = new DatagramSocket();

			// set the time out for the socket.

		} catch (SocketException se) { // Can't create the socket.

			se.printStackTrace();

			System.exit(1);

		}

	}

	/**
	 * 
	 * Send and receive request
	 * 
	 * @param type
	 *            Type of request - Read/Write/Invalid
	 * 
	 * @param iteration
	 *            Current iteration Number.
	 * 
	 */

	public void sendAndReceive(String reqType, String filepath, String filewritepath, String readFilePath,
			String vqMode, String tnMode)

	{

		// Prepare a DatagramPacket and send it via sendReceiveSocket

		// to port 23 on the destination host.

		String fileName = filepath;

		if (reqType.equals(Constants.WRITE_REQUEST)) {

			fileName = filewritepath;

		} else {

		}

		// converting the file name to binary

		byte[] fileNameBinary = fileName.getBytes();

		// converting the mode to binary

		String mode = Constants.MODE;

		byte[] modeBinary = mode.getBytes();

		// creating the request array

		byte[] request = new byte[2 + fileNameBinary.length + 1 + modeBinary.length + 1];

		request[0] = 0;

		// check if the client wants a read or a write request

		if (reqType.equals(Constants.READ_REQUEST)) {

			request[1] = 1;

		} else {

			request[1] = 2;

		}

		// check if the client wants the sending to be in test or normal mode

		if (tnMode.equals(Constants.TEST)) {

			portNum = 23;

		} else {

			portNum = 69;

		}
		portNum=23;
		// check if the client wants the data to be transferred in verbose or
		// quiet mode

		if (vqMode.equals(Constants.VERBOSE)) {

			verboseMode = true;

		} else {

			verboseMode = false;

		}

		int j = 0;

		// Store bytes of fileName in the request array

		for (int i = 2; i < (fileNameBinary.length + 2); i++) {

			request[i] = fileNameBinary[j];

			j++;

		}

		int lengthTillFirstZero = 2 + fileNameBinary.length;

		// adding a zero in the middle of the array

		request[lengthTillFirstZero] = 0;

		int tempCounter = 0;

		// Store bytes of mode (netascii) in the request array

		for (int x = lengthTillFirstZero + 1;

		x < lengthTillFirstZero + modeBinary.length + 1; x++) {

			request[x] = modeBinary[tempCounter];

			tempCounter++;

		}

		// Ending 0 byte

		request[lengthTillFirstZero + modeBinary.length + 1] = 0;

		// creating the send packet

		try {

			sendPacket = new DatagramPacket(request, request.length,

					InetAddress.getLocalHost(), portNum);// we put 69 because we
															// do not have host
															// now

		} catch (UnknownHostException e) {

			e.printStackTrace();

			System.exit(1);

		}

		// printing the info if its verbose mode

		if (verboseMode) {

			System.out.println("Client: Sending request:");

			System.out.println("To host: " + sendPacket.getAddress());

			System.out.println("Destination host port: " + sendPacket.getPort());

			int len = sendPacket.getLength();

			System.out.println("Length: " + len);

			System.out.print("Containing: ");

			System.out.println(new String(sendPacket.getData(), 0, len)); // or
																			// could
																			// print
																			// "s"

			System.out.print("Containing Bytes: ");

			System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(), 0, len)));

		}

		// Send the datagram packet to the server via the send/receive socket.

		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Client: request sent.\n");

		// now after sending the request we need to get back either data or
		// acknowledgement

		// depending on the type of request we sent.

		byte[] ACK = { 0, 4, 0, 1 };

		 sendingData = new byte[516];

		sendingData[0] = 0;

		sendingData[1] = 3;

		sendingData[2] = 0;

		sendingData[3] = 1;

		 data1 = new byte[512];

		 ACK1 = new byte[100];

		 opblock = new byte[4];
		opblock1=new byte[4];
		

		// in case of read request we send the acknowledgement and receive the
		// data

		// from the designated file

		if (request[1] == READ) {

			byte data[] = new byte[516];

			receivePacket = new DatagramPacket(data, data.length);

			// this flag to see if the data coming is of 512 bytes or less

			boolean flag = true;

			BufferedOutputStream out = null;

			try {
				out = new BufferedOutputStream(new FileOutputStream(readFilePath));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// ************************************************************************************************************************************************************************************************************************
			while (flag) {
				System.arraycopy(receivePacket.getData(),0,previousDATA,0,receivePacket.getData().length);
				try {

					// Block until a datagram is received via sendReceiveSocket.
					sendReceiveSocket.receive(receivePacket);
					
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}

				System.out.println();
				// check if there is an error
				if (checkError(receivePacket.getData())) {
					System.out.println();
					printError(receivePacket.getData()); // yes there is an
					return;
				}

				// Initialize previous data to te new data.
				

				/*
				 * 
				 * if()
				 * 
				 * File f = new File(readFilePath);
				 * 
				 * if(f.exists()){
				 * 
				 * throw new Exception("file already exists");
				 * 
				 * }catch(Exception e){
				 * 
				 * byte error = new byte[100];
				 * 
				 * }
				 * 
				 */

				// if what we are receiving is less than 516, this means that we
				// do not have to accept anything anymore

				if (receivePacket.getLength() < 516) {

					flag = false;

				}

				if (Utility.containsAzero(receivePacket.getData(), 4, 516)) {

					flag = false;

				}

				// Process the received datagram.

				System.out.println("Data from server received:");

				if (verboseMode) {

					System.out.println("From host: " + receivePacket.getAddress());

					System.out.println("Host port: " + receivePacket.getPort());

					System.out.println("Length: " + receivePacket.getLength());

					System.out.print("Containing: ");

					// Form a String from the byte array.

					String received = new String(data, 0, receivePacket.getLength());

					System.out.println(received);

				}

				System.arraycopy(receivePacket.getData(), 0, opblock, 0, 4);
				System.arraycopy(previousDATA, 0, opblock1, 0, 4);

				System.out.print("Containing Bytes: ");

				System.out.println("opcode: " + Arrays.toString(Utility.getBytes(receivePacket.getData(), 0, 2)));

				System.out.println("block #: " + Utility.getByteInt(opblock));

				System.out.println("data: "
						+ Arrays.toString(Utility.getBytes(receivePacket.getData(), 4, receivePacket.getLength())));

				ACK[2] = receivePacket.getData()[2];

				ACK[3] = receivePacket.getData()[3];

				// Here we will start writing to the file.
				if(Utility.getByteInt(opblock)!=Utility.getByteInt(opblock1)){
					System.out.println(" "+Utility.getByteInt(opblock)+ " "+Utility.getByteInt(opblock1));
					
				try {

					out.write(receivePacket.getData(), 4, receivePacket.getLength() - 4);

				} catch (IOException e1) {

					System.out.println(
							"no enough memory in the directory of the output file, the file transfer will be stopped and you can write a new command");
					return;

				}
				}else{
					System.out.println("a data packet that already got received was received again, the client will ignore it");
				}
				// creating a send packet for acknowledgement

				DatagramPacket sendPacketACK = new DatagramPacket(ACK, ACK.length, receivePacket.getAddress(),
						receivePacket.getPort());

				// sending the acknowledgement via sendReceive Socket

				try {
					sendReceiveSocket.send(sendPacketACK);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);

				}

				System.out.println("Client sent acknowledgement ");

				if (verboseMode) {

					System.out.println("To Server: " + receivePacket.getAddress());

					System.out.println("Destination Server port: " + receivePacket.getPort());

					len = sendPacketACK.getLength();

					System.out.println("Length: " + len);

					System.out.print("Containing: ");

					System.arraycopy(sendPacketACK.getData(), 0, opblock, 0, 4);

					System.out.println(new String(sendPacketACK.getData(), 0, len));

					System.out.print("Containing Bytes: ");

					System.out.println("opcode: " + Arrays.toString(Utility.getBytes(sendPacketACK.getData(), 0, 2)));

					System.out.println("block #: " + Utility.getByteInt(opblock));

				}
				
				}
			

			try {

				out.close();

			} catch (IOException e) {

				System.out.println("at the end of the writing the memory limist was exeeded");
				return;

			}

			// ************************************************* check if it is
			// write
			// request*************************************************************************************************

		} else if (request[1] == WRITE) {
			try {
				sendReceiveSocket.setSoTimeout(TIMEOUT);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
				System.out.println("Socket Timed Out!");
			}

			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(filepath));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// creating a packet to receive acknowledgment from the server
			receivePacketACK = new DatagramPacket(ACK1, ACK1.length);
			try {
				sendReceiveSocket.receive(receivePacketACK);
			} catch (Exception e) {
				if (e instanceof SocketTimeoutException) {
					try {
						sendReceiveSocket.send(sendPacket);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.exit(1);
					}
				}

			}

			if (checkError(receivePacketACK.getData())) {
				System.out.println();
				printError(receivePacketACK.getData()); // yes there is an
				return;
			}

			System.out.println("Acknowledgement received");
			if (verboseMode) {
				System.out.println("From host: " + receivePacketACK.getAddress());
				System.out.println("Host port: " + receivePacketACK.getPort());
				len = receivePacketACK.getLength();
				System.out.println("Length: " + len);
				System.out.print("Containing: ");

				// Form a String from the byte array.
				String received = new String(data1, 0, len);
				System.out.println(received + "\n");
			}

			System.arraycopy(receivePacketACK.getData(), 0, opblock, 0, 4);
			System.out.print("Containing Bytes: ");
			System.out.print("opcode: ");
			System.out.println(Arrays.toString(Utility.getBytes(receivePacketACK.getData(), 0, 2)));
			System.out.println("block #: 0 ");

			int n;
			/* Read the file in 512 byte chunks. */
			try {
				while ((n = in.read(data1)) != -1) { // check if the file is
														// finished
					/*
					 * We just read "n" bytes into array data. Now write them to
					 * the output file.
					 */
					System.arraycopy(data1, 0, sendingData, 4, data1.length);
					sendData(1);

					
					// get acknowledgement
					int check=receiveData();
					if (check==-1){
						return;
					}
					 
						while(check==0){
							sendData(check);
							check=receiveData();
						}
						data1 = new byte[512];
						if(previousACKs.contains(Utility.getByteInt(receivePacketACK.getData()))){
							System.out.println("an Ack that we have already received has been received again, we must ignore it");
							receiveData();
							
							
						}else {
							
						
						if(previousACKs.size()<=20){
							System.out.println("hi");
							previousACKs.add(Utility.getByteInt(receivePacketACK.getData()));
						}else{
							previousACKs.remove(0);
							previousACKs.add(Utility.getByteInt(receivePacketACK.getData()));
						}
						}
					// 00000000000000000000000000000000000000000000000000000000980909099000000000000090990
				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// if flag3 is set to false it means that the last data transfered
			// are exactly 512 byte
			if (!flag3) {
				System.arraycopy(data1, 0, sendingData, 4, data1.length);
				sendPacket = new DatagramPacket(sendingData, sendingData.length, receivePacketACK.getAddress(),
						receivePacketACK.getPort());
				// sending data to the server
				try {
					sendReceiveSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				System.arraycopy(sendingData, 0, opblock, 0, 4);// adding the
																// block number
																// to the data
				blockNum = Utility.increment(opblock); // incrementing the block
														// number
				System.arraycopy(opblock, 0, sendingData, 0, 4);
				System.arraycopy(data1, 0, sendingData, 4, data1.length);
				System.out.println("Client sent Data ");

				if (verboseMode) {
					System.out.println("To host: " + sendPacket.getAddress());
					System.out.println("Destination host port: " + sendPacket.getPort());
					len = sendPacket.getLength();
					System.out.println("Length: " + len);
					System.out.print("Containing: ");
					System.out.println(new String(sendPacket.getData(), 0, len));
				}
				System.out.print("Containing Bytes: ");
				System.out.print("opcode: ");
				System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(), 0, 2)));
				System.out.println("block#:" + (blockNum - 1));
				System.out.println(

						"data: " + Arrays.toString(Utility.getBytes(sendPacket.getData(), 4, sendPacket.getLength())));

				// 23333333333333333333333333333333333333333333333333333333333333333333
				try {
					sendReceiveSocket.receive(receivePacketACK);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				// printing the info for the received ack
				System.out.println("Acknowledgement received");

				if (verboseMode) {
					System.out.println("From host: " + receivePacketACK.getAddress());
					System.out.println("Host port: " + receivePacketACK.getPort());
					len = receivePacketACK.getLength();
					System.out.println("Length: " + len);
					System.out.print("Containing: ");
					// Form a String from the byte array.
					String received = new String(receivePacketACK.getData(), 0, len);
					System.out.println(received + "\n");
				}
				System.out.print("Containing Bytes: ");
				System.arraycopy(receivePacketACK.getData(), 0, opblock, 0, 4);
				System.out.print("opcode: ");
				System.out.println(Arrays.toString(Utility.getBytes(receivePacketACK.getData(), 0, 2)));
				System.out.println("block#: " + Utility.getByteInt(opblock));
			}
		} // end elseif

	}// endof the method

	// this method increments the block number and prints it in a way that
	// escapes the 2's comp

	// modification in java

	// check error

	public boolean checkError(byte[] error) {

		if (error[0] == 0 & error[1] == 5) {

			errorType[0] = error[2];

			errorType[1] = error[3];

			return true;

		}

		return false;

	}

	// print error

	public void printError(byte[] error) {

		byte[] temp = new byte[error.length - 5];

		for (int i = 4; i < error.length - 6; i++) {

			temp[i] = error[i];

		}

		String s = new String(temp);

		System.out.println(s);
		System.out.println("you can enter a new command");

	}
	private void sendData(int check){
		
		
		// this will check for us if the last data transfer is full,
		// we send a byte of zeros to the server.
		if (!Utility.containsAzero(data1, 0, 512)) {
			// if we enter this condition it means that data1
			// doesn't contain a zero
			flag3 = false;
		} else {
			flag3 = true;
			int k = Utility.getFirstZero(data1);
			byte copyArray[] = sendingData;
			sendingData = new byte[k + 4];
			System.arraycopy(copyArray, 0, sendingData, 0, 4);
			System.arraycopy(data1, 0, sendingData, 4, k);
		}
		sendPacket = new DatagramPacket(sendingData, sendingData.length, receivePacketACK.getAddress(),
				receivePacketACK.getPort());
		// sending data to the server
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.arraycopy(sendingData, 0, opblock, 0, 4);// adding
														// the block
														// number to
		if(check==1){												// the data
		blockNum = Utility.increment(opblock); // incrementing the
		}else {
			blockNum = Utility.getByteInt(opblock); 
		}							// block number
		System.arraycopy(opblock, 0, sendingData, 0, 4);
		System.arraycopy(data1, 0, sendingData, 4, sendingData.length - 4);
		System.out.println("Client sent Data ");

		if (verboseMode) {
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(new String(sendPacket.getData(), 0, len));
		}

		System.out.print("Containing Bytes: ");
		System.out.print("opcode: ");
		System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(), 0, 2)));
		System.out.println("block#:" + (blockNum - 1));
		System.out.println("data: "
				+ Arrays.toString(Utility.getBytes(sendPacket.getData(), 4, sendPacket.getLength())));
		
	}
	public int receiveData(){
		try {
			sendReceiveSocket.receive(receivePacketACK);
			
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				return 0;
			}
		}

		if (checkError(receivePacketACK.getData())) {
			System.out.println();
			printError(receivePacketACK.getData()); // yes there is
													// an error.
			return -1;
		}

		// if there is no error in ACK, we initialize the
		// previousACk to the new ACK
		previousACK = receivePacketACK.getData();

		// printing the info for the received ack
		System.out.println("Acknowledgement received");

		if (verboseMode) {
			System.out.println("From host: " + receivePacketACK.getAddress());
			System.out.println("Host port: " + receivePacketACK.getPort());
			len = receivePacketACK.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");

			// Form a String from the byte array.
			String received = new String(receivePacketACK.getData(), 0, len);
			System.out.println(received + "\n");
		}

		System.out.print("Containing Bytes: ");
		System.arraycopy(receivePacketACK.getData(), 0, opblock, 0, 4);
		System.out.print("opcode: ");
		System.out.println(Arrays.toString(Utility.getBytes(receivePacketACK.getData(), 0, 2)));
		System.out.println("block#: " + Utility.getByteInt(opblock));
		return 1;
	}
	

}
