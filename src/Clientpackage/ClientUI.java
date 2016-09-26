package Clientpackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import utilities.Contants;

public class ClientUI {
	Client c = new Client();
	
	 public static void main(String[] args) {

	        BufferedReader br = null;

	        try {

	            br = new BufferedReader(new InputStreamReader(System.in));

	            while (true) {

	                System.out.print("Enter command: ");
	                String input = br.readLine();

	                if (input.equals("rrq")){
	                	
	                	
	                	//c.sendAndReceive(requestType, filePath, vqMode, tnMode);
	                	System.out.println("Sending a read request.");
	                    
	                }
	                
	                //Adding a test commit comment
	                if ("q".equals(input)) {
	                    System.out.println("Exit!");
	                    System.exit(0);
	                }

	                System.out.println("input : " + input);
	                System.out.println("-----------\n");
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	    }
}
