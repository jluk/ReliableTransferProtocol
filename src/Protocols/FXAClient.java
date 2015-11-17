package Protocols;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * FxA CLIENT
 â—� Command: FxA-client X A P
 â—� Command: connect - The FxA-client connects to the FxA-server (running at the same IP host).
 â—� Command: get F - The FxA-client downloads file F from the server (if F exists in the same directory with the FxA-server program).
 â—� Command: post F - The FxA-client uploads file F to the server (if F exists in the same directory with the FxA-client program). This feature will be treated as extra credit for up to 20 project points.
 â—� Command: window W (only for projects that support configurable flow window) W: the maximum receiverâ€™s window-size at the FxA-Client (in segments).
 â—� Command: disconnect - The FxA-client terminates gracefully from the FxA-server.
 */

public class FXAClient {

	private static short clientPort;
	private static short netEmuPort;
	private static String ipAddress;


	public static void main (String[] args) throws Exception {

		if (args.length!=4||!args[0].equals("FxA-client")){
			System.out.println("The arguments entered were invalid. Exiting.");
			System.exit(0);
		}

		try{
			clientPort = Short.parseShort(args[1]);
		}
		catch(NumberFormatException ex){
			System.out.println("Port number must be an int. Exiting.");
			System.exit(0);
		}
		ipAddress = args[2];

		try{
			netEmuPort = Short.parseShort(args[3]);
		}
		catch(NumberFormatException ex){
			System.out.println("Port number was invalid. Exiting.");
			System.exit(0);
		}

		RXPClient client = new RXPClient("localhost", ipAddress, clientPort, netEmuPort);

		Scanner scan = new Scanner(System.in);

	   	System.out.println("File Transfer Protocol Client started.");


		// RUNNING BLOCK
		while (true) {

		   String nextLine = scan.nextLine();
		   String[] input = nextLine.split(" ");

		   //One Word input
		   if (input.length == 1) {

			   //CONNECT
			   if (nextLine.equals("connect")) {
				   client.connect();
			   }

			   //DISCONNECT
			   else if (nextLine.equals("disconnect")) {
				   client.close();
				   scan.close();
			   } else {
				   System.out.println("Invalid input.");
			   }
		   }

		   //Two Word Inputs
		   else if (input.length == 2) {

			   //GET
			   if (input[0].equals("get")) {
				   String filename = input[1];
				   System.out.println("Looking for file: " + filename);
				   String request = "GET*" + filename;
				   byte[] ret = client.getData(request.getBytes());

				   if (ret.length != 0) {
					   System.out.println("Writing file: " + filename);
					   FileOutputStream fos = new FileOutputStream(filename);
					   fos.write(ret);
					   fos.close();
					   System.out.println("GET Completed Successfully!");
				   } else {
					   System.out.println("GET Failed");
				   }
			   }

			   //POST
			   else if (input[0].equals("post")) {
				   String filename = input[1];
				   byte[] rqst = client.getData(("POST*" + filename).getBytes());
				   String r = new String(rqst);
				   System.out.println("Sent post notice.");

				   if (r.equals("!")) {
					   String fRqst = System.getProperty("user.dir") + "\\" + filename;
					   System.out.println("Searching for filepath: " + fRqst);
					   File f = new File(fRqst);
					   if (f.exists()) {
						   byte[] fileIn = new byte[Files.readAllBytes(f.toPath()).length];
						   fileIn = Files.readAllBytes(f.toPath());
						   client.sendData(fileIn);
					   } else {
						   System.out.println("File was not found.");
						   client.sendData(new byte[0]);
					   }
				   } else {
					   System.out.println("Incorrect request received.");
				   }
			   } else {
				   System.out.println("Invalid input.");
			   }
		   }
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

	 public static void connect(InetAddress IPAddress, int hostPort, int destinationPort){
		 System.out.println("Log: connect called");

	 }
 
	 public static void disconnect() {
		 System.out.println("Log: disconnect called");
	 }


}
