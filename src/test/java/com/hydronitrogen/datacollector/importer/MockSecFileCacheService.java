package com.hydronitrogen.datacollector.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hydronitrogen.datacollector.caching.SecFileCacheService;

/**
 * Replace me when you're not on an airplane.
 * @author hkothari
 */
public final class MockSecFileCacheService implements SecFileCacheService {

    @Override
    public InputStream getFile(Path path) {
        return getFile(path, 0);
    }

    @Override
    public InputStream getFile(Path path, int fileType) {
        // If we're asking for the idx zip return that otherwise return the temp XBRL file.
        if (path.getFileName().toString().endsWith("zip")) {
            try {
                return new FileInputStream("src/test/resources/company.idx.zip");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public Path getCacheDir() {
        return Paths.get("dontmatter");
    }

}
