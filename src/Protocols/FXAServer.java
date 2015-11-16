package Protocols;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * FxA SERVER
 â—� Command-line: FxA-server X A P
 The command-line arguments are:
 X: the port number at which the FxA-serverâ€™s UDP socket should bind to (odd number)
 A: the IP address of NetEmu
 P: the UDP port number of NetEmu
 â—� Command: window W (only for projects that support pipelined and bi- directional transfers)
 W: the maximum receiverâ€™s window-size at the FxA-Server (in segments).
 â—� Command: terminate Shut-down FxA-Server gracefully.
 */

public class FXAServer {
	public static void main(String[] args) throws Exception{
		
		if (args.length != 3) {
		    System.out.println("Invalid number of arguments. Correct usage involves three command-line arguments, \"fta-client X A P\".");
		    System.out.println("X: the port number at which the FxA-clientâ€™s UDP socket should bind to (even number). This port number should be equal to the serverâ€™s port number minus 1");
		    System.out.println("A: the IP address of NetEmu");
		    System.out.println("P: the UDP port number of NetEmu");
		    System.exit(0);
		   }

		   int hostPort = Integer.parseInt(args[0]);
		   InetAddress IPAddress = InetAddress.getByName(args[1]);
		   int destinationPort = Integer.parseInt(args[2]);

		   System.out.println("File Transfer Protocol Client started.");
		   
		   BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));

		   // RUNNING BLOCK
		   while (true) {
			   String input;
			   
			   //Block until != null
			   while((input = stdIn.readLine()) == null);

			   String[] inputLine = input.split("\\s");
			   
			   if(inputLine[0].equals("get")) {
				   if(inputLine.length == 2) get(inputLine[1]);
				   else System.out.println("Invalid command length. Usage example: get F");
			   } else if (inputLine[0].equals("post")) {
				   if(inputLine.length == 2) post(inputLine[1]);
				   else System.out.println("Invalid command length. Usage example: post F");
			   } else if (inputLine[0].equals("window")) {
				   if(inputLine.length == 2) window(inputLine[1]);
				   else System.out.println("Invalid command length. Usage example: window w");
			   } else if (inputLine[0].equals("terminate")) terminate();
			   else System.out.println("Invalid command.");
		   }
	}
	
	/*
	 * Download files from server to client
	 * @
	 */
	 public static void get(String fileName){
		 System.out.println("Log: get called with file name: " + fileName);
	 }
	  /*
	  * Upload files to server from client
	  * @
	  */
	 public static void post(String fileName){
		 System.out.println("Log: post called with file name: " + fileName);
	 }
	 
	 public static void window(String windowSize){
		 System.out.println("Log: window called with windowSize: " + windowSize);
	 }
	 
	 public static void terminate() {
		 
	 }

}
