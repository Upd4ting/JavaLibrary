package socketlib;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
public final class DataBuffer {

    private int readID = 0;
    private List<Byte> data = new ArrayList<Byte>();
    
    public void writeByte(byte b) {
            data.add(b);
    }
    public int readByte() {
            return (int) data.get(readID++);
    }
    public void writeByteArray(byte[] data) {
            if (data == null)
                    throw new NullPointerException();
            for (int i = 0; i < data.length; i++) {
                    this.writeByte(data[i]);
            }
    }
    public byte[] readByteArray(int len) {
            byte[] data = new byte[len];
            for (int i = 0; i < len; i++) {
                    data[i] = (byte) this.readByte();
            }
            return data;
    }
    public static DataBuffer setData(byte[] data) {
            DataBuffer db = new DataBuffer();
            for(int i = 0; i < data.length; i++) {
                    db.writeByte(data[i]);
            }
            return db;
    }
    public byte[] getData() {
            byte[] tab = new byte[data.size()];
            int i=0;
            for (Byte b : data) {
                    tab[i] = b;
                    i++;
            }
            return tab;
    }
    /*
     * Byte = 1 OK
     * Short = 2 OK
     * Int = 4 OK
     * Boolean = 1 OK
     * String = N/A OK
     * Long = 8 OK
     * Double = 8 OK
     * Float = 4 OK
     */

    // BOOLEAN

    public boolean readBoolean() {
            return readByte() == 1 ? true : false;
    }

    public void writeBoolean(boolean b) {
            writeByte(b ? (byte)1 : (byte)0);
    }

    // DOUBLE
    public void writeDouble(double value) {
        byte[] data = ByteBuffer.allocate(8).putDouble(value).array();
        writeByteArray(data);
    }

    public double readDouble() {
            byte[] data = readByteArray(8);
        return ByteBuffer.wrap(data).getDouble();
    }

    // SHORT
    public void writeShort(int s) {
             byte[] data = ByteBuffer.allocate(2).putShort((short) s).array();
             writeByteArray(data);
    }
    public short readShort() {
            byte[] data = readByteArray(2);

            return ByteBuffer.wrap(data).getShort();
    }

    // INTEGER
    public void writeInt(int i) {
            byte[] data = ByteBuffer.allocate(4).putInt(i).array();
            writeByteArray(data);
    }
    public int readInt() {
            byte[] data = readByteArray(4);
            return ByteBuffer.wrap(data).getInt();
    }

    // CHAR
    public void writeChar(char c) {
            writeShort((short) c);
    }

    public char readChar() {
            return (char) readShort();
    }

    // STRING
    public void writeString(String w) {
            byte[] b = w.getBytes(StandardCharsets.UTF_8);

            writeInt(b.length);
            writeByteArray(b);
    }
    public String readString() {
            int len = this.readInt();
            return new String(readByteArray(len), StandardCharsets.UTF_8);
    }

    // FLOAT
    public void writeFloat(float value) {  
            byte[] data = ByteBuffer.allocate(4).putFloat(value).array();
            writeByteArray(data);
    }

    public float readFloat() {
            byte[] data = readByteArray(4);
            return ByteBuffer.wrap(data).getFloat();
    }

    // LONG
    public void writeLong (long value) {
            byte[] data = ByteBuffer.allocate(8).putLong(value).array();
            writeByteArray(data);
    }

    public long readLong() {
            byte[] data = readByteArray(8);
            return ByteBuffer.wrap(data).getLong();
    }
}