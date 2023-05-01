package ru.kiteiru;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.FileInputStream;
import java.io.IOException;

public class Decompression {
    private final String filename;

    public Decompression(String filename) {
        this.filename = filename;
    }

    public BZip2CompressorInputStream decompress() throws IOException {
        return new BZip2CompressorInputStream(new FileInputStream(filename));
    }

}
