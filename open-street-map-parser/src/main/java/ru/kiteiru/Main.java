package ru.kiteiru;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Main {
    private static final String FILENAME = "./src/main/resource/RU-NVS.osm.bz2";
    public static void main(String[] args) throws XMLStreamException, IOException {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("Hello World!");

        Decompression file = new Decompression(FILENAME);
        BZip2CompressorInputStream inputStream = file.decompress();

        Parsing parsing = new Parsing(inputStream);
        Parsing.parseXmlFile();

    }
}