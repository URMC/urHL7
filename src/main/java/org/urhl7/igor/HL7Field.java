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
import java.util.ArrayList;
import java.util.List;

/**
 * HL7Field is a data object that represents the discrete field portions of an HL7 message.
 * 
 * @author dmorgan
 */
public class HL7Field implements DataField, DelimitedStructure {

    private ArrayList<HL7FieldComponent> components = new ArrayList<HL7FieldComponent>();
    private String data;
    private char[] delims;
    private HL7RepeatingField parent;
    private boolean baseField = false;
    private boolean isMSHDelimiterField = false;

    /**
     * Creates a HL7Field object that understands the specified delimiters.
     * @param delims delimiter set to use
     */
    public HL7Field(char[] delims) {
        this.delims = delims;
    }

    /**
     * Creates a HL7Field object that understands the specified delimiters of the parent, as well as references it's parent.
     * @param parent reference to parent
     */
    public HL7Field(HL7RepeatingField parent) {
        this.delims = parent.getDelims();
        setParent(parent);
    }

    /**
     * Returns a reference to the parent HL7RepeatingField for this HL7Field
     * @return the parent
     */
    public HL7RepeatingField getParent() {
        return parent;
    }

    /**
     * Sets the parent of this HL7Field to a HL7RepeatingField
     * @param parent the parent to set
     */
    public void setParent(HL7RepeatingField parent) {
        this.parent = parent;
        setDirty();
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

            for(HL7FieldComponent inField : components) {
                String field = inField.marshal();
                tmpfields.add(field);
            }

            String[] sFields = tmpfields.toArray(new String[tmpfields.size()]);
            return StringHelper.implode(sFields, delims[1]+"");
        }
    }

    /**
     * Unmarshals an arbitrary String representation of a structure into this data structure
     * @param data a String representation of data
     */
    public void unmarshal(String data){
        char[] smallerDelims = new char[delims.length-1];
        for(int i=1; i<delims.length; i++) {
            smallerDelims[i-1] = delims[i];
        }
        if (!data.equals(new String(smallerDelims))) {
            //continue parsing?
            String[] brokenup = StringHelper.explode(data, delims[1]+"");
            if (brokenup.length > 1 || data.contains(delims[4]+"")) {
                this.data = data; //maybe not wanted
                components = new ArrayList<HL7FieldComponent>();
                for(String part : brokenup) {
                    HL7FieldComponent comp = new HL7FieldComponent(this);
                    comp.unmarshal(part);
                    components.add(comp);
                }
                baseField = false;
            } else {
                this.data = data;
                baseField = true;
            }
        } else {
            this.data = data;
            baseField = true;
            isMSHDelimiterField = true;
        }

        setDirty();
    }

    /**
     * Changes the internal delimiters for this field
     * @param chars the delimiters
     */
    public void changeDelims(char[] chars) {
        if (isBaseField()) {

            //BUG - MSH-Delimiter FIELD IS CONSIDERED A SIMPLE HL7Field ONLY WHEN ALL CHARS CHANGE (?)
            if (!isMSHDelimiterField) {
                String tmpData = getData();
                delims = chars;
                setData(tmpData);
            } else {
                delims = chars;
            }
        } else {
            delims = chars;
            for(HL7FieldComponent comp : components) {
                comp.changeDelims(delims);
            }
        }
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
     * Returns the delimiter set for this data object.
     * @return delimiter array
     */
    public char[] getDelims() {
        return delims;
    }

    /**
     * Returns if this field is the base field, or if it has subfields included.
     * @return the baseField
     */
    public boolean isBaseField() {
        return baseField;
    }

    /**
     * Set the underlying data for this data object.
     * @param data a String representation of the data
     */
    public void setData(String data) {
        this.data = HL7Escape.escape(delims, data);
        baseField = true;
        if (components != null) {
            components.clear();
            setDirty();
        }
    }

    /**
     * Retrieve the underlying data for this data object.
     * @return String representation of the data
     */
    public String getData() {
        return HL7Escape.unescape(delims, data);
    }


    /**
     * Return the underlying list structure of all field components associated with this field.
     * @return List of HL7FieldComponents
     */
    public List<HL7FieldComponent> getFieldComponents() {
        return components;
    }

    /**
     * Returns a specific HL7FieldComponent as the provided index for the underlying list structure.
     * @param pos the index you wish you use
     * @return the HL7FieldComponent at this position.
     */
    public HL7FieldComponent getFieldComponent(int pos) {
        return components.get(pos);
    }

    
    /**
     * Adds data as a HL7FieldComponent to the end of the underlying list structure. This is a 
     * shorthand for addFieldComponent(Igor.component(data)).
     * @param data the data to add
     * @return the success of the addition
     */
    public boolean addFieldComponent(String data) {
        return addFieldComponent(Igor.component(data));
    }
    
    /**
     * Adds a HL7FieldComponent to the end of the underlying list structure.
     * @param fieldcomp the HL7FieldComponent to add
     * @return the success of the addition.
     */
    public boolean addFieldComponent(HL7FieldComponent fieldcomp) {
        baseField = false;
        fieldcomp.setParent(this);
        boolean suc = components.add(fieldcomp);
        this.data = marshal();

        setDirty();

        return suc;
    }

    /**
     * Adds a HL7FieldComponent to the underlying list structure at the specified index. Data beyond the specified index will
     * be shifted one position over.
     * @param index the index to insert the HL7FieldComponent at for the underlying data list.
     * @param fieldcomp the HL7FieldComponent to add.
     */
    public void addFieldComponent(int index, HL7FieldComponent fieldcomp) {
        baseField = false;
        fieldcomp.setParent(this);
        components.add(index, fieldcomp);
        this.data = marshal();

        setDirty();
    }

    /**
     * Removes a particular HL7FieldComponent at a particular index position for the underlying list data structure.
     * @param pos the index to remove
     * @return the removed HL7FieldComponent (with parent reference removed)
     */
    public HL7FieldComponent removeFieldComponent(int pos) {
        HL7FieldComponent f = components.remove(pos);
        f.setParent(null);
        if (components.size() == 0) { baseField = true; }
        this.data = marshal();

        setDirty();

        return f;
    }

    /**
     * Remove a particular HL7FieldComponent from the underlying list data structure
     * @param fieldcomp the HL7FieldComponent to remove
     * @return success of the removal (with parent reference removed)
     */
    public boolean removeFieldComponent(HL7FieldComponent fieldcomp) {
        boolean s = components.remove(fieldcomp);
        fieldcomp.setParent(null);
        if (components.size() == 0) { baseField = true; }
        this.data = marshal();

        setDirty();

        return s;
    }

    /**
     * Replaces a HL7FieldComponent at a position with the provided HL7FieldComponent
     * @param pos index to replace
     * @param fieldcomp HL7FieldComponent to replace with
     * @return the previous HL7FieldComponent with parent references removed.
     */
    public HL7FieldComponent setFieldComponent(int pos, HL7FieldComponent fieldcomp) {
        fieldcomp.setParent(this);
        HL7FieldComponent old = components.set(pos, fieldcomp);
        old.setParent(null);
        this.data = marshal();

        setDirty();

        return old;
    }

    private void setDirty() {
        if (parent != null && parent.getParent() != null && parent.getParent().getParent() != null) {
            parent.getParent().getParent().needsRecache = true;
        }
    }
}
