package com.hydronitrogen.datacollector.caching;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.hydronitrogen.datacollector.utils.SecFtpUtils;
import com.hydronitrogen.datacollector.utils.StreamUtils;

/**
 * A default implementation of a SecFtpService that just
 * holds on to the files from the SEC so they don't have
 * to be downloaded multiple times, and also so they don't need
 * to remain in memory when being used.
 * @author hkothari
 */
public final class SecFtpCacheServiceImpl implements SecFtpService  {

    private static final String DIRECTORY_LIST_FILE = ".directory_list.json";

    private final Path cacheDir;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecFtpCacheServiceImpl() {
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
    public synchronized InputStream getFile(Path path, int fileType) {
        Path cacheFilePath = getCacheDir().resolve(path);
        ensureDirectoryCreated(cacheFilePath.getParent());
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
    public synchronized List<String> getDirectory(Path path) {
        ensureDirectoryCreated(path);
        Path cacheDirectoryListFilePath = getCacheDir().resolve(path).resolve(DIRECTORY_LIST_FILE);
        if (!Files.exists(cacheDirectoryListFilePath)) {
            // Read the directory list
            FTPClient client = SecFtpUtils.getSecConnection();
            FTPFile[] files = SecFtpUtils.listFilesInDirectory(client, path);
            List<String> filesList = Lists.newArrayList();
            for (FTPFile ftpFile : files) {
                filesList.add(ftpFile.getName());
            }
            try {
                objectMapper.writeValue(new FileOutputStream(cacheDirectoryListFilePath.toFile()), filesList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return filesList;
        } else {
            TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
            try {
                return objectMapper.readValue(new FileInputStream(cacheDirectoryListFilePath.toFile()), typeRef);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Path getCacheDir() {
        return cacheDir;
    }

    private void ensureDirectoryCreated(Path directory) {
        Path toCreate = getCacheDir().resolve(directory);
        try {
            Files.createDirectories(toCreate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
