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

import org.urhl7.utils.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HL7FieldComponent is a data object that represents the discrete field component portions of an HL7 message.
 * @author dmorgan
 */
public class HL7FieldComponent implements DataField, DelimitedStructure {
    private String data;
    private char[] delims;
    private HL7Field parent;
    private ArrayList<HL7FieldSubcomponent> subcomponents = new ArrayList<HL7FieldSubcomponent>();
    private boolean baseField = false;

    /**
     * Creates a HL7FieldComponent object that understands the specified delimiters.
     * @param delims
     */
    public HL7FieldComponent(char[] delims) {
        this.delims = delims;
    }

    /**
     * Creates a HL7FieldComponent object that understands the specified delimiters of the parent, as well as references it's parent.
     * @param parent
     */
    public HL7FieldComponent(HL7Field parent) {
        this.delims = parent.getDelims();
        setParent(parent);
    }

    /**
     * Sets the parent of this HL7FieldComponent to a HL7Field
     * @param parent the parent to set
     */
    public void setParent(HL7Field parent) {
        this.parent = parent;
    }

    /**
     * Returns a reference to the parent HL7Field for this HL7FieldComponent
     * @return the parent
     */
    public HL7Field getParent() {
        return parent;
    }

    /**
     * Returns the delimiter set for this data object.
     * @return delimiter array
     */
    public char[] getDelims() {
        return delims;
    }

    /**
     * Changes the internal delimiters for this field
     * @param delims the delimiters
     */
    public void changeDelims(char[] delims) {
        if (isBaseField()) {
            String tmpData = getData();
            this.delims = delims;
            
            //FIX BUG: MSH-Delimiter SEGMENT ESCAPING CHARS
            HL7RepeatingField rf = getParent().getParent();
            HL7Segment s = rf.getParent();
            if(s.getSegmentName().equals("MSH") && s.getRepeatingField(1) == rf) {
                //this is MSH-Delimiter field.. do not setData.
            } else {
                setData(tmpData);
            }
            //ENDFIX

            //setData(tmpData); //REMOVED FOR BUG FIX
        } else {
            this.delims = delims;
            for(HL7FieldSubcomponent subcomp : subcomponents) {
                subcomp.changeDelims(delims);
            }
        }
    }

    /**
     * Unmarshals an arbitrary String representation of a structure into this data structure
     * @param data a String representation of data
     */
    public void unmarshal(String data) {
        char[] smallerDelims = new char[delims.length-1];
        for(int i=1; i<delims.length; i++) {
            smallerDelims[i-1] = delims[i];
        }
        if (!data.equals(new String(smallerDelims))) {
            String[] brokenup = StringHelper.explode(data, delims[4]+"");
            if (brokenup.length > 1) {
                this.data = data; //maybe not wanted
                subcomponents = new ArrayList<HL7FieldSubcomponent>();
                for(String part : brokenup) {
                    HL7FieldSubcomponent comp = new HL7FieldSubcomponent(this);
                    comp.unmarshal(part);
                    subcomponents.add(comp);
                }
                baseField = false;
            } else {
                this.data = data;
                baseField = true;
            }
        } else {
            this.data = data;
            baseField = true;
        }

        parent.getParent().getParent().getParent().needsRecache = true;
    }

    /**
     * Returns a string representation of this and the underlying data structures
     * @return a String representation of this structure
     */
    public String marshal() {
        if (isBaseField()) {
            return data;
        } else {
            ArrayList<String> tmpfields = new ArrayList<String>();

            for(HL7FieldSubcomponent inField : subcomponents) {
                String field = inField.marshal();
                tmpfields.add(field);
            }

            String[] sFields = tmpfields.toArray(new String[tmpfields.size()]);
            return StringHelper.implode(sFields, delims[4]+"");
        }
    }

    /**
     * Set the underlying data for this data object.
     * @param data a String representation of the data
     */
    public void setData(String data) {
        baseField = true;
        this.data = HL7Escape.escape(delims, data);
        if (subcomponents != null) {
            subcomponents.clear();
        }
    }

    /**
     * Retrieve the underlying data for this data object.
     * @return String representation of the data
     */
    public String getData() {
        return HL7Escape.unescape(delims, this.data);
    }

    /**
     * Returns if this field is the base field, or if it has subfields included.
     * @return the baseField
     */
    public boolean isBaseField() {
        return baseField;
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
     * Returns the underlying array list data structure containing HL7FieldSubcomponents.
     * @return the underlying data structure
     */
    public List<HL7FieldSubcomponent> getFieldSubcomponents() {
        return subcomponents;
    }

    /**
     * Returns a specific HL7FieldSubcomponent based off index
     * @param pos the index to return
     * @return the HL7FieldSubcomponent at specified index
     */
    public HL7FieldSubcomponent getFieldSubcomponent(int pos) {
        return subcomponents.get(pos);
    }

    /**
     * Adds a HL7FieldSubcomponent to the underlying data structure for this HL7FieldComponent
     * @param fieldcomp subcomponent to add
     * @return success of the addition
     */
    public boolean addFieldSubcomponent(HL7FieldSubcomponent fieldcomp) {
        baseField = false;
        fieldcomp.setParent(this);
        fieldcomp.changeDelims(getDelims());
        boolean suc =  subcomponents.add(fieldcomp);
        this.data = marshal();
        return suc;
    }

    /**
     * Adds a HL7FieldSubcomponent to the underlying data structure for this HL7FieldComponent at a specified index
     * @param index the position to add
     * @param fieldsubcomp the subcomponent to add
     */
    public void addFieldSubcomponent(int index, HL7FieldSubcomponent fieldsubcomp) {
        baseField = false;
        fieldsubcomp.setParent(this);
        fieldsubcomp.changeDelims(getDelims());
        subcomponents.add(index, fieldsubcomp);
        this.data = marshal();
    }

    /**
     * Removes the HL7FieldSubcomponent from the underlying data structure
     * @param pos the position to remove from
     * @return the removed HL7FieldSubcomponent
     */
    public HL7FieldSubcomponent removeFieldSubcomponent(int pos) {
        HL7FieldSubcomponent f = subcomponents.remove(pos);
        f.setParent(null);
        if (subcomponents.size() == 0) { baseField = true; }
        this.data = marshal();
        return f;
    }

    /**
     * Removes the HL7FieldSubcomponent from the underlying data structure
     * @param fieldcomp the HL7FieldSubcomponent to remove
     * @return removal success
     */
    public boolean removeFieldSubcomponent(HL7FieldSubcomponent fieldcomp) {
        boolean s = subcomponents.remove(fieldcomp);
        fieldcomp.setParent(null);
        if (subcomponents.size() == 0) { baseField = true; }
        this.data = marshal();
        return s;
    }

    /**
     * Replaces the HL7FieldSubcomponent at the specified position with the provied subcomponent
     * @param pos the index to replace
     * @param fieldsubcomp the subcomponent to replace with
     * @return the previous subcomponent
     */
    public HL7FieldSubcomponent setFieldSubcomponent(int pos, HL7FieldSubcomponent fieldsubcomp) {
        fieldsubcomp.setParent(this);
        fieldsubcomp.changeDelims(getDelims());
        HL7FieldSubcomponent old = subcomponents.set(pos, fieldsubcomp);
        old.setParent(null);
        this.data = marshal();    
        return old;
    }


}
