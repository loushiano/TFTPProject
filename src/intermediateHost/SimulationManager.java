package intermediateHost;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import utilities.Utility;

public class SimulationManager extends Thread {
	private DatagramPacket receivePacket,sendPacket;
	private DatagramSocket sendReceiveSocket,receiveClientSocket,sendToClientSocket;
	private int clientPort;
	private boolean flag;
	private int testCode,packetNum;
	byte data[]=null;
	private int AckData;
	private int counter;
	private DatagramPacket receiveclientPacket;
	private boolean flagsendClient,flagReceiveClient,flagReceiveServer;
	private DatagramPacket delayedData,delayedAck,delayed,duplicated;
	private int timer;
	private boolean flagSendToServer=true;
	
	
	public SimulationManager(DatagramPacket receivePacket,int testCode,int packetNum, int AckData){
		this.receivePacket=receivePacket;
		clientPort=receivePacket.getPort();
		try {
			sendReceiveSocket=new DatagramSocket();
			receiveClientSocket=new DatagramSocket();
			sendToClientSocket= new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 this.testCode=testCode;
		 this.packetNum=packetNum;
		 this.AckData=AckData;
		 counter=0;
		 delayedData=null;
		 delayedAck=null;
		 flagsendClient=true;
		 flagReceiveClient=true;
	}
	@Override
	public void run(){
		
		System.out.println("IntermediateHost: Packet received:");
	      System.out.println("From host: " + receivePacket.getAddress());
	      System.out.println("Host port: " + receivePacket.getPort());
	      int len = receivePacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: " );

	      // Form a String from the byte array.
	      String received = new String(receivePacket.getData(),0,len);   
	      System.out.println(received + "\n");
	      System.out.println("Containing Bytes: ");
	      System.out.println(Arrays.toString(Utility.getBytes(receivePacket.getData(),0, len)));
	      
	      
	      
	      try {
				sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
						  InetAddress.getLocalHost(), 69);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		      System.out.println( "IntermediateHost: Sending packet:");
		      System.out.println("To Server: " + sendPacket.getAddress());
		      System.out.println("Destination host port: " + sendPacket.getPort());
		      len = sendPacket.getLength();
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
		      flag=true;
		      start:
		      while(flag){
		    	  counter++;
			      data = new byte[516];
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
			      	//Simulate errors
			      
			      	if(testCode!=0 && counter==packetNum){
			      		
			      		if(testCode==1 && receivePacket.getLength()>4 && AckData==1){
			      			System.out.println("kol khara");
			      			System.out.println("Data packet received from the server is being lost, will wait for another data again");
			      		continue start;
			      		}else if(testCode==1 && receivePacket.getLength()==4 && AckData==0 ){
			      			System.out.println("kol khara");
			      			flagsendClient=false;flagReceiveClient=true;
			      		}else if(testCode==2 && receivePacket.getLength()>4 && AckData==1){
			      			 delayedData=receivePacket;
			      			 timer=(int) System.currentTimeMillis();
			      			continue start;
			      			
			      		}else if(testCode==2 && receivePacket.getLength()==4 && AckData==0){
			      			flagsendClient=false;flagReceiveClient=true;
			      			timer=(int) System.currentTimeMillis();
			      			delayedAck=receivePacket;
			      		}else if(testCode==3 && ((receivePacket.getLength()==4 && AckData==0) ||(receivePacket.getLength()>4 && AckData==1 ))){
			      			
			      			sendToClient(true,null);
			      		}
			      	}
			      	if(delayedData!=null){
			      	if((timer+5000)==(int)System.currentTimeMillis()){
			      		sendToClient(true,delayedData);
			      		delayedData=null;
			      	}
			      	}
			      	if(delayedAck!=null){
				      	if((timer+5000)==(int)System.currentTimeMillis()){
				      		sendToClient(true,delayedAck);
				      		delayedAck=null;
				      		
				      	}
				      	}
			
			    
			      // Process the received datagram.
			      System.out.println("IntermediateHost: Packet received:");
			      System.out.println("From host: " + receivePacket.getAddress());
			      System.out.println("Host port: " + receivePacket.getPort());
			      len = receivePacket.getLength();
			      System.out.println("Length: " + len);
			      System.out.print("Containing: " );
			      if((len!=4 && len!=516)||Utility.containsAzero(data, 4,len)){
			    	  flag=false;
			    	  
			    	  
			      }
			      // Form a String from the byte array.
			      received = new String(data,0,len);   
			      System.out.println(received + "\n");
			     
			      
			    
			      
			      
			      
			      //Send packet to client
			      sendToClient(flagsendClient,null);
			 
			      		receiveFromClient(flagReceiveClient);
			      		
			      	 	if(testCode!=0 && counter==packetNum){
			      	 		
				      		if(testCode==1 && receiveclientPacket.getLength()>4 && AckData==1){
				      			System.out.println("Data packet received from the client is being lost, will wait for another data from it again");
				      			receiveFromClient(true);
				      		}else if(testCode==1 && receiveclientPacket.getLength()==4 && AckData==0 ){
				      			System.out.println("Ack packet received from the client is being lost, will wait for another data from the server again");
				      			flagSendToServer=false;
				      		}else if(testCode==2 && receiveclientPacket.getLength()>4 && AckData==1){
				      			 delayedData=receivePacket;
				      			 timer=(int) System.currentTimeMillis();
				      			flagSendToServer=false;
				      			
				      		}else if(testCode==2 && receiveclientPacket.getLength()==4 && AckData==0){
				      			System.out.println("Ack packet received from the client is being delayed, will wait for another data from the server again");
				      			flagSendToServer=false;
				      			timer=(int) System.currentTimeMillis();
				      			delayedAck=receiveclientPacket;
				      		}else if(testCode==3 && ((receiveclientPacket.getLength()==4 && AckData==0) ||(receiveclientPacket.getLength()>4 && AckData==1 ))){
				      			System.out.println("duplicating the ack");
				      			duplicated=receiveclientPacket;
				      			timer=(int) System.currentTimeMillis();
				      			
				      		}
				      	}
				      	if(delayedData!=null){
				      	if((timer+5010)==(int)System.currentTimeMillis()){
				      		sendBackToServer(true,delayedData);
				      		delayedData=null;
				      	}
				      	}
				      	if(delayedAck!=null){
				      		System.out.println(""+timer+ " " +(int)System.currentTimeMillis());
					      	if((timer+5010)<(int)System.currentTimeMillis()){
					      		System.out.println("send the delayed ACK again");
					      		
					      		sendBackToServer(true,delayedAck);
					      		flagSendToServer=false;
					      		delayedAck=null;
					      		
					      	}
					      	}
				      	if(duplicated!=null){
				      		System.out.println(""+timer+ " " +(int)System.currentTimeMillis());
					      	if((timer+1)<(int)System.currentTimeMillis()){
					      		System.out.println("send the duplicated ACK again");
					      		
					      		sendBackToServer(true,duplicated);
					      		flagSendToServer=false;
					      		duplicated=null;
					      		
					      	}
					      	}
				      	
			      		
				   
					      sendBackToServer(flagSendToServer,null);
					   
			      
			}
		
		
	}
	public void duplicate(){
		
	}
	public void delay(){
		
	}
	public void lose(){
		
	}
	public void printing(){
		
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
	      int len = receiveclientPacket.getLength();
	      System.out.println("Length: " + len);
	      System.out.print("Containing: ");
	      System.out.println(new String(receiveclientPacket.getData(),0,len));
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
			sendPacket = new DatagramPacket(data, receivePacket.getLength(),
					  InetAddress.getLocalHost(), clientPort);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			}else{
				sendPacket=delayed;
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
	    	  sendToClientSocket = new DatagramSocket();
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
		
	}else{
		flagReceiveClient=true;
	}
	}
}
