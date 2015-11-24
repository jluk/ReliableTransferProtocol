package Protocols;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * FxA SERVER
 * Command-line: FxA-server X A P
 * X: the port number at which the FxA-serverâ€™s UDP socket should bind to (odd number)
 * A: the IP address of NetEmu
 * P: the UDP port number of NetEmu
 *
 * Command: window W (only for projects that support pipelined and bi- directional transfers)
 * W: the maximum receiver's window-size at the FxA-Server (in segments).
 *
 * Command: terminate Shut-down FxA-Server gracefully.
 */

public class FXAServer {
	
	private static short serverPort;
	private static short netEmuPort;
	private static String ipAddress;

	public static void main(String args[]) throws ClassNotFoundException, IOException {

		if (args.length!=4||!args[0].equals("FxA-server")){
			System.out.println("The arguments entered were invalid. Exiting.");
			System.exit(0);
		}

		try{
			serverPort = Short.parseShort(args[1]);

			if ((serverPort & 1)!=1){
				System.out.println("Server port number must be odd. Exiting.");
				System.exit(1);
			}
			netEmuPort = Short.parseShort(args[3]);
		}
		catch (NumberFormatException ex){
			System.out.println("The port number was invalid. Exiting.");
			System.exit(0);
		}
		ipAddress = args[2];


		RXPServer server = new RXPServer("localhost", ipAddress, serverPort, netEmuPort);
		server.startRXPServer();

		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader scan = new BufferedReader(in);
		boolean run = true;
		String nextLine = "";
		byte[] request = null;
		while(run){
			request = server.runServer();
			if (request!= null){
				System.out.println("Receiving request from client.");
				String val = new String(request);
				if (val.indexOf("GET*")!=-1){
					String fRqst = val.substring(4);
					fRqst = System.getProperty("user.dir")+"/"+ fRqst;
					System.out.println("Searching for filepath: "+fRqst);
					File f = new File(fRqst);
					if (f.exists()){
						byte[] fileIn = new byte[Files.readAllBytes(f.toPath()).length];
						fileIn = Files.readAllBytes(f.toPath());
						server.sendData(fileIn);
					}
					else{
						System.out.println("The file "+fRqst+" was not found.");
						server.sendData(new byte[0]);
					}
				}
				else if (val.indexOf("POST*")!=-1){
					String fname = val.substring(5);
					byte[] serverResponse = "!".getBytes();
					System.out.println("Sending ready response.");
					int response = server.sendData(serverResponse);
					if (response>=0){
						byte[] clientResponse = null;
						System.out.println("Waiting for file.");
						do{
							clientResponse = server.runServer();

						}
						while(clientResponse == null);

						if (clientResponse.length != 0){
							System.out.println("Post was successful.");
							FileOutputStream fos = new FileOutputStream(fname);
							fos.write(clientResponse);
							fos.close();
						}
						else{
							System.out.println("Post was unsuccessful.");
						}
					}
					else{
						System.out.println("Unable to send response.");
					}
				}
				else{
					System.out.println("Invalid Request.");
					server.sendData(new byte[0]);
				}

			}
			else{
				if (System.in.available()>0){
					System.out.println("Waiting for input: ");
					nextLine = scan.readLine();
					if (nextLine.length()>=8){
						String[] input = nextLine.split(" ");
						if (input[0].equals("window")){
							try{
								int size = Integer.parseInt(input[1]);
								server.setWindow(size);
							}
							catch(NumberFormatException ex){
								System.out.println("Invalid window size.");
							}
						}
						else if (input[0].equals("terminate")){
							server.close();
							run = false;
						}
						else{
							System.out.println("Invalid command.");
						}
					}
					else{
						System.out.println("Invalid command.");
					}
				}
			}
		}
	}
}
