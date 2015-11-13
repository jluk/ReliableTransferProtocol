package Protocols;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

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

  public static void main (String[] args) throws Exception {

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
	   } else if (inputLine[0].equals("connect")) connect();
	   else if (inputLine[0].equals("disconnect")) disconnect();
	   else System.out.println("Invalid command.");
   }
  }

 /*
 * Download files from server to client
 * @
 */
 public static void get(String fileName){

 }
  /*
  * Upload files to server from client
  * @
  */
 public static void post(String fileName){

 }
 
 public static void connect(){
	 
 }
 
 public static void disconnect() {
	 
 }


}
