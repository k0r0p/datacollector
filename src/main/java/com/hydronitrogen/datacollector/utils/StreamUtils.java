package com.hydronitrogen.datacollector.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author hkothari
 */
public final class StreamUtils {

    private static final int BUFFER_SIZE = 1024;

    private StreamUtils() {
        // Utility class -- do not instantiate.
    }

    /**
     * Reads the information from the input stream and writes it into the
     * output stream until there is no more to read. Then both streams are
     * closed.
     * @param input the stream to read from.
     * @param output the stream to write to.
     */
    public static void streamToStream(InputStream input, OutputStream output) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            while (input.read(buffer) != 0) {
                output.write(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }
}
