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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import Clientpackage.Client;
import utilities.Utility;

public class ErrorSimulator {
	public static boolean FLAG;
	DatagramPacket receivePacket;
	DatagramSocket receiveSocket;
	int testCode;int packetNum=0;int AckData=0;
	private DatagramPacket sendPacket;
	private DatagramSocket sendReceiveSocket,sendToClientSocket;
	private int clientPort;
	private boolean flag;
	byte data[]=null;
	
	private int counter=0;
	private DatagramPacket receiveclientPacket;
	private boolean flagsendClient,flagReceiveClient;
	private DatagramPacket delayedData,delayedAck,duplicated,dupReq;
	private int timer;
	private boolean flagSendToServer=true;
	private DatagramPacket duplicated1;
	private DatagramPacket delayedAck1;
	private DatagramPacket delayedData1;
	private int write;
	private DatagramPacket request;
	private byte[] data1;
	private DatagramPacket delReq;
	private int delay=6010;
	private DatagramPacket clientPacket;
	
	public ErrorSimulator(){
		
		try {
			
			receiveSocket = new DatagramSocket(23);
			
		} catch (SocketException se) {
	         se.printStackTrace();
	         System.exit(1);
	    }
		try {
			sendReceiveSocket=new DatagramSocket();
			 
			sendToClientSocket= new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 delayedData=null;
		 delayedAck=null;
		 flagsendClient=true;
		 flagReceiveClient=true;
		 duplicated1=null;
		 delayedAck1=null;
		 delayedData1=null;
		 dupReq=null;
		 delReq=null;
		
	}
	
	public void receiveSendPacket(){
			
			// Construct a DatagramPacket for receiving packets up 
	      // to 100 bytes long (the length of the byte array).
				
		      data1 = new byte[1400];
		      ErrorSimulatorUI ui=new ErrorSimulatorUI();
				ArrayList<Integer> results=ui.run();
				testCode=results.get(0);
				if(results.size()>1){
				packetNum=results.get(1);
				if(results.size()>2){
				AckData=results.get(2);
				}
				}
				 receiveRequest();
					if(testCode==1 && packetNum==0){
						receiveRequest();
						sendRequest(null);
						
					}else if(testCode==2 && packetNum==0){
						delReq=clientPacket;
						timer=(int)System.currentTimeMillis();
						receiveRequest();
						sendRequest(null);
						
					}else if (testCode==3 && packetNum==0){
						sendRequest(null);
						dupReq=clientPacket;
						timer=(int)System.currentTimeMillis();
					}else if(testCode==4){
						DatagramPacket errorPacket=clientPacket;
						errorPacket.getData()[1]=9;
						sendRequest(errorPacket);
						
					}else if(testCode==6){
						int i=Utility.getFirstZero(clientPacket.getData());
						byte arr[]=new byte[clientPacket.getLength()-i];
						System.arraycopy(clientPacket.getData(),i+1, arr,0,clientPacket.getLength()-i-1);
						int j=Utility.getFirstZero(arr);
						//System.out.println(new String(clientPacket.getData(),j+2,1));
						clientPacket.getData()[j+2]=11;
						sendRequest(null);
					}else if(testCode==9){
						int i=Utility.getFirstZero(clientPacket.getData());
						clientPacket.getData()[i]=1;
						sendRequest(null);
					
					}else {	
						sendRequest(null);
						request=clientPacket;
					}
					
		      		
		    	  
		    	  clientPort=clientPacket.getPort();
		      		
					
			      flag=true;
			      start:
			      while(flag){
			    	  counter++;
				      data = new byte[516];
				      receiveFromServer();
				      	//Simulate errors
				      
				      	if(testCode!=0 && counter==packetNum){
				      		
				      		if(testCode==1 && receivePacket.getLength()>4 && AckData==1){
				      			
				      			System.out.println("Data packet received from the server is being lost, will wait for another data again");
				      		continue start;
				      		}else if(testCode==1 && receivePacket.getLength()==4 && AckData==0 ){
				      			System.out.println("ACK packet received from the server is being lost, will wait for another data again");
				      			if(counter>1){
				      			flagsendClient=false;flagReceiveClient=true;
				      			}else{
				      				receiveRequest();
				      				sendBackToServer(true,request);
				      				continue start;
				      				
				      			}
				      		}else if(testCode==2 && receivePacket.getLength()>4 && AckData==1){
				      			System.out.println("the data packet received will be delayed");
				      			 delayedData1=receivePacket;
				      			 timer=(int) System.currentTimeMillis();
				      			continue start;
				      			
				      		}else if(testCode==2 && receivePacket.getLength()==4 && AckData==0){
				      			System.out.println("ACK packet received from the server is being DELAYED, will wait for another data again");
				      			flagsendClient=false;flagReceiveClient=true;
				      			timer=(int) System.currentTimeMillis();
				      			delayedAck1=receivePacket;
				      		}else if(testCode==3 && ((receivePacket.getLength()==4 && AckData==0) ||(receivePacket.getLength()>4 && AckData==1 ))){
				      			
				      			System.out.println("duplicating the Packet");
				      			duplicated1=receivePacket;
				      			timer=(int) System.currentTimeMillis();
				      		}else if (testCode==5 && ((receivePacket.getLength()==4 && AckData==0) ||(receivePacket.getLength()>4 && AckData==1 ))){
				      			receivePacket.getData()[1]=9;
				      		}else if(testCode==7 && ((receivePacket.getLength()==4 && AckData==0) ||(receivePacket.getLength()>4 && AckData==1 ))){
				      			receivePacket.getData()[3]=(byte) (receivePacket.getData()[3]+9);
				      		}else if(testCode==8 && ((receivePacket.getLength()==4 && AckData==0) ||(receivePacket.getLength()>4 && AckData==1 ))){
				      			try {
									DatagramSocket error= new DatagramSocket();
									try {
										sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
												  InetAddress.getLocalHost(), clientPort);
									} catch (UnknownHostException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
										try {
											error.send(sendPacket);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
								} catch (SocketException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				      			
				      		}
				      	}
				      	if(delayedData1!=null){
				      		System.out.println(""+timer+ " " +(int)System.currentTimeMillis());
				      	if((timer+5010)<(int)System.currentTimeMillis()){
				      		
				      		System.out.println("sending the delayed data");
				      		sendToClient(true,delayedData1);
				      		receiveFromClient(true);
				      		sendBackToServer(true,null);
				      		delayedData1=null;
				      	}
				      	}
				      	if(delayedAck1!=null){
					      	if((timer+5010)<(int)System.currentTimeMillis()){
					      		System.out.println("sending the delayed Ack");
					      		sendToClient(true,delayedAck1);
					      		delayedAck1=null;
					      		
					      	}
					      	}
				      	if(duplicated1!=null){
				      		System.out.println(""+timer+ " " +(int)System.currentTimeMillis());
					      	if((timer+1)<(int)System.currentTimeMillis()){
					      		System.out.println("send the duplicated Packet again");
					      		sendToClient(true,duplicated1);
					      		if(duplicated1.getLength()>4){
					      		
					      		receiveFromClient(true);
					      		sendBackToServer(true,null);
					      		}
					      		duplicated1=null;
					      		
					      	}
				      	}
				      	if(dupReq!=null){
				      		if((timer+5)<(int)System.currentTimeMillis()){
				      		sendRequest(dupReq);
				      		}
				      		dupReq=null;
				      	}
				      	if(delReq!=null){
				      		
				      		if((timer+6010)<(int)System.currentTimeMillis()){
				      			
					      		sendRequest(delReq);
					      		}
					      		delReq=null;
				      		
				      	}
				
				    
				      
				    
				     
				      
				    
				      
				      
				      
				      //Send packet to client
				      sendToClient(flagsendClient,null);
				      if(receivePacket.getData()[1]==5){
				    	  return;
				      }
				      		receiveFromClient(flagReceiveClient);
				      		
				      	 	if(testCode!=0 && counter==packetNum){
				      	 		
					      		if(testCode==1 && receiveclientPacket.getLength()>4 && AckData==1){
					      			System.out.println("Data packet received from the client is being lost, will wait for another data from it again");
					      			receiveFromClient(true);
					      		}else if(testCode==1 && receiveclientPacket.getLength()==4 && AckData==0 ){
					      			System.out.println("Ack packet received from the client is being lost, will wait for another data from the server again");
					      			flagSendToServer=false;
					      		}else if(testCode==2 && receiveclientPacket.getLength()>4 && AckData==1){
					      			System.out.println("DATA packet received from the client is being delayed, will wait for another data from the server again");
					      			 
					      			 timer=(int) System.currentTimeMillis();
					      			 receiveFromClient(true);
					      			delayedData=receiveclientPacket;
					      		}else if(testCode==2 && receiveclientPacket.getLength()==4 && AckData==0){
					      			System.out.println("Ack packet received from the client is being delayed, will wait for another data from the server again");
					      			flagSendToServer=false;
					      			timer=(int) System.currentTimeMillis();
					      			if(delay<Client.TIMEOUT){
					      				flagSendToServer=true;
					      			}
					      			delayedAck=receiveclientPacket;
					      		}else if(testCode==3 && ((receiveclientPacket.getLength()==4 && AckData==0) ||(receiveclientPacket.getLength()>4 && AckData==1 ))){
					      			System.out.println("duplicating the packet");
					      			duplicated=receiveclientPacket;
					      			timer=(int) System.currentTimeMillis();
					      			
					      		}else if(testCode==5 && ((receiveclientPacket.getLength()==4 && AckData==0) ||(receiveclientPacket.getLength()>4 && AckData==1 )) ){
					      			receiveclientPacket.getData()[1]=9;
					      		}else if (testCode==7 && ((receiveclientPacket.getLength()==4 && AckData==0) ||(receiveclientPacket.getLength()>4 && AckData==1 ))){
					      			receiveclientPacket.getData()[3]=(byte)(receiveclientPacket.getData()[3]+9);
					      		}else if(testCode==8 && ((receiveclientPacket.getLength()==4 && AckData==0) ||(receiveclientPacket.getLength()>4 && AckData==1 ))){
					      			try {
										DatagramSocket error= new DatagramSocket();
										try {
											sendPacket = new DatagramPacket(receiveclientPacket.getData(), receiveclientPacket.getLength(),
													  InetAddress.getLocalHost(),receivePacket.getPort());
										} catch (UnknownHostException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
											try {
												error.send(sendPacket);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
									} catch (SocketException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					      			
					      		}
					      	}
					      	if(delayedData!=null){
					      	if((timer+delay)<(int)System.currentTimeMillis()){
					      		sendBackToServer(true,delayedData);
					      		receiveFromServer();
					      		sendToClient(true,null);
					      		if(delayedData.equals(receiveclientPacket)){
					      			receiveFromClient(true);
					      		}
					      		delayedData=null;
					      	}
					      	}
					      	if(delayedAck!=null){
					      		System.out.println(""+timer+ " " +(int)System.currentTimeMillis());
						      	if((timer+delay)<(int)System.currentTimeMillis()){
						      		System.out.println("send the delayed ACK again");
						      		
						      		sendBackToServer(true,delayedAck);
						      		
						      		delayedAck=null;
						      		
						      	}
						      	}
					      	if(duplicated!=null){
					      		System.out.println(""+timer+ " " +(int)System.currentTimeMillis());
						      	if((timer+4)<(int)System.currentTimeMillis()){
						      		System.out.println("send the duplicated Packet again");
						      		
						      		sendBackToServer(true,duplicated);
						      		if(duplicated.getLength()>4){
						      			receiveFromServer();
							      		sendToClient(true,null);
							      		
						      		}
						      		
						      		duplicated=null;
						      		
						      	}
						      	}
					      	
				      		
					   
						      sendBackToServer(flagSendToServer,null);
						   
				      
				}
			      if(write==2){
			    	  
			    	  receiveFromServer();
			    	  
			    	  sendToClient(true,null);
			    	  
			      }

		     
		     
				
				
				
				
				
				
				
	}
	
	private void receiveRequest() {
		clientPacket = new DatagramPacket(data1, data1.length);
	      System.out.println("IntermediateHost: Waiting for Packet.\n");

	      // Block until a datagram packet is received from receiveSocket.
	      try {        
	         receiveSocket.receive(clientPacket);
	      } catch (IOException e) {
	         System.out.println("kol");
	      }
	      System.out.println("IntermediateHost: Packet received:");
	      System.out.println("From host: " + clientPacket.getAddress());
	      System.out.println("Host port: " + clientPacket.getPort());
	      int len = clientPacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: " );

	      // Form a String from the byte array.
	      String received = new String(clientPacket.getData(),0,len);   
	      System.out.println(received + "\n");
	      System.out.println("Containing Bytes: ");
	      System.out.println(Arrays.toString(Utility.getBytes(clientPacket.getData(),0, len)));
	       write=clientPacket.getData()[1];
	      
		
	}

	
	private void sendRequest(DatagramPacket delayed) {
		
		if(delayed==null){
	      
	      
	      
	      try {
				sendPacket = new DatagramPacket(clientPacket.getData(), clientPacket.getLength(),
						  InetAddress.getLocalHost(), 69);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	      }else{
	    	  try {
					sendPacket = new DatagramPacket(delayed.getData(), delayed.getLength(),
							  InetAddress.getLocalHost(), 69);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	  
	      }

		      System.out.println( "IntermediateHost: Sending packet:");
		      System.out.println("To Server: " + sendPacket.getAddress());
		      System.out.println("Destination host port: " + sendPacket.getPort());
		      int len = sendPacket.getLength();
		      System.out.println("Length: " + len);
		      System.out.print("Containing: ");
		      System.out.println(new String(sendPacket.getData(),0,len));
		      System.out.println("Containing Bytes: ");
		      System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(),0, len)));
		      // Send the datagram packet to the client via the send socket. 
		     
		      try {
		         sendReceiveSocket.send(sendPacket);
		      } catch (IOException e) {
		         e.printStackTrace();
		         System.exit(1);
		      }

		      System.out.println("IntermediateHost: packet sent to Server");
		
	}
	public void sendBackToServer(boolean flag,DatagramPacket delayed){
			if(flag){
				if(delayed==null){
		 try {
				sendPacket = new DatagramPacket(receiveclientPacket.getData(), receiveclientPacket.getLength(),
						  InetAddress.getLocalHost(),receivePacket.getPort());
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				}else{
					try {
						sendPacket= new DatagramPacket(delayed.getData(), delayed.getLength(),
								  InetAddress.getLocalHost(),receivePacket.getPort());
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	      System.out.println( "IntermediateHost: Sending packet:");
	      System.out.println("To Server: " + receivePacket.getAddress());
	      System.out.println("Destination host port: " + receivePacket.getPort());
	      int len = sendPacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: ");
	      System.out.println(new String(sendPacket.getData(),0,len));
	      // Send the datagram packet to the client via the send socket. 
	      try {
	    	  
	         sendReceiveSocket.send(sendPacket);
	      } catch (IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }

	      System.out.println("IntermediateHost: packet sent to Server");
			}else{
				flagSendToServer=true;
			}
	}	
	public void sendToClient(boolean flag,DatagramPacket delayed){
		if(flag){
			if(delayed==null){
		try {
			sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
					  InetAddress.getLocalHost(), clientPort);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			}else{
				try {
					sendPacket=new DatagramPacket(delayed.getData(), delayed.getLength(),
							  InetAddress.getLocalHost(), clientPort);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
	      System.out.println( "IntermediateHost: Sending packet:");
	      System.out.println("To Client: " + sendPacket.getAddress());
	      System.out.println("Destination host port: " + sendPacket.getPort());
	      int len = sendPacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: ");
	      System.out.println(new String(sendPacket.getData(),0,len));
	      
	      // Send the datagram packet to the client via the send socket.
	      
	      try {
	    	 
	         sendToClientSocket.send(sendPacket);
	      } catch (IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }

	      System.out.println("IntermediateHost: packet sent to Client");
		}else{
			flagsendClient=true;
		}
		
	}
	public void receiveFromClient(boolean flag1){
		data = new byte[516];
			if(flag1){
	       receiveclientPacket = new DatagramPacket(data, data.length);
	      
	      try {        
	    	  	
		         System.out.println("Waiting..."); // so we know we're waiting
		         sendToClientSocket.receive(receiveclientPacket);
		      } catch (IOException e) {
		         System.out.print("IO Exception: likely:");
		         System.out.println("Receive Socket Timed Out.\n" + e);
		         e.printStackTrace();
		         System.exit(1);
		      }

		      
		      // Process the received datagram.
		      System.out.println("IntermediateHost: Packet received:");
		      System.out.println("From host: " + receiveclientPacket.getAddress());
		      System.out.println("Host port: " + receiveclientPacket.getPort());
		      int len = receiveclientPacket.getLength();
		      System.out.println("Length: " + len);
		      System.out.print("Containing: " );
		      if((len!=4 && len!=516)||Utility.containsAzero(data, 4,len)){
		    	  flag=false;
		    	  
		    	  
		      }
		      // Form a String from the byte array.
		      String received = new String(receiveclientPacket.getData(),0,len);   
		      System.out.println(received + "\n");
		      byte opblock[]=new byte[4];
		      System.arraycopy(receiveclientPacket.getData(), 0, opblock, 0, 4);
		      System.out.println("opcode: "+ Utility.getByteInt(opblock));
		
	}else{
		flagReceiveClient=true;
	}
	}
	public void receiveFromServer(){
		data=new byte[516];
		receivePacket = new DatagramPacket(data, data.length);
	      System.out.println("IntermediateHost: Waiting for Packet.\n");
	      	
	      // Block until a datagram packet is received from receiveSocket.
	       
	      try {        
	         System.out.println("Waiting..."); // so we know we're waiting
	         sendReceiveSocket.receive(receivePacket);
	      } catch (IOException e) {
	         System.out.print("IO Exception: likely:");
	         System.out.println("Receive Socket Timed Out.\n" + e);
	         e.printStackTrace();
	         System.exit(1);
	      }
	   // Process the received datagram.
	      System.out.println("IntermediateHost: Packet received:");
	      System.out.println("From host: " + receivePacket.getAddress());
	      System.out.println("Host port: " + receivePacket.getPort());
	     int len = receivePacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: " );
	      if((len!=4 && len!=516)||Utility.containsAzero(data, 4,len)){
	    	  flag=false;
	    	  
	    	  
	      }
	      // Form a String from the byte array.
	     String received = new String(data,0,len);   
	      System.out.println(received + "\n");
	}
	public static void main( String args[] )
	{
		ErrorSimulator e = new ErrorSimulator();
		//Thread waitingThread=new WaitingThread();
		//waitingThread.start();
		
		e.receiveSendPacket();
	}
	
	
}
