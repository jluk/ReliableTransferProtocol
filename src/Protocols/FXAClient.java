package Protocols;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * FxA CLIENT
 * Command: FxA-client X A P
 * Command: connect - The FxA-client connects to the FxA-server (running at the same IP host).
 * Command: get F - The FxA-client downloads file F from the server (if F exists in the same directory with the FxA-server program).
 * Command: post F - The FxA-client uploads file F to the server (if F exists in the same directory with the FxA-client program). This feature will be treated as extra credit for up to 20 project points.
 * Command: window W (only for projects that support configurable flow window) W: the maximum receiverâ€™s window-size at the FxA-Client (in segments).
 * Command: disconnect - The FxA-client terminates gracefully from the FxA-server.
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
		boolean run = true;
		while(run){
			String nextLine = scan.nextLine();
			String[] input = nextLine.split(" ");
			if (input.length==1){
				if(nextLine.equals("connect")){
					client.connect();
				}
				else if (nextLine.equals("disconnect")){
					client.close();
					scan.close();
					run = false;
				}
				else{
					System.out.println("Invalid input.");
				}
			}
			else if (input.length==2){
				String filename = input[1];

				if (input[0].equals("get")){
					System.out.println("Looking for file: "+filename);
					String request = "GET*"+filename;
					byte[] ret = client.getData(request.getBytes());
					if (ret.length!=0){
						System.out.println("Writing file: "+filename);
						FileOutputStream fos = new FileOutputStream(filename);
						fos.write(ret);
						fos.close();
						System.out.println("Successful GET.");
					}
					else{
						System.out.println("Unsuccessful GET.");
					}
				}
				else if(input[0].equals("post")){
					byte[] rqst = client.getData(("POST*"+filename).getBytes());
					String r = new String(rqst);
					System.out.println("Sent post notice.");
					if (r.equals("!")){
						String fRqst = System.getProperty("user.dir")+"/"+filename;
						System.out.println("Searching for filepath: "+fRqst);
						File f = new File(fRqst);
						if (f.exists()){
							byte[] fileIn = new byte[Files.readAllBytes(f.toPath()).length];
							fileIn = Files.readAllBytes(f.toPath());
							client.sendData(fileIn);
						}
						else{
							System.out.println("File was not found.");
							client.sendData(new byte[0]);
						}
					}
					else{
						System.out.println("Incorrect request received.");
					}
				}
				else{
					System.out.println("Invalid input.");

				}
			}
		}
	}
}
