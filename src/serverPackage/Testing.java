package serverPackage;

public class Testing {
	 static int j,k,l;
	 static boolean flag,flag2;
public static void main(String ars[]){
	flag=false;
	flag2=false;
	byte bla[]=new byte[2];
	bla[0]=0;
	bla[1]=0;
	
	  for(int i=0;i<65535;i++){
	 
	 	bla[1]++;
	 	
	 	if(bla[1]==-128){
	 		flag=true;
	 		
	 	}
	 	if(flag){
	 		
	 		k=bla[1]+256;
	 	}else {
	 		k=bla[1];
	 	}
	 	if(k==256){
	 		flag=false;
	 		k=0;
	 		bla[0]+=1;
	 		bla[1]=0;
	 	}
	 	if((bla[0]<<8)==-32768){
	 		flag2=true;
	 	}
	 	if(flag2){
	 		l=(bla[0]<<8)+k+65536;
	 	}else{
		
		   
		   l=(bla[0]<<8)+k;
	 	}
		System.out.println(" "+l);
		//System.out.print(" "+j);
	}
}
}

