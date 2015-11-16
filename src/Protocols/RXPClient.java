package Protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.zip.Adler32;
import RxpPacket.*;

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
    private RXPClientPacketFactory packetFactory;
    private int windowSize;

    private RXPPacket packetSent;
    private RXPPacket packetRecv;

    public RXPClient(String sourceIP, String destIP, short sourcePort, short destPort){
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.destIP = destIP;
        this.destPort = destPort;
        this.connectionState = 0;
        Adler32 adler = new Adler32();
        windowSize = 1;
        packetFactory = new RXPClientPacketFactory();
    }

    //Attempt to establish connection with RXPServer
    public int connect() throws IOException, ClassNotFoundException{

        if(connectionState != 0) return -1;
        clientSocket = new DatagramSocket(sourcePort);
        //packetSent = packetFactory.createConnectionPacket(sourceIP, destIP, destPort, sourcePort);
        sendPacket(packetSent);
        packetRecv = recvPacket(packetSent);

        if(packetRecv.getPacketHeader().getAckNumber() != (packetSent.getPacketHeader().getSeqNumber() + 1)) return -1;
        //packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        sendPacket(packetSent);
        packetRecv = recvPacket(packetSent);
        connectionState = packetRecv.getPacketHeader().getConnectionCode();

        return connectionState;
    }

    //Send data to server
    public int sendData(byte[] data) throws IOException, ClassNotFoundException{
    	
    	//Dummy code to test NetEmu message relay
    	DatagramPacket toSend = new DatagramPacket(data, data.length, InetAddress.getByName(destIP), destPort);
    	clientSocket.send(toSend);

        return 0;
    }

    //Request data from server
    public byte[] getData(byte[] data) throws ClassNotFoundException, IOException{
        sendData(data);
        packetRecv = recvPacket(packetSent);

        return serverSendRequestHandler();

    }

    //Packetize the current packet to be sent and send it
    //Handle received packet
    private byte[] serverSendRequestHandler() throws IOException, ClassNotFoundException{
        //packetSent = packetFactory.createNextPacket(packetRecv, sourceIP, sourcePort);
        //sendPacket(packetSent);
        byte[] data = new byte[packetSent.getPacketHeader().getDataSize()];

        return data;
    }

    //Convert the received packet into a byte[] for transport
    private void sendPacket(RXPPacket packetToSend) throws IOException{

    }

    //Receive incoming data and place into an RXPPacket
    private RXPPacket recvPacket(RXPPacket lastPacketSent) throws IOException, ClassNotFoundException{

        return new RXPPacket();
    }

    //Close the RXP connection
    public void close() {
        if(!clientSocket.isClosed()){
            clientSocket.close();
            connectionState = 0;
        }
    }
}
