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

package org.urhl7.igor;

/**
 * Igor is the main class that will begin the creation and using of relevent objects.
 * @author dmorgan
 */
public class Igor {
    private final static char[] DEFAULT_DELIMS = {'|', '^', '~', '\\', '&'};

    private Igor() {}


    /**
     * Creates an empty HL7Structure using default delimiters. MSH|^~\& is defined.
     * @return a blank HL7Structure
     */
    public static HL7Structure structure() {
        return structure(DEFAULT_DELIMS, 0);
    }

    /**
     * Creates an empty HL7Structure using default delimiters. MSH|^~\& is defined, as well as the number of
     * additional fields requested.
     * @param additionalFields number of fields to add to the MSH beyond the segment name and delimiters.
     * @return a blank HL7Structure
     */
    public static HL7Structure structure(int additionalFields) {
        return structure(DEFAULT_DELIMS, additionalFields);
    }

    /**
     * Creates an empty HL7Structure with  MSH[delims] defined.
     * @param delims the delimiters to use for the message.
     * @return an empty HL7Structure
     */
    public static HL7Structure structure(char[] delims) {
        return structure(delims, 0);
    }

    /**
     * Creates an empty HL7Structure with  MSH[delims] defined, as well as the number of
     * additional fields requested.
     * @param delims the delimiters to use for the message.
     * @param additionalFields number of fields to add to the MSH beyond the segment name and delimiters.
     * @return an empty HL7Structure
     */
    public static HL7Structure structure(char[] delims, int additionalFields) {
        HL7Structure structure = new HL7Structure(delims);
        String data = "MSH" + new String(delims) + "\r";
        structure.unmarshal(data);
        if (additionalFields > 0) {
            HL7Segment seg = structure.helper().getSegment("MSH");
            for(int i=0; i<additionalFields; i++) {
                seg.addRepeatingField(Igor.quickField());
            }
        }
        return structure;
    }

    /**
     * Static factory method to return a instantiated HL7Structure object. The object will auto sense what the
     * encoded delimiters are and parse based off that
     * @param hl7data the String of data
     * @return a newly created HL7Structure object
     */
    public static HL7Structure structure(String hl7data) {
        //determine the delimiters
        //System.out.println("\n\ndata recieved: !" + hl7data + "!\n\n");
        //int positionOfSegmentSplit = 3;
        //char sSplitChar = hl7data.charAt(positionOfSegmentSplit);
        //String chars = hl7data.substring(positionOfSegmentSplit, hl7data.indexOf(sSplitChar, positionOfSegmentSplit+1));
        String chars = hl7data.substring(3, 8);
        char[] charDelimiters = chars.toCharArray();

        //build a structure
        HL7Structure structure = new HL7Structure(charDelimiters);

        //load data into HL7 structure
        structure.unmarshal(hl7data);

        return structure;
    }

    /**
     * Creates a new stand-alone abstract HL7 segment, based off the segment name.
     * @param segmentName the name of the segment
     * @return an HL7Segment object
     */
    public static HL7Segment segment(String segmentName) {
        return segment(DEFAULT_DELIMS, segmentName);
    }

    /**
     * Creates a new stand-alone abstract HL7 segment, based off the segment name. Using non-default
     * delimiters.
     * @param delims the delimiter set to use
     * @param segmentName the name of the segment
     * @return an HL7Segment object
     */
    public static HL7Segment segment(char[] delims, String segmentName) {
        HL7Segment segment = new HL7Segment(delims);
        segment.unmarshal(segmentName);
        return segment;
    }

    /**
     * Creates a stand alone HL7Segment that has single cardinality, empty fields attached to it. Using default
     * delimiters.
     * @param segmentName Name of the segment
     * @param fieldCount Number of fields to have in the segment
     * @return a segment with single cardinality repeating fields with blank fields in them
     */
    public static HL7Segment segment(String segmentName, int fieldCount) {
        return segment(DEFAULT_DELIMS, segmentName, fieldCount);
    }

    /**
     * Creates a stand alone HL7Segment that has single cardinality, empty fields attached to it. Using non-default
     * delimiters.
     * @param delims delimiter set to use
     * @param segmentName Name of the segment
     * @param fieldCount Number of fields to have in the segment
     * @return a segment with single cardinality repeating fields with blank fields in them
     */
    public static HL7Segment segment(char[] delims, String segmentName, int fieldCount) {
        HL7Segment segment = segment(delims, segmentName);
        for(int i=0; i<fieldCount; i++) {
            segment.addRepeatingField(quickField());
        }
        return segment;
    }

    /**
     * Creates a stand-alone generic HL7Repeating field.
     * @return a new HL7RepeatingField
     */
    public static HL7RepeatingField repeatingField() {
        return (new HL7RepeatingField(DEFAULT_DELIMS));
    }

    /**
     * Creates a HL7Repeating field with a child field, with no data in it.
     * @return a blank HL7RepeatingField with a blank child HL7Field populated
     */
    public static HL7RepeatingField quickField() { 
        return quickField("");
    }

    /**
     * Creates a HL7Repeating field with a child HL7Field with <i>data</i> populated.
     * @param data the data to populate the underlying HL7Field with
     * @return a HL7RepeatingField with a child HL7Field populated with data
     */
    public static HL7RepeatingField quickField(String data) {
        return quickField(DEFAULT_DELIMS, data);
    }

    /**
     * Creates a HL7Repeating field with a child HL7Field with <i>data</i> populated. Non-default
     * specified delimiters will be used.
     * @param delims delimiter set to use for this structure
     * @param data the data to populate the underlying HL7Field with
     * @return a HL7RepeatingField with a child HL7Field populated with data using non-default delimiters
     */
    public static HL7RepeatingField quickField(char[] delims, String data) {
        HL7RepeatingField repField = repeatingField();
        HL7Field f = field(delims, data);
        repField.addField(f);
        return repField;
    }

    /**
     * Creates a generic, stand-alone, empty HL7Field
     * @return an HL7Field
     */
    public static HL7Field field() {
        return field("");
    }

    /**
     * Creates a generic, stand-alone HL7Field with data populated
     * @param data the data to populate the field with
     * @return a populated HL7Field
     */
    public static HL7Field field(String data) {
        return field(DEFAULT_DELIMS, data);
    }

    /**
     * Creates a stand-alone HL7Field using non-default delimiters, populated with <i>data</i>
     * @param delims delimiter set to use
     * @param data the data to populate the field with
     * @return a populated, custom delimited HL7Field
     */
    public static HL7Field field(char[] delims, String data) {
        HL7Field field = new HL7Field(delims);
        field.setData(data);
        return field;
    }

    /**
     * Creates an empty generic, stand-alone HL7FieldComponent
     * @return an empty generic HL7FieldComponent
     */
    public static HL7FieldComponent component() {
        return component("");
    }

    /**
     * Creates an generic, stand-alone HL7FieldComponent populated with <i>data</i>
     * @param data the data to populate the HL7FieldComponent with
     * @return a populated generic HL7FieldComponent
     */
    public static HL7FieldComponent component(String data) {
        return component(DEFAULT_DELIMS, data);
    }

    /**
     * Creates an generic, stand-alone HL7FieldComponent populated with <i>data</i> using non-default delimiters
     * @param delims the delimiter set to use
     * @param data the data to populate the HL7FieldComponent with
     * @return a populated HL7FieldComponent
     */
    public static HL7FieldComponent component(char[] delims, String data) {
        HL7FieldComponent component = new HL7FieldComponent(delims);
        component.setData(data);
        return component;
    }

    /**
     * Creates an empty generic, stand-alone HL7FieldSubcomponent
     * @return a generic, empty HL7FieldSubcomponent
     */
    public static HL7FieldSubcomponent subcomponent() {
        return subcomponent("");
    }

    /**
     * Creates a populated stand-alone, generic HL7FieldSubcomponent
     * @param data the data to populate with
     * @return an HL7FieldSubcomponent
     */
    public static HL7FieldSubcomponent subcomponent(String data) {
        return subcomponent(DEFAULT_DELIMS, data);
    }

    /**
     * Creates a populated stand-alone, generic HL7FieldSubcomponent using a non-standard delimiter set.
     * @param delims the delimiters to use
     * @param data the data to populate with
     * @return an HL7FieldSubcomponent
     */
    public static HL7FieldSubcomponent subcomponent(char[] delims, String data) {
        HL7FieldSubcomponent sc = new HL7FieldSubcomponent(delims);
        sc.setData(data);
        return sc;
    }
}
