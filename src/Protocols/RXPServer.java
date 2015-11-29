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
    int rcvTimeout;

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
        rcvTimeout = 3500;
        packetFactory = new RXPServerPacketFactory();
    }

    //Init and listen for connection requests
    public int startRXPServer() throws IOException, ClassNotFoundException{

        serverSocket = new DatagramSocket(sourcePort);
        packetSent = packetFactory.createConnectionPacket(sourceIP, destIP, destPort, sourcePort);
        System.out.println("Awaiting connection...");
        serverSocket.setSoTimeout(500);
        
        packetRecv = new RXPPacket();

        while(packetRecv.getPacketHeader().getConnectionCode() != 100) {
        	
        	try{ 
        		packetRecv = recvPacket(); //CC 100
        	} catch(SocketTimeoutException e) {
        		
        	}
        }
        
        System.out.println(packetRecv.toString());

        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        
        while(packetRecv.getPacketHeader().getConnectionCode() == 100) {
        	
        	sendPacket(packetSent);
        	try{ 
        		packetRecv = recvPacket(); //CC 101
        	} catch(SocketTimeoutException e) {
        		
        	}
        }
        
        System.out.println(packetSent.toString());
        
        while(packetRecv.getPacketHeader().getConnectionCode() != 200) {
        	
        	sendPacket(packetSent);
        	try{ 
        		packetRecv = recvPacket(); //CC 101
        	} catch(SocketTimeoutException e) {
        		
        	}
        }

        System.out.println(packetRecv.toString());

        //if(packetRecv.getPacketHeader().getAckNumber() != packetSent.getPacketHeader().getSeqNumber() + 1) return -1;

        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        
        int attempt = 0;
        while(packetRecv.getPacketHeader().getConnectionCode() == 200) {
        	
        	sendPacket(packetSent); //CC 201
        	try{ 
        		packetRecv = recvPacket(); 
        	} catch(SocketTimeoutException e) {
        		if(attempt>10) break;
        	}
        	
        	attempt++;
        }
        
        System.out.println(packetSent.toString());

        connectionState = packetSent.getPacketHeader().getConnectionCode();
        
        serverSocket.setSoTimeout(0);

        System.out.println("Connected to client successfully...");
        return connectionState;
    }

    //Listen for send/receive requests
    //TODO: Write PUT
    public byte[] runServer() throws ClassNotFoundException, IOException{
        if(connectionState != 201) return null;
        packetRecv = recvPacket();

        //Request to send data to client
        if(packetRecv.getPacketHeader().getConnectionCode() == 700){
            return clientSendRequestHandler();
        }

        return null;
    }

    //Parse data from a client's GET request
    private byte[] clientSendRequestHandler() throws IOException, ClassNotFoundException{
    	
    	//HAMYChange
    	System.out.println("ClientSendRequestHandler");
    	
        packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        serverSocket.setSoTimeout(rcvTimeout);
        
        while(packetRecv.getPacketHeader().getConnectionCode() != 700) {
        	
        	try{ 
        		packetRecv = recvPacket(); //CC 101
        	} catch(SocketTimeoutException e) {
        		
        	}
        	
        	System.out.println("Waiting on CC 700 from client...");
        }
        
        while(packetRecv.getPacketHeader().getConnectionCode() == 700) {
        	
        	sendPacket(packetSent); //CC 701
        	try{ 
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		
        	}
        	
        	//HAMYChange
        	System.out.println("Receiving CC 700");
        	System.out.println(packetSent.toString());
        }

        byte[] data = new byte[packetSent.getPacketHeader().getDataSize()];

        int dataPosition = 0;
        while(dataPosition < packetSent.getPacketHeader().getDataSize()){
            
        	//Make sure CCs don't have seqnum == 0;
            while(packetRecv.getPacketHeader().getSeqNumber() != dataPosition) {
            	
            	sendPacket(packetSent);
            	try{ 
            		packetRecv = recvPacket();
            	} catch(SocketTimeoutException e) {
            		
            	}
            	
            	//ACK old packets being sent over.
            	if(packetRecv.getPacketHeader().getSeqNumber() < dataPosition) {
            		sendPacket(packetFactory.createClientRequestPacket(sourceIP, destIP, destPort, sourcePort,
                            packetRecv.getPacketHeader().getDataSize(), packetRecv.getPacketHeader().getSeqNumber() + packetRecv.getPacketHeader().getPacketSize()));
            	}
            	
            	//HAMYChange
                System.out.println(packetRecv.toString());
                System.out.println("DataPosition: " + dataPosition);
            }
            
            //HAMYChange
            System.out.println(packetRecv.toString());
            
            System.arraycopy(packetRecv.getData(), 0, data, dataPosition, packetRecv.getPacketHeader().getPacketSize());
            dataPosition += packetRecv.getPacketHeader().getPacketSize();
            packetSent = packetFactory.createClientRequestPacket(sourceIP, destIP, destPort, sourcePort,
                    packetRecv.getPacketHeader().getDataSize(), dataPosition);
            sendPacket(packetSent);
        }
        
        while(packetRecv.getPacketHeader().getSeqNumber() == dataPosition){
        	sendPacket(packetSent);
        	try{ 
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		
        	}
        }
        
        //HAMYChange
        System.out.println("End of loop");

        serverSocket.setSoTimeout(0);
        return data;
    }

    //Send data from a GET request
    public int sendData(byte[] data) throws IOException, ClassNotFoundException{
        if(connectionState != 201) return -1;
        serverSocket.setSoTimeout(rcvTimeout);

        packetSent = packetFactory.createPutRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length);
        
        while(packetRecv.getPacketHeader().getConnectionCode() != 701) {
        	
        	
        	try{ 
        		packetRecv = recvPacket();
        	} catch(SocketTimeoutException e) {
        		sendPacket(packetSent);
        	}
        	
        	//HAMYChange
        	System.out.println("Server SendData: Waiting on CC 701 from Client...");
        }
        
        //if(packetRecv.getPacketHeader().getConnectionCode() != 701) return -1;

        int dataPosition = 0;
        packetSent = packetFactory.createSendRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length, 0, (512 - packetSent.getPacketHeader().getHeaderSize()) >= data.length ? data.length : 512 - packetSent.getPacketHeader().getHeaderSize());

        while(dataPosition < data.length){
            packetSent.setData(Arrays.copyOfRange(data, dataPosition, dataPosition + packetSent.getPacketHeader().getPacketSize()));
            sendPacket(packetSent);
            
            while(packetRecv.getPacketHeader().getAckNumber() != dataPosition + packetSent.getPacketHeader().getPacketSize()) {
            	
            	sendPacket(packetSent);
            	try{ 
            		packetRecv = recvPacket();
            	} catch(SocketTimeoutException e) {
            		
            	}

            	
            	//HAMYChange
                System.out.println(packetRecv.toString());
                System.out.println("DataPosition: " + dataPosition);
                
            }
            
          //HAMYChange
            System.out.println("Accepted packet.");
            System.out.println(packetRecv.toString());
            System.out.println("DataPosition: " + dataPosition);

            dataPosition = packetRecv.getPacketHeader().getAckNumber();
            packetSent = packetFactory.createSendRequestPacket(sourceIP, destIP, destPort, sourcePort, data.length, packetRecv.getPacketHeader().getAckNumber(), (512 - packetSent.getPacketHeader().getHeaderSize()) >= data.length - dataPosition ? data.length - dataPosition : 512 - packetSent.getPacketHeader().getHeaderSize());
        }
        
        serverSocket.setSoTimeout(0);
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
