package com.hydronitrogen.datacollector.caching;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Holds on to the files from the SEC so they don't have
 * to be downloaded multiple times, and also so they don't need
 * to remain in memory when being used.
 * @author hkothari
 */
public interface SecFileCacheService {

    /**
     * Returns the file at the given path from the SEC's
     * FTP site using binary transfer mode.
     * @param path the relative path of the file given that we
     * start at ftp.sec.gov
     * @return an inputStream reference to the cached version of the file.
     */
    InputStream getFile(Path path);

    /**
     * Gets the provided file from the SEC's FTP site using
     * the provided filetype from the apache FTP class.
     * @param path the relative path of the file given that we
     * start at ftp.sec.gov
     * @param fileType either FTP.FILE_TYPE_ASCII or FTP.FILE_TYPE_BINARY
     * @return an inputStream reference to the cached version of the file.
     */
    InputStream getFile(Path path, int fileType);

    /**
     * Gets the directory in which the cache resides.
     */
    Path getCacheDir();

}