package serverPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

import utilities.Utility;

public class ConnectionManager extends Thread{
	private String filepath;
	private DatagramPacket receivePacket, sendPacket;
	private DatagramSocket sendReceiveSocket, receiveSocket;
	private byte[] data;
	public static final byte READ = 1, WRITE = 2;
	private boolean isReadRequest, isWriteRequest;
	String filePath;
	
	public ConnectionManager(DatagramSocket receiveSocket, DatagramPacket receivedPacket, byte[] data,String filepath ){
		this.receivePacket = receivedPacket;
		this.receiveSocket = receiveSocket;
		this.filepath=filepath;
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.data = data;
	}
	
	public void run(){
		// Process the received datagram.
					
					System.out.println("Server: Packet received:");
					System.out.println("From host: " + receivePacket.getAddress());
					System.out.println("Host port: " + receivePacket.getPort());
					int len = receivePacket.getLength();
					System.out.println("Length: " + len);
					System.out.print("Containing: " );

					// Form a String from the byte array.
					String received = new String(data,0,len);   
					System.out.println(received + "\n");
					System.out.print("Containing Bytes: ");
				    System.out.println(Arrays.toString(Utility.getBytes(receivePacket.getData(),0, len)));
				    
				    
				    //Validate Request
				    if (isValidRequest(data)){
				    	if (data[1] == READ){
				    		isReadRequest = true;
				    		isWriteRequest = false;
				    	} else if (data[1] == WRITE){
				    		isReadRequest = false;
				    		isWriteRequest = true;
				    	}
				    } else { // Throw an exception if the packet is an invalid request
				    	System.out.println("Packet is invalid.");
						receiveSocket.close();
						
						try {
							throw new Exception("Invalid Packet.");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
				    }

					// Slow things down (wait 5 seconds)
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e ) {
						e.printStackTrace();
						System.exit(1);
					}

				

					// Construct a datagram packet that is to be sent to a specified port 
					// on a specified host.
					// The arguments are:
					//  data - the packet data (a byte array). This is the packet data
					//         that was received from the client.
					//  receivePacket.getLength() - the length of the packet data.
					//    Since we are echoing the received packet, this is the length 
					//    of the received packet's data. 
					//    This value is <= data.length (the length of the byte array).
					//  receivePacket.getAddress() - the Internet address of the 
					//     destination host. Since we want to send a packet back to the 
					//     client, we extract the address of the machine where the
					//     client is running from the datagram that was sent to us by 
					//     the client.
					//  receivePacket.getPort() - the destination port number on the 
					//     destination host where the client is running. The client
					//     sends and receives datagrams through the same socket/port,
					//     so we extract the port that the client used to send us the
					//     datagram, and use that as the destination port for the echoed
					//     packet.
					
					
					//Create a response byte array
					//0301 for a read request
					//0401 for a write request
					byte[] responseData = null;
					
					if (isReadRequest){
						responseData=new byte[516];
						responseData[0] = 0;
						responseData[1] = 3;
						responseData[2] = 0;
						responseData[3] = 1;
						BufferedInputStream in = null;
						try {
							in = new BufferedInputStream(new FileInputStream(filepath));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						        byte[] data1 = new byte[512];
						        byte[] ACK = new byte[4];
						        DatagramPacket receivePacketACK = new DatagramPacket(ACK, ACK.length);
						        int n;
						        
						        /* Read the file in 512 byte chunks. */
						        try {
									while ((n = in.read(data1)) != -1) {
									    /* 
									     * We just read "n" bytes into array data. 
									     * Now write them to the output file. 
									     */
										System.arraycopy(data1,0,responseData,4,data1.length);
										sendPacket = new DatagramPacket(responseData, responseData.length,
												receivePacket.getAddress(), receivePacket.getPort());
										System.out.println( "Server: Sending packet:");
										System.out.println("To host: " + sendPacket.getAddress());
										System.out.println("Destination host port: " + sendPacket.getPort());
										len = sendPacket.getLength();
										System.out.println("Length: " + len);
										System.out.print("Containing: ");
										System.out.println(new String(sendPacket.getData(),0,len));
										System.out.print("Containing Bytes: ");
									    System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(),0, len)));
									    
									    //send data
									    try {
											sendReceiveSocket.send(sendPacket);
										} catch (IOException e) {
											e.printStackTrace();
											System.exit(1);
										}

										System.out.println("data sent");
										//get acknowledge
									    try {
											sendReceiveSocket.receive(receivePacketACK);
										} catch (IOException e) {
											e.printStackTrace();
											System.exit(1);
										}
										
										System.out.println("Server: Packet received:");
										System.out.println("From host: " + receivePacketACK.getAddress());
										System.out.println("Host port: " + receivePacketACK.getPort());
										len = receivePacketACK.getLength();
										System.out.println("Length: " + len);
										System.out.print("Containing: " );

										// Form a String from the byte array.
										received = new String(data,0,len);   
										System.out.println(received + "\n");
										System.out.print("Containing Bytes: ");
									    System.out.println(Arrays.toString(Utility.getBytes(receivePacketACK.getData(),0, len)));
										
										
										
									    
									  
									  
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
					} else if (isWriteRequest){
						/*responseData=new byte[4];
						responseData[0] = 0;
						responseData[1] = 4;
						responseData[2] = 0;
						responseData[3] = 0; */
						BufferedOutputStream out = null;
						try {
							out = new BufferedOutputStream(new FileOutputStream("C:/Users/Iman/Desktop/workspace/Sysc3303/src/serverPackage/in.dat.txt"));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						byte[] DATA = new byte[516];
						byte[] ACK = {0,4,0,0};
						DatagramPacket sendPacketACK = new DatagramPacket(ACK, ACK.length);
						DatagramPacket receivePacketDATA = new DatagramPacket(DATA, DATA.length);


								
							
							
								while(true){
									
								//send ack
							    try {
									sendReceiveSocket.send(sendPacketACK);
								} catch (IOException e) {
									e.printStackTrace();
									System.exit(1);
								}
								
								System.out.println( "Server: Sending packet:");
								System.out.println("To host: " + receivePacket.getAddress());
								System.out.println("Destination host port: " + receivePacket.getPort());
								len = sendPacketACK.getLength();
								System.out.println("Length: " + len);
								System.out.print("Containing: ");
								System.out.println(new String(sendPacketACK.getData(),0,len));
								System.out.print("Containing Bytes: ");
							    System.out.println(Arrays.toString(Utility.getBytes(sendPacketACK.getData(),0, len)));
								
							    
							    if(receivePacketDATA.getLength()<516){
							    	System.exit(1);
							    }
							    
							    
								//get data
							    try {
									sendReceiveSocket.receive(receivePacketDATA);
								} catch (IOException e) {
									e.printStackTrace();
									System.exit(1);
								}
							    System.out.println("Server: Packet received:");
								System.out.println("From host: " + receivePacket.getAddress());
								System.out.println("Host port: " + receivePacket.getPort());
								 len = receivePacketDATA.getLength();
								System.out.println("Length: " + len);
								System.out.print("Containing: " );

								// Form a String from the byte array.
								received = new String(data,0,len);   
								System.out.println(received + "\n");
								System.out.print("Containing Bytes: ");
								System.out.println(Arrays.toString(Utility.getBytes(receivePacketDATA.getData(),0, len)));	
								
								try {
									out.write(DATA,4,512);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								

								// get block number from the received block
								ACK[2] = receivePacketDATA.getData()[2];
								ACK[3] = receivePacketDATA.getData()[3];
								sendPacketACK = new DatagramPacket(ACK, ACK.length);

								

								
								}
								
							}
							
						
						
						
					}
					
					
					
					

		
	
	
	/**
	 * Validate the request
	 * @param data the request data to validate
	 * @return false if request is not valid, true if request is valid
	 */
	public boolean isValidRequest(byte[] data){
		boolean isValid = true;
		
		if (data[0]!=0) return false; //First element should always be zero

		//2nd element can either be 1 or 2
		if (data[1]==1 || data[1] == 2) isValid = true;
		else {
			return false;
		}

		int count = 0;
		
		int fileNameEndingIndex = 0;
		
		boolean zeroSwitch = false; //true when iterator hits a zero
		
		for (int i = 2; i<receivePacket.getLength(); i++){
			
			if (data[i] != 0) count++;
			
			else if (data[i] == 0) {
				fileNameEndingIndex = i-1; //index of the last byte of the file name in the data array
				zeroSwitch = true; //zero found, turn the switch on
				break;
			}
		}

		//The count will be zero if there is no file name in the data
		if (count==0 || count > receivePacket.getLength()-3) return false;
		
		if (!zeroSwitch) return false;


		int modeEndingIndex = 0;
		count = 0;
		zeroSwitch = false;
		for (int j = fileNameEndingIndex + 2; j<receivePacket.getLength(); j++){
			if (data[j] != 0) count++;

			else if (data[j] == 0) {
				modeEndingIndex = j-1; //index of the last character of the "mode" in the data array
				zeroSwitch = true;
				break;
			}

		}

		if (count==0 || count == receivePacket.getLength()) return false; 
		if (!zeroSwitch) return false;
		else {
			if (receivePacket.getLength() - modeEndingIndex != 2) return false;
		}

		return true;
	}
}
