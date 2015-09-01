/*
 * The MIT License
 *
 * Copyright (c) 2012 David Morgan, University of Rochester Medical Center
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.urhl7.spark;

import java.io.*;
import org.urhl7.igor.Igor;

/**
 * A simple HL7 File reader that will use an event driven model that fires on each parsing of a message in the file.
 * @author dmorgan
 */
public class SparkFileReader {
    private File inputFile;
    private String delimiter;
    private HL7MessageListener listener;
    private int INTERNAL_BUFFER_SIZE = 500;

    /**
     * The default delimiter between messages. The default value is "\r\n"
     */
    public static final String DELIMITER_DEFAULT = "\r\n";

    /**
     * Creates a new SparkFileReader, reading a file at the provided path, with no listener.
     * @param filePath The file to read in
     */
    public SparkFileReader(String filePath) {
        this(new File(filePath));
    }

    /**
     * Creates a new SparkFileReader pointing at a specific file with no listener.
     * @param inputFile The file to read in
     */
    public SparkFileReader(File inputFile) {
        this(inputFile, null, DELIMITER_DEFAULT);
    }

    /**
     * Creates a new SparkFileReader, reading a file at the provided path, with a specified listener.
     * @param filePath The file to read in
     * @param listener the listener to use
     */
    public SparkFileReader(String filePath, HL7MessageListener listener) {
        this(new File(filePath), listener);
    }

    /**
     * Creates a new SparkFileReader pointing at a specific file with a specified listener.
     * @param inputFile the file to read in
     * @param listener the listener to use
     */
    public SparkFileReader(File inputFile, HL7MessageListener listener) {
        this(inputFile, listener, DELIMITER_DEFAULT);
    }

    /**
     * Creates a new SparkFileReader, reading a file at the provided path, with no listener using the specified delimiter.
     * @param filePath The file to read in
     * @param delimiter the delimiter between messages
     */
    public SparkFileReader(String filePath, String delimiter) {
        this(new File(filePath), delimiter);
    }

    /**
     * Creates a new SparkFileReader pointing at a specific file with no listener using the specified delimiter.
     * @param inputFile the file to read in
     * @param delimiter the delimiter between messages
     */
    public SparkFileReader(File inputFile, String delimiter) {
        this(inputFile, null, delimiter);
    }

    /**
     * Creates a new SparkFileReader, reading a file at the provided path, with a listener, and a specified delimiter.
     * @param filePath The file to read in
     * @param listener the listener to use
     * @param delimiter the delimiter between messages
     */
    public SparkFileReader(String filePath, HL7MessageListener listener, String delimiter) {
        this(new File(filePath));
    }

    /**
     * Create a new SparkFileReader pointing a specific file, with a listener, and a specified delimiter.
     * @param inputFile the file to read in
     * @param listener the listener to use
     * @param delimiter the delimiter between messages
     */
    public SparkFileReader(File inputFile, HL7MessageListener listener, String delimiter) {
        this.inputFile = inputFile;
        this.listener = listener;
        this.delimiter = delimiter;
    }

    /**
     * Returns the delimiter that is being searched for between messages in the file.
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the delimiter to look for between messages in the file.
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Returned the HL7MessageListener that is being triggered for this SparkFileReader.
     * @return the listener
     */
    public HL7MessageListener getListener() {
        return listener;
    }

    /**
     * Sets the listener to use for the handling of messages as they come in to the SparkFileReader.
     * @param listener the listener to set
     */
    public void setListener(HL7MessageListener listener) {
        this.listener = listener;
    }

    /**
     * Begins pasring the messages in the file specified. This may throw an IOException and must be handled. The parse function reads in the file,
     * when it finds a delimiter will attempt to parse the message. This message is then sent to the listener specified.
     * @return success of the parsing (if any of the messaceReceived(HL7Structure struct) calls return false, this will as well).
     * @throws java.io.IOException
     */
    public boolean parse() throws java.io.IOException {
        boolean success  = true;
        FileReader fr = new FileReader(inputFile);

        char[] buf = new char[getInternalBufferSize()];
        StringBuilder sb = new StringBuilder();
        String message = "";

        int byteIn = fr.read(buf) ;

        while(byteIn != -1) {

            sb.append(buf, 0, byteIn);

            while (sb.indexOf(delimiter) != -1) {
                message = sb.substring(0, sb.indexOf(delimiter)).toString();
                success = success && listener.messageReceived(Igor.structure(new String(message)));
                if (message.length()+delimiter.length() <= sb.length()) {
                    sb.delete(0, message.length()+delimiter.length());
                } else {
                    sb.delete(0, message.length());
                }
            }
            buf = new char[getInternalBufferSize()];
            byteIn = fr.read(buf);
            
        }
        
        //final cleanup.
        if (!sb.toString().trim().equals("")) {
            message = sb.toString();
            success = success && listener.messageReceived(Igor.structure(new String(message)));
        }

        fr.close();
        return success;
    }

    /**
     * Gets the size of the internal buffer being used.
     * @return the INTERNAL_BUFFER_SIZE
     */
    public int getInternalBufferSize() {
        return INTERNAL_BUFFER_SIZE;
    }

    /**
     * Sets the size of the internal buffer being used.
     * @param internalBufferSize the INTERNAL_BUFFER_SIZE to set
     */
    public void setInternalBufferSize(int internalBufferSize) {
        this.INTERNAL_BUFFER_SIZE = internalBufferSize;
    }

}
