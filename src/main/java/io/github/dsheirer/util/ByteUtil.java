package io.github.dsheirer.util;

/**
 * Utilities for working with bytes and byte arrays
 */
public class ByteUtil
{
    /**
     * Converts a byte array to an integer using big endian format.
     * @param bytes containing four bytes.
     * @param offset into the byte array to start parsing.
     * @return signed integer value.
     */
    public static int toInteger(byte[] bytes, int offset)
    {
        if(bytes == null || bytes.length < (offset + 4))
        {
            throw new IllegalArgumentException("Conversion to integer requires byte array with at least 4 bytes - " +
                    "length:" + bytes.length + " offset:" + offset);
        }

        int value = (bytes[offset + 3] & 0xFF) << 24;
        value += (bytes[offset + 2] & 0xFF) << 16;
        value += (bytes[offset + 1] & 0xFF) << 8;
        value += (bytes[offset] & 0xFF);

        return value;
    }
}
