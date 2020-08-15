package com.github.zachcloud.reader;

import com.github.zachcloud.exceptions.ReadingException;
import com.github.zachcloud.interfaces.IBinaryReader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for reading binary files (such as MPQs)
 */
public final class BinaryReader implements IBinaryReader {

    private ByteOrder byteOrder;
    private ByteBuffer buffer;
    private List<Integer> undoStack;
    private int lastReadBytes = 0;

    /**
     * Makes a new binary reader with file contents
     * Reads the file from disk
     *
     * @param origin    File to read bytes from
     * @param byteOrder Byte order (little/big endian)
     */
    public BinaryReader(File origin, ByteOrder byteOrder) {
        try {
            byte[] fileData = IOUtils.toByteArray(new FileInputStream(origin));
            initialize(fileData, byteOrder);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read file: " + origin.getAbsolutePath());
        }
    }

    /**
     * Makes a new binary reader with byte contents
     *
     * @param source    Source bytes
     * @param byteOrder Byte order (little/big endian)
     */
    public BinaryReader(byte[] source, ByteOrder byteOrder) {
        initialize(source, byteOrder);
    }

    /**
     * Internal initialization of state
     *
     * @param source    Source bytes
     * @param byteOrder Byte order (little/big endian)
     */
    private void initialize(byte[] source, ByteOrder byteOrder) {
        buffer = ByteBuffer.allocate(source.length);
        buffer.put(source);
        buffer.order(byteOrder);
        // Ignore this warning. It's not redundant. Code in JAR form fails without cast!!
        ((Buffer) buffer).flip();
        this.undoStack = new LinkedList<>();
        this.byteOrder = byteOrder;
    }

    /**
     * Makes a new binary reader with byte contents
     * Assumes little endian
     *
     * @param source    Source bytes
     */
    public BinaryReader(byte[] source) {
        initialize(source, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Makes a new binary reader with file contents
     * Assumes little endian
     *
     * @param origin File to set for reader
     */
    public BinaryReader(File origin) {
        this(origin, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Finds the target String and navigates the read
     * buffer to the found String.
     *
     * @param flag String to go to
     * @throws ReadingException If bytes cannot be read
     */
    public void goTo(String flag) throws ReadingException {
        int size = flag.length();
        byte currentByte;
        byte currentLetterByte;
        int currentLetter = 0;
        while (currentLetter != size) {
            currentByte = readByte();
            currentLetterByte = (byte) flag.charAt(currentLetter);
            if (currentByte == currentLetterByte) {
                currentLetter++;
            } else {
                if (currentLetter > 0) {
                    for (int i = 0; i < currentLetter; i++) {
                        // Correctly handle undo stack.
                        undo();
                    }
                }
                currentLetter = 0;
            }
        }
        for (int i = 0; i < currentLetter; i++) {
            // Correctly handle undo stack.
            undo();
        }
    }

    /**
     * Goes to the specified position.
     *
     * @param position Position to go to (byte)
     */
    public void position(int position) {
        ((Buffer) buffer).position(position);
    }

    /**
     * Undoes the last operation (shifts position back)
     */
    public void undo() {
        if (undoStack.size() == 0) {
            throw new RuntimeException("Attempted to undo a non-existent operation");
        }
        int adjustment = undoStack.remove(undoStack.size() - 1);
        ((Buffer) buffer).position(((Buffer) buffer).position() - adjustment);
    }

    /**
     * Undoes the last operations (shifts position back)
     *
     * @param count How many operations to undo
     */
    public void undo(int count) {
        for(int i = 0; i < count; i++) {
            undo();
        }
    }

    /**
     * Adds the last operation to stack
     * Resets byte count of last operation
     */
    private void adjustStack() {
        if (lastReadBytes != 0) {
            undoStack.add(lastReadBytes);
            if (undoStack.size() > 100) {
                undoStack.remove(0);
            }
        }
        lastReadBytes = 0;
    }

    /**
     * Reads a number of bytes.
     *
     * @param count How many bytes to read
     * @return Byte array of read bytes
     * @throws ReadingException If reading fails.
     */
    private byte[] readBytesInternal(int count) throws ReadingException {
        byte[] collected = new byte[count];
        for (int i = 0; i < count; i++) {
            collected[i] = readByteInternal();
        }
        return collected;

    }

    /**
     * Reads specified number of bytes as an array.
     *
     * @param count How many bytes to read
     * @return  Number of read bytes
     * @throws ReadingException If bytes cannot be read
     */
    public byte[] readBytes(int count) throws ReadingException {
        adjustStack();
        return readBytesInternal(count);
    }

    /**
     * Reads a number of characters as a String
     *
     * @param length Character count to read
     * @return String data
     * @throws ReadingException If bytes cannot be read
     */
    public String readString(int length) throws ReadingException {
        //System.out.println("Reading string (" + length + ")");
        adjustStack();
        byte[] collected = readBytesInternal(length);
        return new String(collected);
    }

    /**
     * Reads a String until it finds a null terminator (0x00).
     *
     * @return String data
     * @throws ReadingException If bytes cannot be read
     */
    public String readString() throws ReadingException {
        //System.out.println("Reading string (until 0x00)");
        adjustStack();
        List<Byte> bytes = new ArrayList<>();
        byte current = 0;
        do {
            current = readByteInternal();
            if (current != 0x00) {
                bytes.add(current);
            }
        } while (current != 0x00);
        byte[] bytesArray = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bytesArray[i] = bytes.get(i);
        }
        return new String(bytesArray);
    }

    /**
     * Reads a single byte.
     * For internal use, increments the read bytes count
     *
     * @return 1 byte
     * @throws ReadingException If bytes cannot be read
     */
    private byte readByteInternal() throws ReadingException {
        lastReadBytes++;
        byte b = buffer.get();
        return b;
    }

    /**
     * Reads a single byte.
     *
     * @return 1 byte
     * @throws ReadingException If bytes cannot be read
     */
    public byte readByte() throws ReadingException {
        adjustStack();
        return readByteInternal();
    }

    /**
     * Reads a 8 byte Unsigned Int.
     * Little endian.
     *
     * @return Uin64
     * @throws ReadingException If bytes cannot be read
     */
    public long readLong() throws ReadingException {
        adjustStack();
        byte[] collected = readBytesInternal(8);
        return java.nio.ByteBuffer.wrap(collected).order(byteOrder).getLong();
    }

    /**
     * Reads a 2 byte Unsigned Int.
     * Little endian.
     *
     * @return Uint16
     * @throws ReadingException If bytes cannot be read
     */
    public int readShort() throws ReadingException {
        adjustStack();
        byte[] collected = readBytesInternal(2);
        return java.nio.ByteBuffer.wrap(collected).order(byteOrder).getShort();
    }

    /**
     * Reads a 4 byte Unsigned Int.
     * Little endian.
     *
     * @return Uint with byte size X
     * @throws ReadingException If bytes cannot be read
     */
    public int readInt() throws ReadingException {
        adjustStack();
        byte[] collected = readBytesInternal(4);
        return java.nio.ByteBuffer.wrap(collected).order(byteOrder).getInt();
    }

    /**
     * Reads a 4-byte Real.
     * Little endian.
     *
     * @return Real value
     * @throws ReadingException If bytes cannot be read
     */
    public double readReal() throws ReadingException {
        adjustStack();
        byte[] collected = new byte[4];
        for (int i = 0; i < 4; i++) {
            collected[i] = readByteInternal();
        }
        return ByteBuffer.wrap(collected).order(byteOrder).getFloat();
    }

    /**
     * Returns current position of reader.
     *
     * @return Position, as int.
     */
    public int getPosition() {
        return ((Buffer) buffer).position();
    }

    /**
     * Returns the size of the buffer.
     *
     * @return  Size of buffer
     */
    public int getSize() {
        return buffer.array().length;
    }

    /**
     * Returns bytes of the reader to an array.
     *
     * @return  Bytes reading from
     */
    public byte[] getArray() {
        return buffer.array();
    }
}
