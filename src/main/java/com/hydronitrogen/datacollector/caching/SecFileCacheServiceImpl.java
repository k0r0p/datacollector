package com.hydronitrogen.datacollector.caching;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.hydronitrogen.datacollector.utils.SecFtpUtils;
import com.hydronitrogen.datacollector.utils.StreamUtils;

/**
 * A default implementation of a SecFileCacheService that just
 * uses a temporary directory for each run.
 * @author hkothari
 */
public final class SecFileCacheServiceImpl implements SecFileCacheService  {

    private final Path cacheDir;

    public SecFileCacheServiceImpl() {
        try {
            this.cacheDir = Files.createTempDirectory("secCache");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getFile(Path path) {
        return getFile(path, FTP.BINARY_FILE_TYPE);
    }

    @Override
    public InputStream getFile(Path path, int fileType) {
        Path cacheFilePath = getCacheDir().resolve(path);
        if (!Files.exists(cacheFilePath)) {
            // Download the file to cache dir
            FTPClient client = SecFtpUtils.getSecConnection();
            InputStream fileInStream = SecFtpUtils.readFileFromSec(client, path, fileType);
            try {
                FileOutputStream fileOutStream = new FileOutputStream(cacheFilePath.toFile());
                StreamUtils.streamToStream(fileInStream, fileOutStream);
            } catch (FileNotFoundException e) {
            }
        }
        try {
            return new FileInputStream(cacheFilePath.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getCacheDir() {
        return cacheDir;
    }
}
