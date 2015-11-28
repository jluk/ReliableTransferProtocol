package Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.zip.Adler32;

/**
 * Created by justinluk1 on 11/11/15.
 */
public class RXPServer {

    private DatagramSocket serverSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket recvPacket;
    private String sourceIP;
    private String destIP;
    private short sourcePort;
    private short destPort;
    private int connectionState;
    private RXPServerPacketFactory packetFactory;
    private Adler32 adler;
    private int windowSize;
    private int rcvTimeout;

    private RXPPacket packetSent;
    private RXPPacket packetRecv;

    public RXPServer(String sourceIP, String destIP, short sourcePort, short destPort){
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.destIP = destIP;
        this.destPort = destPort;
        this.connectionState = 0;
        adler = new Adler32();
        windowSize = 1;
        rcvTimeout = 1500; //0.5 secs
        packetFactory = new RXPServerPacketFactory();
    }

    //Init and listen for connection requests
    public int startRXPServer() throws IOException, ClassNotFoundException{

        serverSocket = new DatagramSocket(sourcePort);
        packetSent = packetFactory.createConnectionPacket(sourceIP, destIP, destPort, sourcePort);
        System.out.println("Awaiting connection...");

        packetRecv = recvPacket();
        
        serverSocket.setSoTimeout(500);
        int attempt = 0;
        while( packetRecv.getPacketHeader().getConnectionCode() != 100 ) {
        	//HAMYChange - may not need the maxAttempt
        	/*if(attempt >= 10) {
        		System.out.println("Couldn't connect to server.");
        		System.exit(1);
        	}

        	attempt++; */
        	try{
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		sendPacket(packetSent); //Sending CC 101
        		continue;
        	}
        } 
        
        System.out.println(packetRecv.toString());

        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        sendPacket(packetSent); //CC 101
        System.out.println(packetSent.toString());

        attempt = 0;
        while( packetRecv.getPacketHeader().getConnectionCode() != 200 ) {
        	//HAMYChange - may not need the maxAttempt
        	/*if(attempt >= 10) {
        		System.out.println("Couldn't connect to server.");
        		System.exit(1);
        	}

        	attempt++; */
        	try{
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		sendPacket(packetSent); //Sending CC 101
        		continue;
        	}
        } 
        
        System.out.println(packetRecv.toString());

        if(packetRecv.getPacketHeader().getAckNumber() != packetSent.getPacketHeader().getSeqNumber() + 1) return -1;

        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        sendPacket(packetSent); //CC 201
        
        //HAMYChange - Shitty attempt to ensure 201 gets there
        attempt = 0;
        while(packetRecv.getPacketHeader().getConnectionCode() == 200) {
        	attempt++;
       	
        	try{
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		if(attempt>15) break;
        		sendPacket(packetSent); //Sending CC 101
        		continue;
        	}
        }
        
        System.out.println(packetSent.toString());

        connectionState = packetSent.getPacketHeader().getConnectionCode();
        
        if(connectionState == 201) System.out.println("Connection established...");
        
        //Set timeout back to infinity
        serverSocket.setSoTimeout(0);

        return connectionState;
    }

    //Listen for send/receive requests
    //TODO: Write PUT
    public byte[] runServer() throws ClassNotFoundException, IOException{
        if(connectionState != 201) return null;
        
        serverSocket.setSoTimeout(0);
        packetRecv = recvPacket();
        
        //Request to send data to client
        if(packetRecv.getPacketHeader().getConnectionCode() == 700){
            return clientSendRequestHandler();
        }

        return null;
    }

    //Parse data from a client's GET request
    private byte[] clientSendRequestHandler() throws IOException, ClassNotFoundException{

        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        sendPacket(packetSent); //CC 701 ACK the download request
        
        serverSocket.setSoTimeout(rcvTimeout);
        
        packetRecv = new RXPPacket();
        while(packetRecv.getPacketHeader().getConnectionCode() == 700) {
        	try {
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		sendPacket(packetSent);
        		continue;
        	}
        }
        
        int ackNum = 1;
        
        byte[] data = new byte[packetSent.getPacketHeader().getDataSize()];

        int dataPosition = 0;
        while(dataPosition < packetSent.getPacketHeader().getDataSize()){
        	
        	//HAMYChange
    		System.out.println("Server: packetRecv Seq num: " + packetRecv.getPacketHeader().getSeqNumber());
    		System.out.println("Server: ackNum: " + ackNum);
        	
        	while(packetRecv.getPacketHeader().getSeqNumber() != (ackNum - 1) ||
        			packetRecv.getData() == null) {
        		try{
        			packetRecv = recvPacket();
        		} catch(SocketTimeoutException e) {
        			sendPacket(packetSent);
        			continue;
        		}
        		
        		//HAMYChange
        		System.out.println("Server: packetRecv Seq num: " + packetRecv.getPacketHeader().getSeqNumber());
        		System.out.println("Server: ackNum: " + ackNum);
        	}
        	
        	
        	
        	
        	//HAMYChange
        	System.out.println("packetRecv: " + packetRecv);
        	System.out.println("data: " + data);
        	
            System.arraycopy(packetRecv.getData(), 0, data, dataPosition, packetRecv.getPacketHeader().getPacketSize());
            dataPosition+=packetRecv.getPacketHeader().getPacketSize();
            packetSent = packetFactory.createClientRequestPacket(sourceIP, destIP, destPort, sourcePort,
                    packetRecv.getPacketHeader().getDataSize(), ackNum);
            sendPacket(packetSent);
            
            ackNum+=2;
        }
        
        serverSocket.setSoTimeout(0);

        return data;
    }

    //Send data from a GET request
    public int sendData(byte[] data) throws IOException, ClassNotFoundException{
        if(connectionState != 201) return -1;
        
        System.out.println("Sending File to Client...");
        serverSocket.setSoTimeout(rcvTimeout);

        packetSent = packetFactory.createPutRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length);
        sendPacket(packetSent);
        
        packetRecv = new RXPPacket();
        
        int attempt = 0;
        while(packetRecv.getPacketHeader().getConnectionCode() != 701) {
        	try{
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		if(attempt > 10) return -1;
        		
        		
        		//HAMYChange
            	System.out.println("Waiting on CC 701 from Client...");
        	}
        	
        	sendPacket(packetSent);
        	
        	//HAMYChange
        	System.out.println("Waiting on CC 701 from Client...");
        	
        	attempt++;
        }
        
        int seqNum = 0;
        
        if(packetRecv.getPacketHeader().getConnectionCode() != 701) return -1;

        int dataPosition = 0;
        packetSent = packetFactory.createSendRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length, seqNum, (512 - packetSent.getPacketHeader().getHeaderSize()) >= data.length ? data.length : 512 - packetSent.getPacketHeader().getHeaderSize());

        while(dataPosition < data.length){
        	
        	//HAMYChange
        	System.out.println("ServerSend: dataPosition:" + dataPosition);
        	System.out.println("ServerSend: dataLength:" + data.length);
        	
            packetSent.setData(Arrays.copyOfRange(data, dataPosition, dataPosition + packetSent.getPacketHeader().getPacketSize()));
            sendPacket(packetSent);
            
            seqNum+=2;
            
            while(packetRecv.getPacketHeader().getAckNumber() != seqNum - 1) {
            	try {
            		packetRecv = recvPacket();
            	} catch(SocketTimeoutException e) {
            		sendPacket(packetSent);
            		continue;
            	}
            	
            	//HAMYChange
            	System.out.println("Server: packetRecv Ack num: " + packetRecv.getPacketHeader().getAckNumber());
        		System.out.println("Server: seqNum: " + seqNum);
            	
            }
                        
            dataPosition+=packetSent.getPacketHeader().getPacketSize();
            packetSent = packetFactory.createSendRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length, seqNum, (512 - packetSent.getPacketHeader().getHeaderSize()) >= data.length - dataPosition ? data.length - dataPosition : 512 - packetSent.getPacketHeader().getHeaderSize());
            
        }
        serverSocket.setSoTimeout(0);
        //HAMYChange
        System.out.println("Server Messages Sent...");
        return 0;
    }

    //method used to make byte[] from packets to be sent over the connection
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
        serverSocket.send(sendPacket);
    }

    //Grab and parse received packet and check for corruption
    private RXPPacket recvPacket() throws IOException, ClassNotFoundException{
        byte[] recv = new byte[512];
        RXPPacket newRecvdPacket;
        recvPacket = new DatagramPacket(recv, recv.length);
        serverSocket.receive(recvPacket);
        newRecvdPacket = new RXPPacket();
        newRecvdPacket.setRXPPacketHeader(recv);
        if(newRecvdPacket.getPacketHeader().getDataSize() != 0 && newRecvdPacket.getPacketHeader().getPacketSize() != 0) {
            newRecvdPacket.setData(Arrays.copyOfRange(recv, newRecvdPacket.getPacketHeader().getHeaderSize(), newRecvdPacket.getPacketHeader().getHeaderSize() + newRecvdPacket.getPacketHeader().getPacketSize()));
        }
        adler.update(Arrays.copyOfRange(recv, 4, newRecvdPacket.getPacketHeader().getHeaderSize() + newRecvdPacket.getPacketHeader().getPacketSize() - 4));

        return newRecvdPacket;
    }

    //Set window size
    public void setWindow(int size){
        this.windowSize = size;
    }

    //Close CC = 0
    public void close(){
        if(!serverSocket.isClosed()){
            serverSocket.close();
            connectionState = 0;
        }
    }
}
