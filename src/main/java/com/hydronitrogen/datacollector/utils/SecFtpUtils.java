package com.hydronitrogen.datacollector.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.google.common.base.Preconditions;

/**
 * @author hkothari
 *
 */
public final class SecFtpUtils {

    private SecFtpUtils() {
        // Utility class -- do not instantiate.
    }

    /**
     * Returns a connection to the SEC ftp website, asserting
     * that it is valid.
     * @return the FTPClient for interacting with the SEC FTP site.
     * @throws RuntimeException if there is a problem connecting.
     */
    public static FTPClient getSecConnection() throws RuntimeException {
        FTPClient client = new FTPClient();
        try {
            client.connect("ftp.sec.gov");
            client.login("anonymous", "anonymous");
            assertValidReply(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    /**
     * Reads a file from ftp.sec.gov.
     * @param client the client used for connecting to SEC.
     * @param path the path to the file you want to download.
     * @param fileType the FTP filetype.
     * @return the inputstream corresponding to the file.
     * @throws IllegalArgumentException if the client isn't connected.
     * @throws NullPointerException if the file can't be read.
     */
    public static InputStream readFileFromSec(FTPClient client, Path path, int fileType)
            throws IllegalArgumentException, NullPointerException {
        try {
            Preconditions.checkArgument(client.isConnected(), "The client is not connected.");
            client.setFileType(fileType);
            InputStream inputStream = client.retrieveFileStream(path.toString());
            Preconditions.checkNotNull(inputStream, "Could not open the file for reading.");
            return inputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lists the files in the provided directory.
     * @param client the client used for connecting to the SEC.
     * @param directory
     * @return the list of the files in that directory.
     * @throws IllegalArgumentException if the client isn't connected.
     */
    public static FTPFile[] listFilesInDirectory(FTPClient client, Path directory) throws IllegalArgumentException {
        try {
            Preconditions.checkArgument(client.isConnected(), "The client is not connected.");
            client.enterLocalPassiveMode();
            FTPFile[] files = client.listFiles(directory.toString());
            assertValidReply(client);
            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertValidReply(FTPClient client) {
        int replyCode = client.getReplyCode();
        String replyString = client.getReplyString();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new RuntimeException("Could not complete FTP request.\nReply Code: " + replyCode + "\nReply String:"
                    + replyString);
        }
    }
}
