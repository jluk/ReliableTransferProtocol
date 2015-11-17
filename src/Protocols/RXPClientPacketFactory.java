package Protocols;

import RxpPacket.*;

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

    public RXPPacket createConnectionPacket(String sourceIP, String destIP, short destPort, short sourcePort) {
        packet = new RXPPacket(0, 0, Math.abs(rand.nextInt()), 0, sourceIP, destIP, destPort, sourcePort, 100);
        return packet;
    }

    public RXPPacket createPutRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize){
        packet = new RXPPacket(0, dataSize, Math.abs(rand.nextInt()), 0, sourceIP, destIP, destPort, sourcePort, 500);
        return packet;

    }

    public RXPPacket createSendRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize, int seqNumber, int packetSize){
        packet = new RXPPacket(packetSize, dataSize, seqNumber, 0, sourceIP, destIP, destPort, sourcePort, 506);
        return packet;
    }

    public RXPPacket createClientRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize, int ackNumber){
        packet = new RXPPacket(0, dataSize, 0,ackNumber, sourceIP, destIP, destPort, sourcePort, 506);
        return packet;
    }

    public RXPPacket createGetRequestPacket(){
        return null;
    }

    /*
     * Method to generate new response packets based on connection codes of previous packet received
     */
    public RXPPacket createNextPacket(RXPPacket packetRecvd, String sourceIP, short sourcePort) {
        packet = new RXPPacket();
        packetHeader = new RXPHeader();
        switch(packetRecvd.getPacketHeader().getConnectionCode()){

            case 101:
                packetHeader.setDataSize(0);
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(200);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                //packetHeader.setDestIP(packetRecvd.getPacketHeader().getSourceIP());
                //packetHeader.setDestPort(packetRecvd.getPacketHeader().getSourcePort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            default:
                break;
        }

        packet.setRxPPacketHeader(packetHeader);
        return packet;
    }
}
