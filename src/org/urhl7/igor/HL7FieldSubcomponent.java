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

package org.urhl7.igor;

import org.urhl7.utils.*;

/**
 * HL7FieldSubcomponent is a data object that represents the discrete field subcomponent portions of an HL7 message.
 * @author dmorgan
 */
public class HL7FieldSubcomponent implements DataField, DelimitedStructure {
    private char[] delims;
    private HL7FieldComponent parent;
    private String data;
    private boolean baseField = true;

    /**
     * Creates a HL7FieldSubcomponent object that understands the specified delimiters.
     * @param delims
     */
    public HL7FieldSubcomponent(char[] delims) {
        this.delims = delims;
    }

    /**
     * Creates a HL7FieldSubcomponent object that understands the specified delimiters of the parent, as well as references it's parent.
     * @param parent
     */
    public HL7FieldSubcomponent(HL7FieldComponent parent) {
        this.delims = parent.getDelims();
        setParent(parent);
    }

    /**
     * Sets the parent of this HL7FieldSubcomponent to a HL7FieldComponent
     * @param parent the parent to set
     */
    public void setParent(HL7FieldComponent parent) {
        this.parent = parent;
    }

    /**
     * Returns a reference to the parent HL7FieldComponent for this HL7FieldSubcomponent
     * @return the parent
     */
    public HL7FieldComponent getParent() {
        return parent;
    }

    /**
     * Returns the delimiter set for this data object.
     * @return delimiter array
     */
    public char[] getDelims() {
        return delims;
    }

    public void changeDelims(char[] delims) {
        String tmpData = getData();
        this.delims = delims;

        //FIX BUG: MSH-Delimiter SEGMENT ESCAPING CHARS
        HL7RepeatingField rf = getParent().getParent().getParent();
        HL7Segment s = rf.getParent();
        if(s.getSegmentName().equals("MSH") && s.getRepeatingField(1) == rf) {
            //this is MSH-Delimiter field.. do not setData.
        } else {
            setData(tmpData);
        }
        //ENDFIX
    }

    /**
     * Unmarshals an arbitrary String representation of a structure into this data structure
     * @param data a String representation of data
     */
    public void unmarshal(String data) {
        this.data = data;
        baseField = true;

        setDirty();

    }

    /**
     * Returns a string representation of this and the underlying data structures
     * @return a String representation of this structure
     */
    public String marshal() { 
        return data;
    }

    /**
     * Set the underlying data for this data object.
     * @param data a String representation of the data
     */
    public void setData(String data) {
        baseField = true;
        this.data = HL7Escape.escape(delims, data);
    }

    /**
     * Retrieve the underlying data for this data object.
     * @return String representation of the data
     */
    public String getData() {
        return HL7Escape.unescape(delims, this.data);
    }

    /**
     * Returns a String representation of this field
     * @return a string representation
     */
    @Override
    public String toString() {
        return marshal();
    }

    /**
     * Returns if this field is the base field, or if it has subfields included.
     * @return the baseField
     */
    public boolean isBaseField() {
        return baseField;
    }

    private void setDirty() {
        if (parent != null && parent.getParent() != null
                           && parent.getParent().getParent() != null && parent.getParent().getParent().getParent() != null
                           && parent.getParent().getParent().getParent().getParent() != null) {

            parent.getParent().getParent().getParent().getParent().needsRecache = true;
        }
    }
}
