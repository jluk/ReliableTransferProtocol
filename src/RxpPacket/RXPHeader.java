package RxpPacket;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by justinluk1 on 11/10/15.
 *
 * Header objects easily parsed into byte[] for transport
 */
public class RXPHeader {

    private int packetSize;//512 bytes
    private int dataSize;
    private int seqNumber;
    private int ackNumber;
    private String sourceIP;
    private String destIP;
    private short destPort;
    private short sourcePort;
    private int connectionCode;
    private int checksum;

    public RXPHeader(){
        packetSize = 0;//512 bytes
        dataSize = 0;
        seqNumber = 0;
        ackNumber = 0;
        sourceIP = "";
        destIP = "";
        destPort = 0;
        sourcePort = 0;
        connectionCode = 0;
        checksum = 0;
    };

    public RXPHeader(int packetSize, int dataSize, int seqNumber, int ackNumber, String sourceIP, String destIP, short destPort, short sourcePort, int connectionCode){
        this.packetSize = packetSize;
        this.dataSize = dataSize;
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.sourceIP = sourceIP;
        this.destIP = destIP;
        this.destPort = destPort;
        this.sourcePort = sourcePort;
        this.connectionCode = connectionCode;
    }

    //SETTERS

    public void setPacketSize(int packetSize){
        this.packetSize = packetSize;
    }

    public void setDataSize(int dataSize){
        this.dataSize = dataSize;
    }

    public void setSeqNumber(int seqNumber){
        this.seqNumber = seqNumber;
    }

    public void setAckNumber(int ackNumber){
        this.ackNumber = ackNumber;
    }

    public void setSourceIP(String sourceIP){
        this.sourceIP = sourceIP;
    }

    public void setDestIP(String destIP){
        this.destIP = destIP;
    }

    public void setDestPort(short destPort){
        this.destPort = destPort;
    }

    public void setSourcePort(short sourcePort){
        this.sourcePort = sourcePort;
    }

    public void setChecksum(int checksum){
        this.checksum = checksum;
    }

    public void setConnectionCode(int connectionCode){
        this.connectionCode = connectionCode;
    }

    //GETTERS

    public int getPacketSize(){
        return this.packetSize;
    }

    public int getDataSize(){
        return this.dataSize;
    }

    public int getSeqNumber(){
        return this.seqNumber;
    }

    public int getAckNumber(){
        return this.ackNumber;
    }

    public String getSourceIP(){
        return this.sourceIP;
    }

    public String getDestIP(){
        return this.destIP;
    }

    public short getDestPort(){
        return this.destPort;
    }

    public short getSourcePort(){
        return this.sourcePort;
    }

    public int getChecksum(){
        return this.checksum;
    }

    public int getConnectionCode(){
        return this.connectionCode;
    }

    public int getHeaderSize(){
        return (7*4) + sourceIP.length() + destIP.length() + 8;
    }

    //breaks down the header into a byte[] to be sent
    public byte[] headerToByte(){
        byte[] bytePacket = new byte[36 + sourceIP.length() + destIP.length()];
        System.arraycopy(ByteBuffer.allocate(4).putInt(checksum).array(), 0, bytePacket, 0, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(packetSize).array(), 0, bytePacket, 4, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(dataSize).array(), 0, bytePacket, 8, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(seqNumber).array(), 0, bytePacket, 12, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(ackNumber).array(), 0, bytePacket, 16, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(connectionCode).array(), 0, bytePacket, 20, 4);
        System.arraycopy(ByteBuffer.allocate(2).putShort(destPort).array(), 0, bytePacket, 24, 2);
        System.arraycopy(ByteBuffer.allocate(2).putShort(sourcePort).array(), 0, bytePacket, 26, 2);
        System.arraycopy(ByteBuffer.allocate(4).putInt(sourceIP.length()).array(), 0, bytePacket, 28, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(destIP.length()).array(), 0, bytePacket, 32, 4);
        System.arraycopy(sourceIP.getBytes(), 0, bytePacket, 36, sourceIP.length());
        System.arraycopy(destIP.getBytes(), 0, bytePacket, 36 + sourceIP.length(), destIP.length());
        return bytePacket;
    }

    //Rebuild the header to parse it
    public void byteToHeader(byte[] packet){

        //Repack the given byte[]
        checksum = (packet[0]<<24)&0xff000000|(packet[1]<<16)&0x00ff0000|(packet[2]<< 8)&0x0000ff00|(packet[3]<< 0)&0x000000ff;
        packetSize = (packet[4]<<24)&0xff000000|(packet[5]<<16)&0x00ff0000|(packet[6]<< 8)&0x0000ff00|(packet[7]<< 0)&0x000000ff;
        dataSize = (packet[8]<<24)&0xff000000|(packet[9]<<16)&0x00ff0000|(packet[10]<< 8)&0x0000ff00|(packet[11]<< 0)&0x000000ff;
        seqNumber = (packet[12]<<24)&0xff000000|(packet[13]<<16)&0x00ff0000|(packet[14]<< 8)&0x0000ff00|(packet[15]<< 0)&0x000000ff;
        ackNumber = (packet[16]<<24)&0xff000000|(packet[17]<<16)&0x00ff0000|(packet[18]<< 8)&0x0000ff00|(packet[19]<< 0)&0x000000ff;
        connectionCode = (packet[20]<<24)&0xff000000|(packet[21]<<16)&0x00ff0000|(packet[22]<< 8)&0x0000ff00|(packet[23]<< 0)&0x000000ff;
        destPort = (short) ((packet[24]<< 8)&0xff00|(packet[25]<< 0)&0x00ff);
        sourcePort = (short) ((packet[26]<< 8)&0xff00|(packet[27]<< 0)&0x00ff);

        //Handle varying IP Address lengths
        int sourceIPLength = (packet[28]<<24)&0xff000000|(packet[29]<<16)&0x00ff0000|(packet[30]<< 8)&0x0000ff00|(packet[31]<< 0)&0x000000ff;
        int destIPLength = (packet[32]<<24)&0xff000000|(packet[33]<<16)&0x00ff0000|(packet[34]<< 8)&0x0000ff00|(packet[35]<< 0)&0x000000ff;
        sourceIP = new String(Arrays.copyOfRange(packet, 36, 36 + sourceIPLength));
        destIP = new String(Arrays.copyOfRange(packet, 36 + sourceIPLength, 36 + sourceIPLength + destIPLength));

    }
}
