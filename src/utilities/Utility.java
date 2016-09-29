package utilities;

/*Assignment 1
 * Name: Sahaj Arora
 * Student No. 100961220
 */

//Utility.java
//Utility class to provide utility methods that can be used by any class.
public class Utility {
	public static boolean shutDown=false;
	/**
	 * Trim a byte array from 0 to specified length
	 * @param data the byte array to trim
	 * @param len length of the trimmed array
	 * @return trimmed byte array
	 */
	public static byte[] getBytes(byte[] data, int pos,int len) {
		
		 byte[] bytes = new byte[len];
		for (int i = pos; i<len; i++){
			bytes[i] = data[i];
		}
		
		return bytes;
	}
	public static int increment(byte array[]){
		int j,k,l;
		 boolean flag,flag2;
	
		flag=false;
		flag2=false;
		
		
		
		
		
		  
		 
		 	array[3]++;
		 	//when we reach to -128 we need to add 256 so we can keep counting up
		 	if(array[3]>=-128 && array[3]<=0){
		 		flag=true;
		 		
		 	}
		 	if(flag){
		 		
		 		k=array[3]+256;
		 	}else {
		 		k=array[3];
		 	}
		 	if(k==256){
		 		flag=false;
		 		k=0;
		 		array[2]+=1;
		 		array[3]=0;
		 	}
		 	//for the number of the left we also can reach -32768 so we have to add (2^16-1) 
		 	//so we keep counting up
		 	if((array[2]<<8)>=-32768 && (array[2]<<88)<=0 ){
		 		flag2=true;
		 	}
		 	//comment
		 	if(flag2){
		 		l=(array[2]<<8)+k+65536;
		 	}else{
			
			   
			   l=(array[2]<<8)+k;
		 	}
			
			return l;
		}
	public static int getByteInt(byte array[]){
		int j,k,l;
		 boolean flag,flag2;
	
		flag=false;
		flag2=false;
		if(array[3]>=-128 && array[3]<=0){
	 		flag=true;
	 		
	 	}
	 	if(flag){
	 		
	 		k=array[3]+256;
	 	}else {
	 		k=array[3];
	 	}
	 	if(k==256){
	 		flag=false;
	 		k=0;
	 		array[2]+=1;
	 		array[3]=0;
	 	}
	 	//for the number of the left we also can reach -32768 so we have to add (2^16-1) 
	 	//so we keep counting up
	 	if((array[2]<<8)>=-32768 && (array[2]<<8)<=0){
	 		flag2=true;
	 	}
	 	//comment
	 	if(flag2){
	 		l=(array[2]<<8)+k+65536;
	 	}else{
		
		   
		   l=(array[2]<<8)+k;
	 	}
		
		return l;
		
	}

}
