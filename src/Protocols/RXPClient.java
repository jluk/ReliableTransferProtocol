package Protocols;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.zip.Adler32;

/**
 * Created by justinluk1 on 11/10/15.
 */
public class RXPClient {

    private DatagramSocket clientSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket recvPacket;
    private String sourceIP;
    private String destIP;
    private short sourcePort;
    private short destPort;
    private int connectionState;
    private Adler32 adler;
    private RXPClientPacketFactory packetFactory;
    private int windowSize;
    private int rcvTimeout;

    private RXPPacket packetSent;
    private RXPPacket packetRecv;

    public RXPClient(String sourceIP, String destIP, short sourcePort, short destPort){
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.destIP = destIP;
        this.destPort = destPort;
        this.connectionState = 0;
        adler = new Adler32();
        windowSize = 1;
        rcvTimeout = 2000;
        packetFactory = new RXPClientPacketFactory();
    }

    /*
     * Attempt to connect RXPClient to RXPServer
     *
     * @return connection code of final transaction
     */
    public int connect() throws IOException, ClassNotFoundException{

        if(connectionState != 0) return -1;
        try {
            clientSocket = new DatagramSocket(sourcePort);
        } catch (BindException ex) {
            System.out.println("Address in use. Please try again...Exiting.");
            System.exit(1);
        }
        
        clientSocket.setSoTimeout(rcvTimeout);
        packetRecv = new RXPPacket();
        
        packetSent = packetFactory.createConnectionPacket(sourceIP, destIP, destPort, sourcePort); //CC 100
        
        System.out.println(packetSent.toString());
        
        while(packetRecv.getPacketHeader().getConnectionCode() != 101) {
        	
        	sendPacket(packetSent); //CC 100
        	try{ 
        		packetRecv = recvPacket(); //CC 101
        	} catch(SocketTimeoutException e) {
        		
        	}
        }
        
        System.out.println(packetRecv.toString());
        
        while(packetRecv.getPacketHeader().getConnectionCode() == 100) {
        	
        	sendPacket(packetSent);
        	try{ 
        		packetRecv = recvPacket(); //CC 101
        	} catch(SocketTimeoutException e) {
        		
        	}
        }

        //Check to drop any packet out of expected sequence
        //if(packetRecv.getPacketHeader().getAckNumber() != (packetSent.getPacketHeader().getSeqNumber() + 1)) return -1;

        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        sendPacket(packetSent); //CC 200
        System.out.println(packetSent.toString());
        
        while(packetRecv.getPacketHeader().getConnectionCode() != 201) {
        	
        	sendPacket(packetSent); //CC200
        	try{ 
        		packetRecv = recvPacket(); //CC 201
        	} catch(SocketTimeoutException e) {
        		
        	}
        }

        System.out.println(packetRecv.toString());

        connectionState = packetRecv.getPacketHeader().getConnectionCode();
        
        clientSocket.setSoTimeout(0);

        System.out.println("Connected to Server Successfully...");
        return connectionState;
    }

    //Send data to server
    public int sendData(byte[] data) throws IOException, ClassNotFoundException{

        if(connectionState != 201) return -1; //201 = connected
        clientSocket.setSoTimeout(rcvTimeout);

        //Tell server we are making a request
        packetSent = packetFactory.createPutRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length); //CC 700
        
        while(packetRecv.getPacketHeader().getConnectionCode() != 701) {
        	
        	sendPacket(packetSent);
        	try{ 
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		
        	}
        	
        	//HAMYChange
        	System.out.println("Waiting on CC 701 from Server...");
        	System.out.println(packetRecv.toString());
        }
        
        int dataPosition = 0;
        //Packetize the data payload: Create correct packet data space by checking data length
        packetSent = packetFactory.createSendRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length, 0,
                (512 - packetSent.getPacketHeader().getHeaderSize()) >= data.length ? data.length : 512 - packetSent.getPacketHeader().getHeaderSize());

        //Repeat sending until all data has been sent
        outerloop: while(dataPosition < data.length){
            packetSent.setData(Arrays.copyOfRange(data, dataPosition, dataPosition + packetSent.getPacketHeader().getPacketSize()));
            
            while(packetRecv.getPacketHeader().getAckNumber() != dataPosition + packetRecv.getPacketHeader().getPacketSize()
            		&& packetRecv.getPacketHeader().getAckNumber() != dataPosition + packetRecv.getPacketHeader().getDataSize()) {
            	
            	sendPacket(packetSent);
            	try{ 
            		packetRecv = recvPacket();
            	} catch(SocketTimeoutException e) {
            		
            	}
            	
            	//Breaks out if the server is trying to do something
            	if(packetRecv.getPacketHeader().getConnectionCode() == 700) break outerloop;
            	
            	//HAMYChange
                System.out.println(packetRecv.toString());
                System.out.println("DataPosition: " + dataPosition);
            }
            
            
            
            //HAMYChange
            System.out.println(packetRecv.toString());
            System.out.println("ClientSend");
            
            dataPosition = packetRecv.getPacketHeader().getAckNumber();
            packetSent = packetFactory.createSendRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length, packetRecv.getPacketHeader().getAckNumber(),
                    (512 - packetSent.getPacketHeader().getHeaderSize()) >= data.length -  dataPosition ? data.length - dataPosition : 512 - packetSent.getPacketHeader().getHeaderSize());
        }

        clientSocket.setSoTimeout(0);
        
        //HAMYChange
        System.out.println("Finished sending...");
        
        //Success
        return 0;
    }

    //Request data from server
    public byte[] getData(byte[] data) throws ClassNotFoundException, IOException{
        sendData(data);
        //packetRecv = recvPacket();

        return serverSendRequestHandler();
    }

    //GET Handler
    private byte[] serverSendRequestHandler() throws IOException, ClassNotFoundException{
    	
    	//HAMYChange
    	System.out.println("ServerSendRequestHandler...");
    	clientSocket.setSoTimeout(rcvTimeout);
    	
    	while(packetRecv.getPacketHeader().getConnectionCode() != 700) {

        	try{ 
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		
        	}
        	
        	//HAMYChange
            System.out.println("Waiting on CC 700 from Server...");
        }

        //Determine response packet from client to server
        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        sendPacket(packetSent);
        
        while(packetRecv.getPacketHeader().getConnectionCode() == 700) {

        	try{ 
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		
        	}
        	
        	sendPacket(packetSent);
        	
        	//HAMYChange
            System.out.println("Receiving CC 700 from Server...");
        }
        
        byte[] data = new byte[packetSent.getPacketHeader().getDataSize()];

        int dataPosition = 0;
        while(dataPosition < packetSent.getPacketHeader().getDataSize()){
            
            while(packetRecv.getPacketHeader().getSeqNumber() != dataPosition) {
            	
            	sendPacket(packetSent);
            	try{ 
            		packetRecv = recvPacket();
            	} catch(SocketTimeoutException e) {
            		
            	}
            	
            	//HAMYChange
                System.out.println(packetRecv.toString());
                System.out.println("DataPosition: " + dataPosition);
            }
            
            System.out.println("PacketAccepted");
            
            System.arraycopy(packetRecv.getData(), 0, data, dataPosition, packetRecv.getPacketHeader().getPacketSize());
            dataPosition += packetRecv.getPacketHeader().getPacketSize();
            packetSent = packetFactory.createClientRequestPacket(sourceIP, destIP, destPort, sourcePort,
                    packetRecv.getPacketHeader().getDataSize(), dataPosition);

            sendPacket(packetSent);
        }

        clientSocket.setSoTimeout(0);
        return data;
    }

    //Convert the received packet into a byte[] for transport
    private void sendPacket(RXPPacket packetToSend) throws IOException{
        byte[] bytesToSend = new byte[512];
        byte[] packetHeader = packetToSend.getPacketHeader().headerToByte();
        System.arraycopy(packetHeader, 0, bytesToSend, 0, packetHeader.length);
        if(packetToSend.getPacketHeader().getPacketSize() != 0){
            System.arraycopy(packetToSend.getData(), 0, bytesToSend, packetHeader.length, packetToSend.getPacketHeader().getPacketSize());
        }

        adler.update(Arrays.copyOfRange(bytesToSend, 4, packetHeader.length + packetToSend.getPacketHeader().getPacketSize() - 4));
        int checksum = (int)adler.getValue();
        bytesToSend[0] = (byte) ((checksum >> 24) & 0xff);
        bytesToSend[1] = (byte) ((checksum >> 16) & 0xff);
        bytesToSend[2] = (byte) ((checksum >> 8) & 0xff);
        bytesToSend[3] = (byte) (checksum & 0xff);
        sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getByName(packetToSend.getPacketHeader().getDestIP()), packetToSend.getPacketHeader().getDestPort());

        clientSocket.send(sendPacket);
    }

    //Receive incoming data and place into an RXPPacket
    private RXPPacket recvPacket() throws IOException, ClassNotFoundException{
        byte[] recv = new byte[512];
        RXPPacket newRecvdPacket;

        //Store datagramPacket in byte[]
        recvPacket = new DatagramPacket(recv, recv.length);
        clientSocket.receive(recvPacket);

        //Build an RXPPacket with datagramPacket
        newRecvdPacket = new RXPPacket();
        newRecvdPacket.setRXPPacketHeader(recv);
        if(newRecvdPacket.getPacketHeader().getDataSize() != 0 && newRecvdPacket.getPacketHeader().getPacketSize() != 0) {
            newRecvdPacket.setData(Arrays.copyOfRange(recv, newRecvdPacket.getPacketHeader().getHeaderSize(),
                    newRecvdPacket.getPacketHeader().getHeaderSize() + newRecvdPacket.getPacketHeader().getPacketSize()));
        }

        adler.update(Arrays.copyOfRange(recv, 4, newRecvdPacket.getPacketHeader().getHeaderSize() + newRecvdPacket.getPacketHeader().getPacketSize() - 4));

        return newRecvdPacket;
    }

    //Close the RXP connection
    //Closed CC = 0
    public void close() {
        if(!clientSocket.isClosed()){
            clientSocket.close();
            connectionState = 0;
        }
    }
}
