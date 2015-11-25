package Protocols;

import java.util.Random;

/**
 * Created by justinluk1 on 11/11/15.
 *
 * Class used to designate packet instantiation based on connection codes
 */
public class RXPServerPacketFactory {
    RXPPacket packet;
    RXPHeader packetHeader;
    Random rand = new Random();

    public RXPServerPacketFactory(){}

    public RXPPacket createConnectionPacket(String sourceIP, String destIP, short destPort, short sourcePort) {
        packet = new RXPPacket(0, 0, 0, 0, sourceIP, destIP, destPort, sourcePort, 0);
        packet.setChecksum(-1);
        return packet;
    }

    public RXPPacket createClientRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize, int ackNumber){
        packet = new RXPPacket(0, dataSize, 0,ackNumber, sourceIP, destIP, destPort, sourcePort, 706);
        return packet;
    }

    public RXPPacket createPutRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize){
        packet = new RXPPacket(0, dataSize, Math.abs(rand.nextInt()), 0, sourceIP, destIP, destPort, sourcePort, 700);
        return packet;
    }

    public RXPPacket createSendRequestPacket(String sourceIP, String destIP, short destPort, short sourcePort, int dataSize, int seqNumber, int packetSize){
        packet = new RXPPacket(packetSize, dataSize, seqNumber, 0, sourceIP, destIP, destPort, sourcePort, 706);
        return packet;
    }

    public RXPPacket createNextPacket(RXPPacket packetRecvd, String sourceIP, short sourcePort){
        packet = new RXPPacket();
        packetHeader = new RXPHeader();
        switch(packetRecvd.getPacketHeader().getConnectionCode()){//201 == connected

            //connection requested from new client
            case 100:
                packetHeader.setDataSize(0);
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(101);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            //connection response ACK'd by client --> send 201 established
            case 200:
                packetHeader.setDataSize(0);
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(201);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            //GET request from server
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

            case 702:
                packetHeader.setDataSize(packetRecvd.getPacketHeader().getDataSize());
                packetHeader.setPacketSize(0);
                packetHeader.setConnectionCode(703);
                packetHeader.setSourceIP(sourceIP);
                packetHeader.setSourcePort(sourcePort);
                packetHeader.setDestIP(packetRecvd.getPacketHeader().getDestIP());
                packetHeader.setDestPort(packetRecvd.getPacketHeader().getDestPort());
                packetHeader.setSeqNumber(Math.abs(rand.nextInt()));
                packetHeader.setAckNumber(packetRecvd.getPacketHeader().getSeqNumber() + 1);
                break;

            //Gettigng data from client
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
                
            //Sending data to client    
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
