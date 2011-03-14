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

import java.util.ArrayList;
import java.util.List;
import org.urhl7.utils.StringHelper;

/**
 * This data object is a representation of an HL7 message. 
 * @author dmorgan
 */
public class HL7Structure implements GenericStructure, DelimitedStructure {
   
    private char[] delimSet;
    private List<HL7Segment> segments;
    private String data;

    private HL7StructureHelper helper = null;
    
    private static final String SEGMENT_SPLIT_STRING =  "\r";
    
    /**
     * Constructor for creating a HL7Structure object. Passing in the delmiting characters provided
     * by the HL7 message will set how this object structure works.
     * @param delims Usually an array of these chars: |^~\&
     */
    public HL7Structure(char[] delims) {
        delimSet = delims;
    }

    /**
     * Returns a HL7StructureHelper for accessing fields that may be needed. This is created once, then reused for this
     * particular structure.
     * @return a HL7StructureHelper object bound to this structure
     */
    public HL7StructureHelper helper() {
        if (helper == null) {
            helper = new HL7StructureHelper(this);
        }
        return helper;
    }

    /**
     * Makes an exact copy of this HL7Structure and returns it. Functionally equivalent as .copy(true)
     * @return a copy of this structure
     * @see #copy(boolean)
     */
    public HL7Structure copy() {
        return this.copy(true);
    }

    /**
     * Makes an exact copy of this HL7Structure and returns it. If retainData is false, it will
     * also remove the data, but leave the structure intact.
     * @param retainData flag to retain the data inside the structure
     * @return a copy of this structure
     */
    public HL7Structure copy(boolean retainData) {
        String thisString = this.marshal();
        HL7Structure newStruct = Igor.structure(thisString);
        if (!retainData) {
            for(HL7Segment seg : newStruct.getSegments()) {
                for(int i=0; i<seg.getRepeatingFields().size(); i++) {
                    if(i!=0) {
                        HL7RepeatingField rf = seg.getRepeatingField(i);
                        for(HL7Field f : rf.getFields()) {
                            if(!f.isBaseField()) {
                                for(HL7FieldComponent cd : f.getFieldComponents()) {
                                    if(!cd.isBaseField()) {
                                        for(HL7FieldSubcomponent fsc : cd.getFieldSubcomponents()) {
                                            fsc.setData("");
                                        }
                                    } else {
                                        cd.setData("");
                                    }
                                }
                            } else {
                                if (!f.marshal().equals( (new String(newStruct.getDelims())).substring(1) )) {
                                    f.setData("");
                                }
                            }
                        }
                    }
                }
            }
        }
        return newStruct;
    }

    /**
     * Tests if the current structure meets the requirements specified by the list of HL7Rules.
     * @param rules list of rules
     * @return true if it meets the requirements, false if it does not.
     */
    public boolean rulesTest(List<HL7Rule> rules) {
        boolean test = true;
        for(HL7Rule rule : rules) {
            if (!ruleTest(rule)) {
                return false;
            }
        }
        return test;
    }

    /**
     * Tests if the current structure meets the requirements specified by the HL7Rule.
     * @param rule the rule to test
     * @return true if it meets the requirements, false if it does not.
     */
    public boolean ruleTest(HL7Rule rule) {
        LocationSpecification loc = rule.getLocationSpecification();
        boolean isSegment = (loc.getRepeatingFieldIndex() == -1) && (loc.getFieldPosition() == -1);
        if (isSegment) {
            switch(rule.getRuleToEnforce()) {
                case EXIST:
                case EXIST_NON_EMPTY:
                    return helper().has(loc);
                default:
                    return false;
            }
        } else {
            //field
            switch(rule.getRuleToEnforce()) {
                case EXIST:
                    return helper().has(loc);
                case EXIST_NON_EMPTY:
                    DataField df = helper().get(loc);
                    return helper().has(loc) && !df.getData().trim().equals("");
                case NUMERIC:
                    return helper().get(loc).getData().matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
                    
                default:
                    return false;
            }
        }
    }



    /**
     * Adds a segment to a particular position in the HL7Structure
     * @param index the position to place the segment
     * @param seg the segment to add
     */
    public void addSegment(int index, HL7Segment seg) {
        seg.setParent(this);
        seg.changeDelims(getDelims());
        segments.add(index, seg);
    }
    
    /**
     * Adds a segment to the end of the HL7Structure
     * @param seg the segment to add
     * @return success of adding the segment
     */
    public boolean addSegment(HL7Segment seg) {
        seg.setParent(this);
        seg.changeDelims(getDelims());
        return segments.add(seg);
    }
    
    /**
     * Removes a particular segment from the HL7Structure
     * @param seg the segment to remove
     * @return the success of removing the segment
     */
    public boolean removeSegment(HL7Segment seg) {
        return segments.remove(seg);
    }
    
    /**
     * Removes the segment at a specific position from the HL7Structure
     * @param pos the position to remove the segment
     * @return the segment that was removed
     */
    public HL7Segment removeSegment(int pos) {
        return segments.remove(pos);
    }
    
    /**
     * Returns the segment at a specific position from the HL7Structure
     * @param pos the position
     * @return the HL7Segment retrieved
     */
    public HL7Segment getSegment(int pos) {
        return segments.get(pos);
    }

    /**
     * Returns the segment position in the HL7Structure
     * @param seg segment reference
     * @return segment position
     */
    public int getSegmentPosition(HL7Segment seg) {
        return segments.indexOf(seg);
    }

    /**
     * Returns a string representation of this and the underlying data structures
     * @return a String representation of this structure
     */
    public String marshal() {
        ArrayList<String> loSegs = new ArrayList<String>();
        
        for(HL7Segment segment : segments) {
            String seg = segment.marshal();
            loSegs.add(seg);
        }
        String[] sSegs = loSegs.toArray(new String[loSegs.size()]);
        String retValue = StringHelper.implode(sSegs, SEGMENT_SPLIT_STRING);
        if (!retValue.endsWith(SEGMENT_SPLIT_STRING)){
            retValue = retValue + SEGMENT_SPLIT_STRING;
        }
        return retValue;
    }
    
    /**
     * Unmarshals an arbitrary String representation of a structure into this data structure
     * @param data a String representation of data
     */
    public void unmarshal(String data) {
        this.data = data;

        segments = new ArrayList<HL7Segment>();
                
        String[] segmentStrings = data.split(SEGMENT_SPLIT_STRING); // HL7 breaker

        for(String segmentString : segmentStrings) {
            HL7Segment segment = new HL7Segment(this);
            segment.unmarshal(segmentString);
            getSegments().add(segment);
        }
    }

    /**
     * Returns an List of the HL7Segment objects that are maintained in this data structure. This value can be null if no 
     * data was unmarshalled.
     * @return A list of HL7Segments
     */
    public List<HL7Segment> getSegments() {
        return segments;
    }

    /**
     * Returns the delimiter set for this data object.
     * @return delimiter array
     */
    public char[] getDelims() {
        return delimSet;
    }
    
    /**
     * Changes the delimiter set for this HL7Structure. By default this will also change the MSH field that 
     * states what the delimiters are. If you do not want to do this, call changeDelims(charDelims, false);
     * @param chars the characters (including field delimiter, ie '|') to set
     */
    public void changeDelims(char[] chars) {
        changeDelims(chars, true);
    }

    /**
     * Changes the delimiter set for this HL7Strucutre. Depending on the changeMSHDelims flag, will either automatically change
     * the data in the MSH segment that specifies the chars, or will not.
     * @param chars the characters (including field delimiter, ie '|') to set.
     * @param changeMSHDelims automatically set the MSH field to the new delimiters
     */
    public void changeDelims(char[] chars, boolean changeMSHDelims) {
        if (changeMSHDelims) {
            try {
                if (helper().has("MSH2")) {
                    HL7Field field = helper().getSegment("MSH").getRepeatingField(1).getField(0);
                    field.unmarshal((new String(chars)).substring(1));
                }
            } catch (Exception e) { } //do nothing if there is an error
        }
        
        delimSet = chars;

        List<HL7Segment> locSegments = getSegments();
        for(HL7Segment seg : locSegments) {
            seg.changeDelims(chars);
        }
    }
    
    
    /**
     * Returns a String representation of this structure
     * @return a String representation
     */
    @Override
    public String toString() {
        return marshal();
    }
    
   

}