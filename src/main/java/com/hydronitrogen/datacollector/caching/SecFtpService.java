package com.hydronitrogen.datacollector.caching;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads files from the SEC's FTP site and provides
 * them to the user at request.
 * @author hkothari
 */
public interface SecFtpService {

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
     * Reads the provided path as a a directory and returns a list of files corresponding to it.
     * @param path the path we'd like to read as a directory.
     * @return a list of String file/directory names in the path.
     */
    List<String> getDirectory(Path path);

}