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

import org.urhl7.utils.StringHelper;
import java.util.*;

/**
 * A HL7Segment is a representation of a segment in an HL7 message. Every segment is a portion of an HL7 Message
 * and contains fields. 
 * @author dmorgan
 */
public class HL7Segment implements GenericStructure, DelimitedStructure {
    

    private char[] delims;
    //private String segmentName;
    private ArrayList<HL7RepeatingField> fields;
    //private String data;
    private HL7Structure parent;

    private DataField segmentNameDataField = null;

    /**
     * Creates a HL7Segment object that understands the specified delimiters.
     * @param delims
     */
    public HL7Segment(char[] delims) {
        this.delims = delims;
        fields = new ArrayList<HL7RepeatingField>();
    }

    /**
     * Creates a HL7Segment object that understands the specified delimiters of it's parent.
     * @param parent
     */
    public HL7Segment(HL7Structure parent) {
        this.delims = parent.getDelims();
        setParent(parent);
        fields = new ArrayList<HL7RepeatingField>();
    }

    /**
     * Compress the segment by removing fields that are empty at the end of the segment. Does not remove
     * fields that have components.
     */
    public void compress() {
        while(getRepeatingFields().get(getRepeatingFields().size()-1).marshal().equals("")) {
            getRepeatingFields().remove(getRepeatingFields().size()-1);
        }
    }

    /**
     * Makes a copy of this HL7Segment.
     * @return a copy of this HL7Segment
     */
    public HL7Segment copy() {
        HL7Segment seg = new HL7Segment(getDelims());
        seg.unmarshal(marshal());
        return seg;
    }

    /**
     * Retrieves the parent HL7Structure for this HL7Segment
     * @return the parent
     */
    public HL7Structure getParent() {
        return parent;
    }

    /**
     * Sets the parent HL7Structure for this HL7Segment
     * @param parent the parent to set
     */
    public void setParent(HL7Structure parent) {
        this.parent = parent;
        setDirty();
    }


    /**
     * Marshals all underlying data in this structure as a String.
     * @return a String representation of this data.
     */
    public String marshal() {
        ArrayList<String> segfields = new ArrayList<String>();
        
        for(HL7RepeatingField inField : fields) {
            String field = inField.marshal();
            segfields.add(field);
        }
        
        String[] sFields = segfields.toArray(new String[segfields.size()]);
        return StringHelper.implode(sFields, delims[0]+"");
    }
    
    /**
     * Unmarshals an arbitrary String representation of a structure into this data structure
     * @param data a String representation of data
     */
    public void unmarshal(String data) {
        //this.data = data;

        getRepeatingFields().clear();

        String[] quickfields = StringHelper.explode(data, delims[0]+"");
                
        //segmentName = quickfields[0];
        for(String field : quickfields) {
            HL7RepeatingField f = new HL7RepeatingField(this);
            f.unmarshal(field);
            getRepeatingFields().add(f);
        }

        segmentNameDataField = getRepeatingField(0).getField(0);

        setDirty();
    }
    
    /**
     * Provides a String representation of the Segment name of this segment (ie: MSH, PV1, OBX)
     * @return the Segment Name of this segment
     */
    public String getSegmentName() {
        //getRepeatingField(0).getField(0).getData();
        //String segmentName = "";
        //try {
        String segmentName = segmentNameDataField.getData();
        //} catch (Exception e) { segmentName = null; }
        //if (segmentName == null) { segmentName = ""; }
        return segmentName;
    }
    
    /**
     * Sets the segment name of the segment. Also modifies the first field that represents
     * the name of the segment
     * @param segName
     */
    protected void setSegmentName(String segName) {
        //segmentName = segName;
        segmentNameDataField.setData(segName);
    }

    /**
     * Returns a list of all HL7Fields that have been unmarshalled into this structure. This could be null
     * if the data was not unmarshalled
     * @return List of HL7Fields
     */
    public List<HL7RepeatingField> getRepeatingFields() {
        return fields;
    }
    
    /**
     * Adds a field to this HL7Segment
     * @param field the field to add
     * @return the success of the add
     */
    public boolean addRepeatingField(HL7RepeatingField field) {
        field.setParent(this);
        field.changeDelims(getDelims());
        setDirty();
        return fields.add(field);
    }
    
    /**
     * Adds a field to this HL7Segment at specified position
     * @param pos the position to add
     * @param field the field to add
     */
    public void addRepeatingField(int pos, HL7RepeatingField field) {

        field.setParent(this);
        field.changeDelims(getDelims());
        fields.add(pos, field);

        setDirty();
    }

    /**
     * Replaces a repeating field at a specific position with the provided HL7RepeatingField
     * @param pos index to replace at
     * @param field the field to replace with
     * @return the field that was removed
     */
    public HL7RepeatingField setRepeatingField(int pos, HL7RepeatingField field) {

        field.setParent(this);
        field.changeDelims(getDelims());
        HL7RepeatingField old = fields.set(pos, field);
        old.setParent(null);

        //possibly dangerous
        try {segmentNameDataField = getRepeatingField(0).getField(0);
        } catch(Exception e) {segmentNameDataField=null;}

        setDirty();

        return old;
    }
    
    
    /**
     * Removes a specific field from this segment
     * @param field the field to remove
     * @return the success of the remove
     */
    public boolean removeRepeatingField(HL7RepeatingField field) {
        setDirty();
        return fields.remove(field);
    }

    /**
     * Removes a field from this segment at a specific position
     * @param pos the position
     * @return the field removed
     */
    public HL7RepeatingField removeRepeatingField(int pos) {
        setDirty();
        return fields.remove(pos);
    }
    
    /**
     * Returns a specific field from this segment.
     * @param pos the position of the field
     * @return the field at a specific position
     */
    public HL7RepeatingField getRepeatingField(int pos) {
        return fields.get(pos);
    }
    
    /**
     * Changes the internal delimiters for this segment, and all children
     * @param chars the delimiters
     */
    public void changeDelims(char[] chars) {
        delims = chars;
        List<HL7RepeatingField> locFields = getRepeatingFields();
        for(HL7RepeatingField field : locFields) {
            field.changeDelims(chars);
        }
    }
    
    /**
     * Returns a String representation of this segment.
     * @return a string representation
     */
    @Override
    public String toString() {
        return marshal();
    }

    /**
     * Returns the delimiter set for this data object.
     * @return delimiter array
     */
    public char[] getDelims() {
        return delims;
    }


    private void setDirty() {
        if (parent != null) {
            parent.needsRecache = true;
        }
    }
    
}
