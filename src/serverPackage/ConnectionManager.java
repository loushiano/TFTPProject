package serverPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.AccessDeniedException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

import utilities.Constants;
import utilities.Utility;

/*
 * This thread is created for every client-connection. It takes care of the server-client transaction.
 */
public class ConnectionManager extends Thread {
	private String filepath;
	private DatagramPacket receivePacket;
	private DatagramSocket sendReceiveSocket, receiveSocket;
	private byte[] data;
	public static final byte READ = 1, WRITE = 2;
	private boolean isReadRequest, isWriteRequest;
	String filePath;
	boolean flag = true;
	byte[] opblock = new byte[4];
	public String mode;
	public int len;
	public String received;
	private boolean flag3 = false;
	private final String FILEPATH = "";
	private DatagramPacket errorPacket;
	private static final int TIMEOUT = 5000;
	byte[] previousACK, previousDATA;
	private int notreceived=0;
	private DatagramPacket receivePacketACK;
	private DatagramPacket sendPacketDATA;
	private byte[] ACK,responseData,data1; 
	private ArrayList<Integer> previousACKs;
	private byte[] opblock1;
	private DatagramPacket receivePacketDATA;
	private DatagramPacket sendPacketACK;
	private byte[] ACK1={ 0, 4, 0, 0 };
	private byte[] DATA;
	private int PortClient;
	private int previousOpcode;
	private BufferedOutputStream  out;
	public ConnectionManager(DatagramPacket receivedPacket, byte[] data, String filepath, String mode) {
		this.receivePacket = receivedPacket;
		previousACKs=new  ArrayList<Integer>();
		this.filepath = FILEPATH + filepath;
		this.mode = mode;
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.data = data;

		previousACK = new byte[4];
		previousDATA = new byte[516];
		opblock1=new byte[4];
		PortClient=receivedPacket.getPort();
	}

	public void run() {
		// Process the received datagram.

		System.out.println("Server: request received:");
		// checking the mode if its verbose or Normal
		if (mode.equals(Constants.VERBOSE)) {

			System.out.println("From host: " + receivePacket.getAddress());
			System.out.println("Host port: " + receivePacket.getPort());
			int len = receivePacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");

			// Form a String from the byte array.
			String received = new String(data, 0, len);
			System.out.println(received + "\n");
			System.out.print("Containing Bytes: ");
			System.out.println(Arrays.toString(Utility.getBytes(receivePacket.getData(), 0, len)));
		}

		// Validate Request

		if (isValidRequest(data)==1) {
			// checking if it is read request
			if (data[1] == READ) {
				isReadRequest = true;
				isWriteRequest = false;

				// checking if it is write request
			} else if (data[1] == WRITE) {
				isReadRequest = false;
				isWriteRequest = true;
			}
		}else{
			byte error[] = new byte[100];
		 if (isValidRequest(data)==-1){ // Throw an exception if the packet is an invalid request
			
			putError(error, 4, "the received request is of wrong format",PortClient);
		} else if (isValidRequest(data)==-2){ // Throw an exception if the packet is an invalid request
			
			putError(error, 4, "the received request is neither a read nor a write request",PortClient);
		} else if (isValidRequest(data)==-3){ // Throw an exception if the packet is an invalid request
			
			putError(error, 4, "the mode of the request received is invalid",PortClient);
		}else{
			
			putError(error, 4, "the filename of the request received is invalid",PortClient);
			
		}
		 sendReceiveSocket.close();
		 return;
		}
		

		// Create a response byte array
		// 0301 for a read request
		// 0401 for a write request
		 responseData = null;

		if (isReadRequest) {

			try {
				sendReceiveSocket.setSoTimeout(TIMEOUT);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// creating the response date which contains 512 data and 4 bytes of
			// the op block
			responseData = new byte[516];
			responseData[0] = 0;
			responseData[1] = 3;
			responseData[2] = 0;
			responseData[3] = 0;
			BufferedInputStream in = null;
		
			try {
				
				in = new BufferedInputStream(new FileInputStream(filepath));
			}catch (FileNotFoundException e) {
				byte error[] = new byte[100];
				String s=e.getMessage();
				System.out.print(s+"\n");
				if(s.contains("Access is")){
					putError(error, 3, "the file you are trying to read from can not be accessed",PortClient);
				}else{
					// if there is an error of code 1, we form an error packet
					// and we send it to the client
					error = new byte[100];
					putError(error, 1, "the file you are trying to read from does not exist",PortClient);
				}
				sendReceiveSocket.close();
				return;
			}
				
					
			

			// creating an array of data to send the data

			 data1 = new byte[512];
			 ACK = new byte[4];

			int n;

			/* Read the file in 512 byte chunks. */
			try {
				while ((n = in.read(data1)) != -1) {
					/*
					 * We just read "n" bytes into array data. Now write them to
					 * the output file.
					 */
					if (!Utility.containsAzero(data1, 0, 512)) {
						// if we enter this condition it means that data1
						// doesn't contain a zero
						flag3 = false;
						System.arraycopy(data1, 0, responseData, 4, data1.length);
					} else {
						// if data contains zero it means it is the last set of
						// bytes we need to transefer so we have to create a
						// packet
						// that is less than 516 bytes
						flag3 = true;
						int j = Utility.getFirstZero(data1);
						byte copyArray[] = responseData;
						responseData = new byte[j + 4];
						System.arraycopy(copyArray, 0, responseData, 0, 4);
						System.arraycopy(data1, 0, responseData, 4, j);

					}
					//here we should send Data.
					sendData(0);

					data1 = new byte[512];
					// creating a packet to receive the acknowlegment
					ACK =new byte[4];
					 notreceived=receiveAck();
					 int i=0;
					while(notreceived==1){
						i++;
						
						sendData(notreceived);
						
						notreceived=receiveAck();
						if(i==2){
							System.out.print("it seems like we are not receiving anything from the client, we are going to stop the file transfer");
							return;
						}
						
					}
					if(notreceived==-1){
						return;
						}
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
				if (!flag3) {
					/*
					 * We just read "n" bytes into array data. Now write them to
					 * the output file.
					 */
					System.arraycopy(data1, 0, responseData, 4, data1.length);
					int check1=0;
					//send Data
					sendData(0);
					// get acknowledge
					int k=0;
					check1=receiveAck();
					if(check1==1){
						k++;
						sendData(check1);
						check1=receiveAck();
						if(k==3){
							System.out.print("exiting");
							return;
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
				
			// checking if its write
			// request***************************************************************************************************************
		} else if (isWriteRequest) {

			out = null;
			// create a file with the file given by the client and catch the
			// error if the file already exists
			File f = new File(filepath);

			try {

				if (!f.createNewFile()) {
					byte error[] = new byte[100];
					putError(error, 6, "the file you are trying to write to already exists",PortClient);
					return;

				}

			} catch (IOException e) {
				// if there is an error of code 2, we form an error packet and
				// we send it to the client

				byte error[] = new byte[100];
				putError(error, 2, "the file you are trying to write to can not be accessed",PortClient);

				return;
			}

			try {
				out = new BufferedOutputStream(new FileOutputStream(filepath));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// creating a an array data to recive the data from the client
			 DATA = new byte[516];
			// creating an array to send ack
			 

				sendACK();
			// this while will keep looping until we get data less than 512 byte
			// from the client
			try {
				sendReceiveSocket.setSoTimeout(8000);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (flag) {
				System.arraycopy(receivePacketDATA.getData(),0,previousDATA,0,receivePacketDATA.getData().length);
				int l=	receiveData();
				if(l==-1){
						return;
						}
					
									
				// get block number from the received block
				ACK1[2] = receivePacketDATA.getData()[2];
				ACK1[3] = receivePacketDATA.getData()[3];
				sendACK();

			}

			try {
				out.close();
			} catch (IOException e) {

				System.out.println("while closing the file that we are writing on the memory limit was exeeded");

				return;
			}

		}
		System.out.println("done with transeferring");
	}

	
	
	
	
	/*
	 * FUNCTIONS USED FOR FILE TRANSFER
	 * 	
	 */
	
	// this method takes a byte array, an error code and a string and it
	// combines them all together
	private void putError(byte[] error, int i, String string,int port) {
		byte msg[] = string.getBytes();
		error=new byte[string.length()+4];
		error[0] = 0;
		error[1] = 5;
		error[2] = 0;
		error[3] = (byte) i;
		System.arraycopy(msg, 0, error, 4, msg.length);
		errorPacket = new DatagramPacket(error, error.length, receivePacket.getAddress(), port);
		System.out.println(string);
		try {
			sendReceiveSocket.send(errorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("error packet sent");

	}

	/**
	 * Validate the request
	 * 
	 * @param data
	 *            the request data to validate
	 * @return false if request is not valid, true if request is valid
	 */
	// checks if the request is valid or not
	public int isValidRequest(byte[] data) {
		boolean isValid = true;

		if (data[0] != 0)
			return -1; // First element should always be zero

		// 2nd element can either be 1 or 2
		if (data[1] == 1 || data[1] == 2)
			isValid = true;
		else {
			return -2;
		}

		int count = 0;

		int fileNameEndingIndex = 0;

		boolean zeroSwitch = false; // true when iterator hits a zero

		for (int i = 2; i < receivePacket.getLength(); i++) {

			if (data[i] != 0)
				count++;

			else if (data[i] == 0) {
				fileNameEndingIndex = i - 1; // index of the last byte of the
												// file name in the data array
				zeroSwitch = true; // zero found, turn the switch on
				break;
			}
		}

		// The count will be zero if there is no file name in the data
		if (count == 0 || count > receivePacket.getLength() - 3)
			return -4;

		if (!zeroSwitch)
			return -1;

		int modeEndingIndex = 0;
		count = 0;
		zeroSwitch = false;
		for (int j = fileNameEndingIndex + 2; j < receivePacket.getLength(); j++) {
			if (data[j] != 0)
				count++;

			else if (data[j] == 0) {
				modeEndingIndex = j - 1; // index of the last character of the
											// "mode" in the data array
				zeroSwitch = true;
				break;
			}

		}

		if (count == 0 || count == receivePacket.getLength())
			return -1;
		if (!zeroSwitch)
			return -1;
		else {
			if (receivePacket.getLength() - modeEndingIndex != 2)
				return -1;
		}
		String mode=new String(data,fileNameEndingIndex+2,5);
			if(mode.equals("octet")){
				return 1;
			}
			return -3;
		
	}
	
	public void sendData(int i){
		sendPacketDATA = new DatagramPacket(responseData, responseData.length,
				receivePacket.getAddress(), receivePacket.getPort());
		
		// printing the info of the sending data
		System.out.println("Server: Sending Data:");
		if (mode.equals(Constants.VERBOSE)) {
			System.out.println("To host: " + sendPacketDATA.getAddress());
			System.out.println("Destination host port: " + sendPacketDATA.getPort());
			len = sendPacketDATA.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");
			System.out.println(new String(sendPacketDATA.getData(), 0, len));
			System.out.print("Containing Bytes: ");
			System.out.println(
					"opcode: " + Arrays.toString(Utility.getBytes(sendPacketDATA.getData(), 0, 2)));

			System.arraycopy(responseData, 0, opblock, 0, 4);
			int op;
			if(i==0){
			op=Utility.increment(opblock);
			}else{
				op=Utility.getByteInt(opblock);
			}
			System.out.println("block#: " + op );
			System.out.println(
					"data: " + Arrays.toString(Utility.getBytes(sendPacketDATA.getData(), 4, len)));
		}
		// send data
				System.arraycopy(opblock, 0, responseData, 0, 4);

				// sending the data packet to the client
				try {
					sendReceiveSocket.send(sendPacketDATA);
				} catch (Exception e) {
					return;
					
				}

				System.out.println("data has been sent");
	}
	public int receiveAck(){
		 receivePacketACK = new DatagramPacket(ACK, ACK.length);
		 
		
		// get acknowledge

		try {
			sendReceiveSocket.receive(receivePacketACK);
			
		} catch (Exception e1) {
			if (e1 instanceof SocketTimeoutException) {
				System.out.println("the server timedout while waiting for receiving an ack from the client, it will retransmit data");
				return 1;
			}
		}
		
		
		
		
		System.out.println("Server: Acknowledgement received:");

		if (mode.equals(Constants.VERBOSE)) {
			System.out.println("From host: " + receivePacketACK.getAddress());
			System.out.println("Host port: " + receivePacketACK.getPort());
			len = receivePacketACK.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: ");

			// Form a String from the byte array.
			received = new String(data, 0, len);
			System.out.println(received + "\n");
			System.out.print("Containing Bytes: ");
			System.out.println(
					"opcode: " + Arrays.toString(Utility.getBytes(receivePacketACK.getData(), 0, 2)));

			System.out.println("block#: " + Utility.getByteInt(receivePacketACK.getData()));
		}
		if(receivePacketACK.getPort()!=PortClient){
			byte error[] = new byte[200];
			putError(error,5,"Server:a packet with an unkown TID has been received we are going to send an error packet to where we received it from",receivePacketACK.getPort());
			return receiveAck();
		}
		if(receivePacketACK.getLength()!=4){
			byte error[] = new byte[200];
			putError(error,4,"a packet with a wrong length of ack got received!",PortClient);
			return -1; 
			
		}
		if(receivePacketACK.getData()[1]!=4){
			byte error[] = new byte[200];
			putError(error,4,"a packet with an wrong opcode for acknowledgement got received,the server is going to stop the file transfer",PortClient);
			return -1;
			
		}
			if((Utility.getByteInt(receivePacketACK.getData())-previousOpcode)>8){
				byte error[] = new byte[200];
				putError(error,4,"a packet with an invalid ack block number got received",PortClient);
				return -1;
				
			}
			previousOpcode=Utility.getByteInt(receivePacketACK.getData());
		return 0;
		}
	public int receiveData(){
		System.out.println("now waiting for new data");
		// get data
		try {
			sendReceiveSocket.receive(receivePacketDATA);
			
		} catch (IOException e) {
			System.out.println("the server seems not to receive anything from the client, its going to stop the file transfer");
			return -1;
		}
		if(receivePacketDATA.getData()[1]==2){
			System.out.println("server has received a request again");
			sendACK();
			return receiveData();
		}
		

		if (Utility.containsAzero(receivePacketDATA.getData(), 4, 516)) {
			flag = false;
		}
		if (receivePacketDATA.getLength() < 516) {
			flag = false;
		}
		System.out.println("Server: Data received:");
		if (mode.equals(Constants.VERBOSE))
			
		System.out.println("From host: " + receivePacketDATA.getAddress());
		System.out.println("Host port: " + receivePacketDATA.getPort());
		len = receivePacketDATA.getLength();
		System.out.println("Length: " + len);
		System.out.print("Containing: ");

		// Form a String from the byte array.
		String received1 = new String(receivePacketDATA.getData(), 0, len);
		System.out.println(received1 + "\n");
		System.out.print("Containing Bytes: ");
		System.out.println("opcode: " + Arrays.toString(Utility.getBytes(receivePacketDATA.getData(), 0, 2)));
		System.arraycopy(receivePacketDATA.getData(), 0, opblock, 0, 4);
		System.out.println("block#: " + Utility.getByteInt(opblock));
		System.out.println("data: " + Arrays.toString(Utility.getBytes(receivePacketDATA.getData(), 4, len)));
		System.arraycopy(receivePacketDATA.getData(), 0, opblock, 0, 4);
		System.arraycopy(previousDATA, 0, opblock1, 0, 4);
		if(receivePacketDATA.getData()[1]!=3){
			byte error[] = new byte[200];
			putError(error,4,"a packet with an wrong opcode for data got received,the server is going to stop the file transfer",PortClient);
			return -1;
		}
		if(receivePacketDATA.getPort()!=PortClient){
			byte error[] = new byte[200];
			putError(error,5,"a packet with an unknown TID got received!",receivePacketDATA.getPort());
			return receiveData();
			
		}
		if(receivePacketDATA.getLength()>516){
			byte error[] = new byte[200];
			putError(error,4,"a packet with a wrong length of data got received!",receivePacketDATA.getPort());
			return -1; 
			
		}
		if((Utility.getByteInt(receivePacketDATA.getData())-previousOpcode)>8){
			byte error[] = new byte[100];
			putError(error,4,"a packet with an invalid blockNumber got received in the server side!",PortClient);
			return -1;
		}
		
		previousOpcode=Utility.getByteInt(receivePacketDATA.getData());
		
		if(Utility.getByteInt(opblock)!=Utility.getByteInt(opblock1) && !Utility.containsAzero(receivePacketDATA.getData(), 4, 516)){
			//System.out.println(Utility.getByteInt(opblock)+" "+Utility.getByteInt(opblock1));
		try {
			out.write(receivePacketDATA.getData(), 4, receivePacketDATA.getLength() - 4);
		} catch (IOException e1) {
			byte error[] = new byte[100];
			putError(error, 3, "no enough memory",PortClient);

			return -1;
		}
		}else{
			if(!Utility.containsAzero(receivePacketDATA.getData(), 4, 516)){
			System.out.println("A data packet that we received beofre,has been sent to us again:IGNORED!!");
			}
		}
			return 0;
	}
	private void sendACK() {
		// creating a packet for the ack
					sendPacketACK = new DatagramPacket(ACK1, ACK1.length, receivePacket.getAddress(),
							receivePacket.getPort());
					// creating packet to receive data
					 receivePacketDATA = new DatagramPacket(DATA, DATA.length);
					
					// sending the ack packet
					try {
						sendReceiveSocket.send(sendPacketACK);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}

					// printing the info for the ack
					System.out.println("Server: Sent Acknowledgement:");
					if (mode.equals(Constants.VERBOSE)) {
						System.out.println("To host: " + receivePacket.getAddress());
						System.out.println("Destination host port: " + receivePacket.getPort());
						len = sendPacketACK.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: ");
						System.out.println(new String(sendPacketACK.getData(), 0, len));
						System.out.print("Containing Bytes: ");
						System.out.println("opcode: " + Arrays.toString(Utility.getBytes(sendPacketACK.getData(), 0, 2)));
						System.out.println("block#:"+Utility.getByteInt(sendPacketACK.getData()));
					}
					System.out.println("ACKNOWLEDGEMENT SENT");
					

		
	}

	
}
	

