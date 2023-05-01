package ru.kiteiru;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Parsing {
    private static final Map<String, Integer> editsByUser = new HashMap<>();
    private static final Map<String, Integer> uniqueKeys = new HashMap<>();

    private static final String USER_ATTRIBUTE = "user";
    private static final String NODE_ELEM = "node";
    private static final String TAG_ELEM = "tag";
    private static final String KEY_ATTRIBUTE = "k";

    private static final String USER_HEADER = "USER";
    private static final String EDIT_NUM_HEADER = "EDIT";
    private static final String KEY_HEADER = "KEY";
    private static final String KEY_NUM_HEADER = "NUM";

    private static final String EDITS_BY_USER_CSV = "./src/main/result/edits_by_user.csv";
    private static final String UNIQUE_KEYS_CSV = "./src/main/result/unique_keys.csv";

    private static XMLStreamReader xmlReader = null;

    public Parsing(BZip2CompressorInputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        xmlReader = factory.createXMLStreamReader(inputStream);
    }

    public static void parseXmlFile() throws XMLStreamException {
        String startElemName;
        String endElemName = null;

        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                startElemName = xmlReader.getLocalName();
                if (startElemName.equals(NODE_ELEM)) {
                    String user = xmlReader.getAttributeValue(null, USER_ATTRIBUTE);
                    incrementMapValue(editsByUser, user);
                    endElemName = NODE_ELEM;
                } else if (startElemName.equals(TAG_ELEM) && endElemName != null) {
                    String key = xmlReader.getAttributeValue(null, KEY_ATTRIBUTE);
                    incrementMapValue(uniqueKeys, key);
                } else {
                    endElemName = null;
                }
            }
        }
        Map<String, Integer> sortedEditsByUser = sortMap(editsByUser);
        Map<String, Integer> sortedUniqueKeys = sortMap(uniqueKeys);

        writeResultInCsv(sortedEditsByUser, EDITS_BY_USER_CSV, USER_HEADER, EDIT_NUM_HEADER);
        writeResultInCsv(sortedUniqueKeys, UNIQUE_KEYS_CSV, KEY_HEADER, KEY_NUM_HEADER);

        xmlReader.close();
    }

    private static void incrementMapValue(Map<String, Integer> map, String key) {
        if(map.containsKey(key))
            map.computeIfPresent(key, (k, v) -> v + 1);
        else
            map.put(key, 1);
    }

    private static Map<String, Integer> sortMap(Map<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (entry1, entry2) -> entry2, LinkedHashMap::new));
    }

    private static void writeResultInCsv(Map<String, Integer> map, String csvName, String header1, String header2) {
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter(csvName)) {
            writer.append(header1)
                    .append(',')
                    .append(header2)
                    .append(eol);
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                writer.append(entry.getKey())
                        .append(',')
                        .append(Integer.toString(entry.getValue()))
                        .append(eol);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
