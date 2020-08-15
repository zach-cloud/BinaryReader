package com.github.zachcloud.interfaces;

import com.github.zachcloud.exceptions.ReadingException;

/**
 * Helper class for reading binary files (such as MPQs)
 */
public interface IBinaryReader {

    /**
     * Finds the target String and navigates the read
     * buffer to the found String.
     *
     * @param flag String to go to
     * @throws ReadingException If bytes cannot be read
     */
    void goTo(String flag) throws ReadingException;

    /**
     * Goes to the specified position.
     *
     * @param position Position to go to (byte)
     */
    void position(int position);

    /**
     * Undoes the last operation (shifts position back)
     */
    void undo();

    /**
     * Undoes the last operations (shifts position back)
     *
     * @param count How many operations to undo
     */
    void undo(int count);

    /**
     * Reads specified number of bytes as an array.
     *
     * @param count How many bytes to read
     * @return  Number of read bytes
     * @throws ReadingException If bytes cannot be read
     */
    byte[] readBytes(int count) throws ReadingException;

    /**
     * Reads a number of characters as a String
     *
     * @param length Character count to read
     * @return String data
     * @throws ReadingException If bytes cannot be read
     */
    String readString(int length) throws ReadingException;
    
    /**
     * Reads a String until it finds a null terminator (0x00).
     *
     * @return String data
     * @throws ReadingException If bytes cannot be read
     */
    String readString() throws ReadingException;

    /**
     * Reads a single byte.
     *
     * @return 1 byte
     * @throws ReadingException If bytes cannot be read
     */
    byte readByte() throws ReadingException;

    /**
     * Reads a 8 byte Unsigned Int.
     * Little endian.
     *
     * @return Uin64
     * @throws ReadingException If bytes cannot be read
     */
    long readLong() throws ReadingException;

    /**
     * Reads a 2 byte Unsigned Int.
     * Little endian.
     *
     * @return Uint16
     * @throws ReadingException If bytes cannot be read
     */
    int readShort() throws ReadingException;

    /**
     * Reads a 4 byte Unsigned Int.
     * Little endian.
     *
     * @return Uint with byte size X
     * @throws ReadingException If bytes cannot be read
     */
    int readInt() throws ReadingException;

    /**
     * Reads a 4-byte Real.
     * Little endian.
     *
     * @return Real value
     * @throws ReadingException If bytes cannot be read
     */
    double readReal() throws ReadingException;

    /**
     * Returns current position of reader.
     *
     * @return Position, as int.
     */
    int getPosition();

    /**
     * Returns the size of the buffer.
     *
     * @return  Size of buffer
     */
    int getSize();

    /**
     * Returns bytes of the reader to an array.
     *
     * @return  Bytes reading from
     */
    byte[] getArray();
}
