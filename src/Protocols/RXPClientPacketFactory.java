package Protocols;

import java.util.Random;

/**
 * Created by justinluk1 on 11/11/15.
 *
 * Class used to designate packet instantiation based on connection codes
 */
public class RXPClientPacketFactory {
    
    //give state code and get bytes out;
    RXPPacket packet;
    RXPHeader packetHeader;
    Random rand = new Random();

    public RXPClientPacketFactory(){}

    //public RXPPacket(int packetSize, int dataSize, int seqNumber,
    // int ackNumber, String sourceIP, String destIP, short destPort,
    // short sourcePort, int connectionCode)

    //Initiate a CONNECT request
    public RXPPacket createConnectionPacket(String sourceIP, String destIP, short destPort, short sourcePort) {
        packet = new RXPPacket(0, 0, Math.abs(rand.nextInt()), 0, sourceIP, destIP, destPort, sourcePort, 100);
        return packet;
    }

    //Initiate a PUT request
    public RXPPacket createPutRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize){
        packet = new RXPPacket(0, dataSize, Math.abs(rand.nextInt()), 0, sourceIP, destIP, destPort, sourcePort, 700);
        return packet;
    }

    //Initiate a GET request
    public RXPPacket createSendRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize, int seqNumber, int packetSize){
        packet = new RXPPacket(packetSize, dataSize, seqNumber, 0, sourceIP, destIP, destPort, sourcePort, 706);
        return packet;
    }

    //GET the rest of a download
    public RXPPacket createClientRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize, int ackNumber){
        packet = new RXPPacket(0, dataSize, 0,ackNumber, sourceIP, destIP, destPort, sourcePort, 706);
        return packet;
    }

    /*
     * Method to generate new response packets based on connection codes of previous packet received
     */
    public RXPPacket createNextPacket(RXPPacket packetRecvd, String sourceIP, short sourcePort) {
        packet = new RXPPacket();
        packetHeader = new RXPHeader();
        switch(packetRecvd.getPacketHeader().getConnectionCode()){

            //connection-request response
            case 101:
                packetHeader.setDataSize(0);
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(200);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            //Server requested a PUT
            case 700:
                packetHeader.setDataSize(packetRecvd.getPacketHeader().getDataSize());
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(701);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            //ACK received from server for GET request
            case 701:
                packetHeader.setDataSize(packetRecvd.getPacketHeader().getDataSize());
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(702);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            case 703:
                packetHeader.setDataSize(packetRecvd.getPacketHeader().getDataSize());
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(705);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;
             
            //Getting data from server
            case 705:
            	packetHeader.setDataSize(packetRecvd.getPacketHeader().getDataSize());
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(706);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;
                
            //Sending data to server    
            case 706:
            	packetHeader.setDataSize(packetRecvd.getPacketHeader().getDataSize());
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(705);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(packetRecvd.getPacketHeader().getAckNumber() + 1);
                packetHeader.setAckNumber(Math.abs(rand.nextInt()));
                break;
                
            default:
                break;
        }

        packet.setRXPPacketHeader(packetHeader);
        return packet;
    }
}
