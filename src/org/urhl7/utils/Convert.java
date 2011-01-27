/*
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

package org.urhl7.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import org.urhl7.igor.*;

/**
 * A utility class that allows for conversion between various data types.
 * Generally DataField/String data types and more usable Object representations such as Date.
 * @author dmorgan
 */
public class Convert {

    /**
     * Precision size to create an HL7 date/time to seconds
     */
    public final static int PRECISION_SECONDS = 14;

    /**
     * Precision size to create an HL7 date/time to minutes
     */
    public final static int PRECISION_MINUTES = 12;

    /**
     * Precision size to create an HL7 date/time to hours
     */
    public final static int PRECISION_HOURS = 10;

    /**
     * Precision size to create an HL7 date/time to the day
     */
    public final static int PRECISION_DAY = 8;

    private final static String DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    private Convert() {}

    /**
     * Creates a Date object from an DataField that is representing a date/time in the standard HL7
     * format. Precision is up to seconds.
     * @param dateDataField the DateField representation of an HL7 date/time
     * @return a Date object instantiated with the specified time
     */
    public static Date toDate(DataField dateDataField) {
        return Convert.toDate(dateDataField.getData());
    }

    /**
     * Creates a Date object from a String that follows the HL7 date/time format (yyyyMMddHHmmss). Precision
     * is up to seconds.
     * @param hl7DateString a String representing an HL7 date/time
     * @return a Date object instantiated with the specified time
     */
    public static Date toDate(String hl7DateString) {
        String formatString = DATE_TIME_FORMAT;
        if(hl7DateString.length() < formatString.length()) {
            formatString = formatString.substring(0, hl7DateString.length());
        }
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.parse(hl7DateString, new ParsePosition(0));
    }

    /**
     * Creates a String that represents the specified Date object in the HL7 date/time format. The
     * default precision is to seconds.
     * @param date a Date object to represent
     * @return a formatted String of provided Date object
     */
    public static String toHL7String(Date date) {
        return Convert.toHL7String(date, Convert.PRECISION_SECONDS);
    }

    /**
     * Creates a String that represents the specified Date object in the HL7 date/time format.
     * @param date a Date object to represent
     * @param precision the precision to create the string. Convert.PRECISION_SECONDS
     * or Convert.PRECISION_MINUTES are most common
     * @return a formatted String of provided Date object
     */
    public static String toHL7String(Date date, int precision) {
        if (precision > PRECISION_SECONDS) {
            precision = PRECISION_SECONDS;
        }
        SimpleDateFormat sfmt = new SimpleDateFormat( DATE_TIME_FORMAT.substring(0,precision) );
        return sfmt.format( date );     
    }
}
