package Clientpackage;


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

import intermediateHost.ErrorSimulator;
import utilities.Constants;
import utilities.Utility;

public class Client {

	DatagramPacket sendPacket, receivePacket;

	DatagramSocket sendReceiveSocket;

	public static final int TIMEOUT = 6000;

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

	private byte[] request;

	private InetAddress localHost;

	private boolean flag;

	private int numbers;

	private int i;

	private byte[] data;

	private byte[] ACK=new byte[4];

	private BufferedOutputStream out;
	private int serverPort;

	private int previousOpcode=0;

	private int k;

	private ArrayList<Integer> previousOpcodes;

	public Client()

	{
		errorType = new byte[2];
		previousACK = new byte[4];
		previousDATA = new byte[516];
		previousACKs =new ArrayList<Integer>();
		previousOpcodes= new ArrayList<Integer>();
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
			String vqMode, String tnMode,String IP)

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

		 request = new byte[2 + fileNameBinary.length + 1 + modeBinary.length + 1];

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
			if(IP.equals("0")){
				try {
					localHost=InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					localHost=InetAddress.getByName(IP);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				sendReceiveSocket.setSoTimeout(6000);
			} catch (SocketException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
		sendRequest();

		// now after sending the request we need to get back either data or
		// acknowledgement

		// depending on the type of request we sent.

		 ACK[0]=0;ACK[1]=4;ACK[2]=0;ACK[3]=1;

		 sendingData = new byte[516];

		sendingData[0] = 0;

		sendingData[1] = 3;

		sendingData[2] = 0;

		sendingData[3] = 0;

		 data1 = new byte[512];

		 ACK1 = new byte[100];

		 opblock = new byte[4];
		opblock1=new byte[4];
		

		// in case of read request we send the acknowledgement and receive the
		// data

		// from the designated file

		if (request[1] == READ) {

			data = new byte[516];

			receivePacket = new DatagramPacket(data, data.length);

			// this flag to see if the data coming is of 512 bytes or less

			 flag = true;

			 out = null;

			try {
				out = new BufferedOutputStream(new FileOutputStream(readFilePath));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		
			// ************************************************************************************************************************************************************************************************************************
			 i=0;
			 numbers=0;
			
			while (flag) {
				
				System.arraycopy(receivePacket.getData(),0,previousDATA,0,receivePacket.getData().length);
				int checking=receiveData();
				
				if(checking==-1){
					return;
				}
				
				// creating a send packet for acknowledgement

				

				sendACK();
				
				}
			
				System.out.println("we are done with the transfering, you can enter a new command!");
			

			try {

				out.close();

			} catch (IOException e) {

				
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
			int i=receiveAck();
			int times=0;
			while(i==0){
				times++;
			sendRequest();
			i=receiveAck();
			if(times==2){
				System.out.println("hmm its seems like the server is not responding anyMore,the client is going to stop/n"
						+ "if you want to create a new transfer just type again the request you want to send");
				return;
			}
			}
			if(i==-1){
				return;
			}
			
				serverPort=receivePacketACK.getPort();
				
					
				
				if(previousACKs.size()<=20){
					previousACKs.add(Utility.getByteInt(receivePacketACK.getData()));
				}else{
					previousACKs.remove(0);
					previousACKs.add(Utility.getByteInt(receivePacketACK.getData()));
				}
				
			
			
			
			 k=0;
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
					sendData(1);

					
					// get acknowledgement
					int check=receiveAck();
					if (check==-1){
						return;
					}
					 	 times=0;
						while(check==0){
							times++;
							sendData(check);
							check=receiveAck();
							if(times==3){
								System.out.println("hmm its seems like the server is not responding anyMore,the client is going to stop/n"
										+ "if you want to create a new transfer just type again the request you want to send");
							return;
							}
						}
						data1 = new byte[512];
						if(previousACKs.contains(Utility.getByteInt(receivePacketACK.getData()))){
							System.out.println("an Ack that we have already received has been received again, we must ignore it");
							receiveAck();
							
							
							
						}else {
							
						
						if(previousACKs.size()<=20){
							previousACKs.add(Utility.getByteInt(receivePacketACK.getData()));
						}else{
							previousACKs.remove(0);
							previousACKs.add(Utility.getByteInt(receivePacketACK.getData()));
						}
						}
					
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
					sendData(1);
				// 23333333333333333333333333333333333333333333333333333333333333333333
				int c=receiveAck();
				if(c==-1){
					return;
				}
				while(c==0){
					sendData(c);
					c=receiveAck();
				}
				System.out.print("we are done from the transferring you can enter a new command!");
			}
		}

	}
	/*
	 * 
	 * 
	 * METHODS AREA
	 * 
	 * 
	 * 
	 */

	
	private void sendRequest() {
		

			sendPacket = new DatagramPacket(request, request.length,

					localHost, portNum);

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
		
	}

	public boolean checkError(byte[] error,int port) {

		if (error[0] == 0 & error[1] == 5 && port==serverPort) {

			errorType[0] = error[2];

			errorType[1] = error[3];

			return true;

		}
		
		return false;

	}

	// print error

	public void printError(byte[] error,int length) {

		byte[] temp = new byte[length];

		for (int i = 4; i <length; i++) {

			temp[i-4] = error[i];

		}

		String s = new String(temp);

		System.out.println(s);
		System.out.println("This is an error of type: "+error[3]);
		System.out.println("you can enter a new command");

	}
	private void sendData(int check){
		
		
		
		
		sendPacket = new DatagramPacket(sendingData, sendingData.length, receivePacketACK.getAddress(),
				receivePacketACK.getPort());
		// sending data to the server
		
		
		//System.arraycopy(data1, 0, sendingData, 4, sendingData.length - 4);
		System.out.println("Client sent Data ");

		if (verboseMode) {
			System.out.println("To host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			len = sendPacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(new String(sendPacket.getData(), 0, len));
		}
		System.arraycopy(sendingData, 0, opblock, 0, 4);
		
		if(check==1){												// the data
			blockNum = Utility.increment(opblock); // incrementing the
			}else{
				blockNum = Utility.getByteInt(opblock);
			}												// block number
		System.arraycopy(opblock, 0,sendingData, 0, 4);
		
		System.out.print("Containing Bytes: ");
		System.out.print("opcode: ");
		System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(), 0, 2)));
		
		System.out.println("block#:" + (blockNum));
		System.out.println("data: "
				+ Arrays.toString(Utility.getBytes(sendPacket.getData(), 4, sendPacket.getLength())));
		System.arraycopy(sendingData, 0, opblock, 0, 4);// adding
		// the block
		// number to
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		}
	public int receiveAck(){
		try {
			sendReceiveSocket.receive(receivePacketACK);
			
		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				
				System.out.println("\n\n CLIENT TIMED OUT.RETRANSMIT DATA\n\n");
				return 0;
			}
		}
		if(i==0){
			serverPort=receivePacketACK.getPort();
		}
		i++;	
		k++;
		if (checkError(receivePacketACK.getData(),receivePacketACK.getPort())) {
			if(receivePacketACK.getPort()!=serverPort){
				
					System.out.println("an error packet form an unknow TID got received, we are going to ignore it");
					return receiveAck();
			}
			System.out.println();
			printError(receivePacketACK.getData(),receivePacketACK.getLength()); // yes there is
													// an error.
			return -1;
		}
		if(receivePacketACK.getLength()>4){
			System.out.println("the received ack is of wrong size! the client is goint to end");
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
		
		if(receivePacketACK.getPort()!=serverPort && k>1){
			System.out.println("the client received data packet from an unknown TID, we are going to ignor it");
			return receiveAck();
		}
		if(receivePacketACK.getData()[1]!=4){
			
			System.out.println("a packet with an wrong opcode for ACK got received,the server is going to stop the file transfer\n"
					+ "you can enter a new command now :)\n");
			return -1;
			
		}
			if((Utility.getByteInt(receivePacketACK.getData())-previousOpcode)>8){
				
				System.out.println("a packet with an invalid ack block number got received.Client is going to stop the file transfering\n"
						+ "you can enter a new commad :)\n");
				return -1;
				
			}
			previousOpcode=Utility.getByteInt(receivePacketACK.getData());
		
		return 1;
	}
	public int receiveData(){
		try {

			// Block until a datagram is received via sendReceiveSocket.
			sendReceiveSocket.receive(receivePacket);
			
		} catch (IOException e) {
			if(e instanceof SocketTimeoutException && i==0){
				System.out.println("the client timed out while waiting for a respond! it is going to retransmit the request");
				sendRequest();
				numbers++;
				if(numbers==2){
					System.out.println("it seems like we are not responding from the server anymore!! we are going to quit");
					return -1;
				}
				return receiveData();
				
			}
			else{
				
				System.out.println("the client is not receiving anything from the server anymore it is going to stop the file transfer!");
				return -1;
			}
		}
		if(receivePacket.getLength()>516){
			System.out.println("the received data is of wrong size! the client is goint to end");
			return -1;
			
		}
		if(i==0){
			serverPort=receivePacket.getPort();
		}
		i++;
		System.out.println();
		// check if there is an error
		if (checkError(receivePacket.getData(),receivePacket.getPort())) {
			if(receivePacket.getPort()!=serverPort){
				
					System.out.println("an error packet form an unknow TID got received, we are going to ignore it");
				return receiveData();
			}
			System.out.println();
			printError(receivePacket.getData(),receivePacket.getLength()); // yes there is an
			return -1;
		}

		// Initialize previous data to te new data.
	

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
		previousOpcodes.add(Utility.getByteInt(opblock1));
		System.out.print("Containing Bytes: ");

		System.out.println("opcode: " + Arrays.toString(Utility.getBytes(receivePacket.getData(), 0, 2)));

		System.out.println("block #: " + Utility.getByteInt(opblock));

		System.out.println("data: "
				+ Arrays.toString(Utility.getBytes(receivePacket.getData(), 4, receivePacket.getLength())));
	
		
		if(receivePacket.getPort()!=serverPort){
			System.out.println("the client received data packet from an unknown TID, we are going to ignor it");
			return receiveData();
		}
		if(receivePacket.getData()[1]!=3){
			
			System.out.println("a packet with an wrong opcode for Data got received,the server is going to stop the file transfer\n"
					+ "you can enter a new command now :)\n");
			return -1;
			
		}
			if((Utility.getByteInt(receivePacket.getData())-previousOpcode)>8){
				
				System.out.println("a packet with an invalid data block number got received.Client is going to stop the file transfering\n"
						+ "you can enter a new commad :)\n");
				return -1;
				
			}
			previousOpcode=Utility.getByteInt(receivePacket.getData());
			
	ACK[2] = receivePacket.getData()[2];

	ACK[3] = receivePacket.getData()[3];

	// Here we will start writing to the file.
	if(!previousOpcodes.contains(Utility.getByteInt(opblock)) && !Utility.containsAzero(receivePacket.getData(), 4,receivePacket.getLength())){
		
		
	try {

		out.write(receivePacket.getData(), 4, receivePacket.getLength() - 4);

	} catch (IOException e1) {

		System.out.println(
				"no enough memory in the directory of the output file, the file transfer will be stopped and you can write a new command");
		return -1;

	}
	}else{
		
		System.out.println("a data packet that already got received was received again, the client will ignore it");
		
		}
	
	return 0;
	}
	public void sendACK(){
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
		}
			System.out.println("opcode: " + Arrays.toString(Utility.getBytes(sendPacketACK.getData(), 0, 2)));

			System.out.println("block #: " + Utility.getByteInt(opblock));
	}
}
