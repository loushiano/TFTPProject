package intermediateHost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Clientpackage.Client;

public class ErrorSimulatorUI {
	private ArrayList<String> testcodes;
	private ArrayList<Integer> results;
	public ArrayList<Integer> run() {
		BufferedReader br = null;
		//String mode=Constants.VERBOSE;
		testcodes=new ArrayList<String>();
		results=new ArrayList<Integer>();
		testcodes.add("1");testcodes.add("2");testcodes.add("3");testcodes.add("0");
		try{
			br = new BufferedReader(new InputStreamReader(System.in));
			
				
				
				
				System.out.println("Enter one of the following numbers to test the file transfer:\n 0-normal mode\n"
						+ "1-lose packet\n2-delay packet\n3-duplicate packet");
				System.out.println();
				String input = br.readLine();
				boolean flag1=true;
					while(flag1){
						
					
				if (testcodes.contains(input)){
					flag1=false;
					int i=Integer.parseInt(input);
					results.add(i);
					System.out.println();
					if(i!=0){
					System.out.println("Which packet would you like the test to apply on:\n0 is for RRQ or WRQ an"
							+ "d 1 and above for acks and datas");
					input=br.readLine();
					boolean flag=true;
					while(flag){
					try{
						i=Integer.parseInt(input);
						flag=false;
					}catch(NumberFormatException e){
						flag=true;
						System.out.println("unrecognized entry, please trye agin");
						input=br.readLine();
					}
					
					}
					results.add(i);
					if(i==0){
						ErrorSimulator.FLAG=true;
					}
					System.out.println("\n Now specify whether the packet you want to test is an ack or a data packet\n"
							+ "0-Ack, 1-data");
					input=br.readLine();
					flag=true;
					i=0;
					while(flag){
						try{
							flag=false;
							i=Integer.parseInt(input);
							if(i!=1 && i!=0){
								System.out.println("unrecognized entry, please trye agin");
								input=br.readLine();
								flag=true;
							}
						}catch(NumberFormatException e){
							flag=true;
							System.out.println("unrecognized entry, please trye agin");
							input=br.readLine();
						}
						
						}
					results.add(i);
					}	
				}else{
					System.out.println("Command not recognized. Please try again.");
				
					
				}
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
		return results;
	}

}
