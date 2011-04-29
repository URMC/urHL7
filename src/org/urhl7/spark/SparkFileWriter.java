/*
 * The MIT License
 *
 * Copyright (c) 2011 David Morgan, University of Rochester Medical Center
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
import java.util.*;
import org.urhl7.igor.*;

/**
 * Allows the writing of HL7Structure messages to a specified file.
 * @author dmorgan
 */
public class SparkFileWriter {
    private File outputFile;
    private String delimiter;
    private boolean appendToFile;

    private FileWriter fw = null;

    /**
     * The default delimiter between messages. The default value is "\r\n"
     */
    public static final String DELIMITER_DEFAULT = "\r\n";


    /**
     * Creates a SparkFileWriter, mapped to a specific File path. Default delimiters are used, and the file will be APPENDED to if it
     * already exists
     * @param outputFilePath the path of file to write data to.
     */
    public SparkFileWriter(String outputFilePath) {
        this(new File(outputFilePath));
    }

    /**
     * Creates a SparkFileWriter, mapped to a specific File. Default delimiters are used, and the file will be APPENDED to if it
     * already exists
     * @param outputFile the file to write data to.
     */
    public SparkFileWriter(File outputFile) {
        this(outputFile, DELIMITER_DEFAULT, true);
    }

    /**
     * Creates a SparkFileWriter, mapped to a specific File path, with a specified delimiter. The file will be APPENDED to if it
     * already exists
     * @param outputFilePath the path of file to write data to.
     * @param delimiter the delimiter that will be placed after every message.
     */
    public SparkFileWriter(String outputFilePath, String delimiter) {
        this(new File(outputFilePath), delimiter);
    }

    /**
     * Creates a SparkFileWriter, mapped to a file, with a specified delimiter. The file will be APPENDED to if it already exists.
     * @param outputFile the file to write data to.
     * @param delimiter the delimiter that will be placed after every message.
     */
    public SparkFileWriter(File outputFile, String delimiter) {
        this(outputFile, delimiter, true);
    }

    /**
     * Creates a SparkFileWriter, mapped to a specific File path. Default delimiters are used. Messages will be appended or
     * will overwrite the file depending on the appendToFile boolean.
     * @param outputFilePath the path of file to write data to.
     * @param appendToFile true to append to the file, false to overwrite the file.
     */
    public SparkFileWriter(String outputFilePath, boolean appendToFile) {
        this(new File(outputFilePath), appendToFile);
    }

    /**
     * Creates a SparkFileWriter, mapped to a file. Messages will be appended or will overwrite the file depending on the appendToFile boolean. Default delimiters will be used.
     * @param outputFile the file to write data to.
     * @param appendToFile true to append to the file, false to overwrite the file.
     */
    public SparkFileWriter(File outputFile, boolean appendToFile) {
        this(outputFile, DELIMITER_DEFAULT, appendToFile);
    }

    /**
     * Creates a SparkFileWriter, mapped to a specific File path, with a specified delimiter, and will append or overwrite the file.
     * @param outputFilePath the path of file to write data to.
     * @param delimiter the delimiter that will be placed after every message.
     * @param appendToFile true to append to the file, false to overwrite the file.
     */
    public SparkFileWriter(String outputFilePath, String delimiter, boolean appendToFile) {
        this(new File(outputFilePath), delimiter, appendToFile);
    }

    /**
     * Creates a SparkFileWriter, mapped to a file. Uses the specified delimiters, and will append or overwrite the file.
     * @param outputFile the file to write data to.
     * @param delimiter the delimiters that are after every message in the file.
     * @param appendToFile true to append to the file, false to overwrite the file.
     */
    public SparkFileWriter(File outputFile, String delimiter, boolean appendToFile) {
        this.outputFile = outputFile;
        this.delimiter = delimiter;
        this.appendToFile = appendToFile;
    }

    /**
     * Writes an HL7Structure message to the file specified, immediately followed by the delimiter.
     * @param message
     * @throws java.io.IOException
     */
    public void write(HL7Structure message) throws IOException {
        if (fw == null) {
            prepFileStreams();
        }
        
        fw.write(message.marshal());
        fw.write(getDelimiter());
        fw.flush();
    }

    /**
     * Writes several HL7Structure messages to the file, each ended with the specified delimiter.
     * @param messages
     * @throws java.io.IOException
     */
    public void writeAll(List<HL7Structure> messages) throws IOException {
        for(HL7Structure message : messages) {
            write(message);
        }
    }

    /**
     * Closes the underlying FileWriter. If you call this method and attempt to write again, you will receive an
     * IOException. Remember to close() when you are finished with your writing to file to avoid locks.
     * @throws java.io.IOException
     */
    public void close() throws IOException {
        if ( fw != null) {
            fw.close();
        }
    }

    /**
     * Returns the output file that is being written to
     * @return the outputFile
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Sets the output file to write to
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
        fw = null;
    }

    /**
     * Returns the delimiters to use after each message.
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the delimiter to use after each message
     * @param delimiter the delimiter to set 
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Will this append or Overwrite file. True appends, False means overwrite
     * @return the appendToFile
     */
    public boolean isAppendToFile() {
        return appendToFile;
    }

    /**
     * Appends or Overwrites file. True appends, False means overwrite
     * @param appendToFile the appendToFile to set
     */
    public void setAppendToFile(boolean appendToFile) {
        this.appendToFile = appendToFile;
        fw = null;

    }

    //reassigns the filewriter
    private void prepFileStreams() throws IOException {
        fw = new FileWriter(getOutputFile(), isAppendToFile());
    }

    /**
     * Ensures that on finalization, the underlying file writer is closed.
     * @throws java.lang.Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();        // close open files
        } finally {
            super.finalize();
        }
    }

}
