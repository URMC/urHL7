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

import java.util.ArrayList;
import java.util.List;
import org.urhl7.utils.StringHelper;

/**
 * The HL7RepeatingField is a data object that represents the one to many relationship between segments and fields.
 * @author dmorgan
 */
public class HL7RepeatingField implements GenericStructure, DelimitedStructure {
    private ArrayList<HL7Field> fields = new ArrayList<HL7Field>();;
    //private String data;
    private char[] delims;
    private HL7Segment parent;

    /**
     * Creates a HL7Field object that understands the specified delimiters.
     * @param delims
     */
    public HL7RepeatingField(char[] delims) {
        this.delims = delims;
    }

    /**
     * Creates a HL7Field object that understands the specified delimiters and parent.
     * @param parent
     */
    public HL7RepeatingField(HL7Segment parent) {
        this.delims = parent.getDelims();
        setParent(parent);
    }

    /**
     * Returns the underlying array list containing all HL7Fields for this structure
     * @return list of HL7Fields
     */
    public List<HL7Field> getFields() {
        return fields;
    }

    /**
     * Returns the HL7Field at the specified position in the underlying data structure
     * @param pos index of the field
     * @return the HL7Field at the index
     */
    public HL7Field getField(int pos) {
        return fields.get(pos);
    }

    /**
     * Adds an HL7Field to the end of the underlying data structure
     * @param field the field to add
     * @return success of the addition
     */
    public boolean addField(HL7Field field) {

        field.setParent(this);
        field.changeDelims(getDelims());
        boolean suc =  fields.add(field);
        //this.data = marshal();

        setDirty();
        return suc;
    }

    /**
     * Adds an HL7Field to the index position of the underlying data structure
     * @param index the position to add to
     * @param field the field to add
     */
    public void addField(int index, HL7Field field) {


        field.setParent(this);
        field.changeDelims(getDelims());
        fields.add(index, field);
        //this.data = marshal();
        setDirty();
    }

    /**
     * Removes the HL7Field at the specified position
     * @param pos the index to remove
     * @return the removed field
     */
    public HL7Field removeField(int pos) {
        HL7Field f = fields.remove(pos);
        f.setParent(null);

        setDirty();

        return f;
    }

    /**
     * Removed the specified field from the underlying data structure
     * @param field the field to remove
     * @return success of the removal
     */
    public boolean removeField(HL7Field field) {


        boolean s = fields.remove(field);
        field.setParent(null);

        setDirty();

        return s;
    }

    /**
     * Replace the field at specified position with the provided field
     * @param pos the index to replace
     * @param field the field to replace with
     * @return the field that was replaced
     */
    public HL7Field setField(int pos, HL7Field field) {
        field.setParent(this);
        field.changeDelims(getDelims());
        HL7Field old = fields.set(pos, field);
        old.setParent(null);

        setDirty();

        return old;
    }

    /**
     * Unmarshals an arbitrary String representation of a structure into this data structure
     * @param data a String representation of data
     */
    public void unmarshal(String data) {
        //this.data = data;


        char[] smallerDelims = new char[delims.length-1];
        for(int i=1; i<delims.length; i++) {
            smallerDelims[i-1] = delims[i];

        }
        String[] quickfields;
        if(!data.equals(new String(smallerDelims))) {
            quickfields = StringHelper.explode(data, delims[2]+"");
        } else {
            quickfields = new String[1];
            quickfields[0] = data;
        }

        fields = new ArrayList<HL7Field>();

        for(String field : quickfields) {
            HL7Field f = new HL7Field(this);
            f.unmarshal(field);
            fields.add(f);
        }

        setDirty();
        
    }

    /**
     * Returns a string representation of this and the underlying data structures
     * @return a String representation of this structure
     */
    public String marshal() {
        ArrayList<String> tfields = new ArrayList<String>();

        for(HL7Field inField : fields) {
            String field = inField.marshal();
            tfields.add(field);
        }

        String[] sFields = tfields.toArray(new String[tfields.size()]);
        return StringHelper.implode(sFields, delims[2]+"");
    }

    /**
     * Sets the HL7Segment provided as the parent for this HL7RepeatingField
     * @param parent the parent to set
     */
    public void setParent(HL7Segment parent) {
        this.parent = parent;
        setDirty();
    }

    /**
     * Returns the HL7Segment that is the parent of this HL7RepeatingField
     * @return the parent HL7Segment
     */
    public HL7Segment getParent() {
        return this.parent;
    }

    /**
     * Returns the delimiter set for this data object.
     * @return delimiter array
     */
    public char[] getDelims() {
        return delims;
    }

    /**
     * Changes the delimiter set for this and all child objects
     * @param delims the delimiter set to use
     */
    public void changeDelims(char[] delims) {
        this.delims = delims;
        for(HL7Field field : fields) {
            field.changeDelims(delims);
        }
    }

    /**
     * Returns a String representation of this repeating field
     * @return a string representation
     */
    @Override
    public String toString() {
        return marshal();
    }

    private void setDirty() {
        if (parent != null && parent.getParent() != null) {
            parent.getParent().needsRecache = true;
        }
    }

/*
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
    @Override
    public boolean equals(Object o) {

        return toString().equals(o.toString());
    }
*/
}
