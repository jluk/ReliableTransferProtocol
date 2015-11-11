package RxpPacket;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * Class to define behavior of an RxpPacket.RXPPacket
 */
public class RXPPacket {

    //Components require the packetHeader defined in protocol and the actual data
    private RXPHeader packetHeader;
    private byte[] data;

    public RXPPacket(){
        packetHeader = new RXPHeader();
    }

    public RXPPacket(int packetSize, int dataSize, int seqNumber, int ackNumber, String sourceIP, String destIP, short destPort, short sourcePort, int connectionCode){
        packetHeader = new RXPHeader(packetSize, dataSize, seqNumber, ackNumber, sourceIP, destIP, destPort, sourcePort, connectionCode);
    }

    //Header getter
    public RXPHeader getPacketHeader(){
        return packetHeader;
    }

    //Header setter if given byte[]
    public void setRxPPacketHeader(byte[] data){
        packetHeader.byteToHeader(data);
    }

    //Header setter if given RxpPacket.RXPHeader object
    public void setRxPPacketHeader(RXPHeader header){
        packetHeader = header;
    }

    //Data getter
    public byte[] getData(){
        return data;
    }

    //Data setter
    public void setData(byte[] data){
        this.data = data;
    }

    //Checksum setter
    public void setChecksum(int checksum){
        packetHeader.setChecksum(checksum);
    }

    //toString method for debugging
    public String toString(){
        String strToRet = "";
        strToRet += "Packet Size: " + packetHeader.getPacketSize() + "\n";
        strToRet += "Data Size: " + packetHeader.getDataSize() + "\n";
        strToRet += "Sequence Number: " + packetHeader.getSeqNumber() + "\n";
        strToRet += "Ack Number: " + packetHeader.getAckNumber() + "\n";
        strToRet += "Connection Code: " + packetHeader.getConnectionCode() + "\n";
        strToRet += "Source IP: " + packetHeader.getSourceIP()+ ":" + packetHeader.getSourcePort() + "\n";
        strToRet += "Dest IP: " + packetHeader.getDestIP() + ":" + packetHeader.getDestPort() + "\n";

        return strToRet;
    }
}
