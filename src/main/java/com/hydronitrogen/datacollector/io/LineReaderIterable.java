package com.hydronitrogen.datacollector.io;

import java.io.IOException;
import java.util.Iterator;

import com.google.common.io.LineReader;

/**
 * An iterator wrapper over Guava's LineReader class.
 * @author hkothari
 */
public final class LineReaderIterable implements Iterable<String> {

    private final LineReaderIterator lineReaderIterator;

    public LineReaderIterable(LineReader lineReader) {
        this.lineReaderIterator = new LineReaderIterator(lineReader);
    }

    @Override
    public Iterator<String> iterator() {
        return lineReaderIterator;
    }

    private static class LineReaderIterator implements Iterator<String> {

        private final LineReader lineReader;
        private String lastLine = null;
        private boolean hasNextCalled = false;

        public LineReaderIterator(LineReader lineReader) {
            this.lineReader = lineReader;
        }

        @Override
        public boolean hasNext() {
            if (hasNextCalled) {
                return lastLine != null;
            } else {
                hasNextCalled = true;
                try {
                    lastLine = lineReader.readLine();
                    return lastLine != null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public String next() {
            if (hasNextCalled) {
                hasNextCalled = false;
                return lastLine;
            } else {
                try {
                    return lineReader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void remove() {
            throw new RuntimeException("Cannot remove values from LineReader.");
        }

    }
}
