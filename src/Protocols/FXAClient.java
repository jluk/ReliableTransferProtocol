package Protocols;

import java.net.InetAddress;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * FxA CLIENT
 ● Command: FxA-client X A P
 ● Command: connect - The FxA-client connects to the FxA-server (running at the same IP host).
 ● Command: get F - The FxA-client downloads file F from the server (if F exists in the same directory with the FxA-server program).
 ● Command: post F - The FxA-client uploads file F to the server (if F exists in the same directory with the FxA-client program). This feature will be treated as extra credit for up to 20 project points.
 ● Command: window W (only for projects that support configurable flow window) W: the maximum receiver’s window-size at the FxA-Client (in segments).
 ● Command: disconnect - The FxA-client terminates gracefully from the FxA-server.
 */

public class FXAClient {

  public static void main (String[] args) throws Exception {

   if (args.length != 3) {
    System.out.println("Invalid number of arguments. Correct usage involves three command-line arguments, \"fta-client X A P\".");
    System.out.println("X: the port number at which the FxA-client’s UDP socket should bind to (even number). This port number should be equal to the server’s port number minus 1");
    System.out.println("A: the IP address of NetEmu");
    System.out.println("P: the UDP port number of NetEmu");
    System.exit(0);
   }

   int hostPort = Integer.parseInt(args[0]);
   InetAddress IPAddress = InetAddress.getByName(args[1]);
   int destinationPort = Integer.parseInt(args[2]);

   System.out.println("File Transfer Protocol Client started.");

   // RUNNING BLOCK
   while (true) {


   }
  }

 /*
 * Download files to server from client
 * @
 */
 public static void get(){

 }
  /*
  * Upload files to server from client
  * @
  */
 public static void put(){

 }


}
