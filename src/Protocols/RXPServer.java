package Protocols;

import RxpPacket.RXPPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
    private int windowSize;

    public RXPServer(String sourceIP, String destIP, short sourcePort, short destPort){
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.destIP = destIP;
        this.destPort = destPort;
        this.connectionState = 0;
        windowSize = 1;
    }

    //Init and listen for connection requests
    public int startRxPServer() throws IOException, ClassNotFoundException{

        return connectionState;
    }

    //Listen for send/receive requests
    public byte[] runServer() throws ClassNotFoundException, IOException{

        return null;
    }

    //Parse data from a PUT request
    private byte[] clientSendRequestHandler() throws IOException, ClassNotFoundException{

        return null;
    }

    //Send data from a GET request
    public int sendData(byte[] data) throws IOException, ClassNotFoundException{

        return 0;
    }

    //method used to make byte[] from packets to be sent over the connection
    private void sendPacket(RXPPacket packetToSend) throws IOException{

    }

    //Convert byte[] into RXPPackets
    private RXPPacket recvPacket(RXPPacket lastPacketSent) throws IOException, ClassNotFoundException{

        return new RXPPacket();
    }

    //Set window size
    public void setWindow(int size){
        this.windowSize = size;
    }

    public void close(){
        if(!serverSocket.isClosed()){
            serverSocket.close();
            connectionState = 0;
        }
    }
}
