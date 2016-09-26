package utilities;

/*Assignment 1
 * Name: Sahaj Arora
 * Student No. 100961220
 */

//Utility.java
//Utility class to provide utility methods that can be used by any class.
public class Utility {
	
	/**
	 * Trim a byte array from 0 to specified length
	 * @param data the byte array to trim
	 * @param len length of the trimmed array
	 * @return trimmed byte array
	 */
	public static byte[] getBytes(byte[] data, int len) {
		
		 byte[] bytes = new byte[len];
		for (int i = 0; i<len; i++){
			bytes[i] = data[i];
		}
		
		return bytes;
	}

}
