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

import java.util.*;
import org.urhl7.utils.*;
/**
 * Helper class that has methods that help move around the underlying data structure.
 * @author dmorgan
 */
public class HL7StructureHelper {
    private HL7Structure structure;
    private static final boolean NEVER_RETURN_NULL = true;

    /**
     * Create a HL7StructureHelper that is bound to the provided HL7Structure
     * @param structure the HL7Structure to bind to
     */
    public HL7StructureHelper(HL7Structure structure) {
        this.structure = structure;
    }

    /**
     * Determine if this structure has a particular data field or segment
     * @param descriptor string descriptor of the location of the data field or segment
     * @return if the data field or segment exists
     */
    public boolean has(String descriptor) {
        LocationSpecification ls = LocationParser.parse(descriptor);
        return has(ls);
    }

    /**
     * Determine if this structure has a particular data field or segment
     * @param loc the LocationSpecification of the data field
     * @return if the data field or segment exists
     */
    public boolean has(LocationSpecification loc) {
        if (loc == null) { return false; }
        if (loc.getSegmentName() == null) { return false; }

        //find segment first
        boolean segmentExist = false;
        int segCount = 0;
        for(HL7Segment segment : structure.getSegments()) {
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                if(loc.isSpecifiedSegmentPosition()) {
                    if (loc.getSegmentRepPosition() == -1 || segCount == loc.getSegmentRepPosition()) {
                        segmentExist = true;
                    }
                } else {
                    segmentExist = true;
                }
                segCount++;
            }
        }

        boolean fieldFound = false;
        //look up field now
        if (loc.getFieldPosition() == -1) { //its a segment only
            return segmentExist;
        } else { //lets look for the field.
            if (segmentExist) {

                int pos = 0;
                for(HL7Segment segment : structure.getSegments()) {
                    boolean lookAtThisOne = false;
                    if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                        if(loc.isSpecifiedSegmentPosition()) {
                            if (pos == loc.getSegmentRepPosition()) { lookAtThisOne = true; }
                        } else {
                            lookAtThisOne = true;
                        }
                        pos++;
                    }
                    if (lookAtThisOne) {
                        try {
                            int repFieldIndex = 0;
                            HL7RepeatingField rf = segment.getRepeatingField(loc.getFieldPosition());
                            if(loc.isSpecifiedFieldPosition()) {
                                repFieldIndex = loc.getRepeatingFieldIndex();
                            }

                            HL7Field field = rf.getField(repFieldIndex);

                            if (loc.getComponentPosition() != -1) {
                                HL7FieldComponent fieldComp = field.getFieldComponent(loc.getComponentPosition());

                                if(loc.getSubcomponentPosition() != -1) {
                                    HL7FieldSubcomponent sc = fieldComp.getFieldSubcomponent(loc.getSubcomponentPosition());
                                } else {
                                    fieldFound = true;
                                }

                            } else {
                                fieldFound = true;
                            }

                        } catch (Exception e) { }
                    }
                }
            }
        }

        return fieldFound;
    }

    /**
     * Retrieves the first data field at a specified location. If the data field does not exist, rather than erroring,
     * it will return an EmptyField with no data.
     * @param descriptor String description of location
     * @return the first DataField that matches the descriptor
     */
    public DataField get(String descriptor) {
        LocationSpecification ls = LocationParser.parse(descriptor);
        return get(ls, NEVER_RETURN_NULL);
    }

    /**
     * Retrieves the first data field at a specified location.
     * @param descriptor String description of location
     * @param neverReturnNull flag to return a null object on a not found object (false) or an EmptyField (true)
     * @return the first DataField that matches the descriptor
     */
    public DataField get(String descriptor, boolean neverReturnNull) {
        LocationSpecification ls = LocationParser.parse(descriptor);
        return get(ls, neverReturnNull);
    }
    /**
     * Retrieves the first data field at a specified location. If the data field does not exist, rather than erroring,
     * it will return an EmptyField with no data.
     * @param loc the LocationSpecification of the data field
     * @return the first DataField that matches the descriptor
     */
    public DataField get(LocationSpecification loc) {
        return get(loc, NEVER_RETURN_NULL);
    }

    /**
     * Retrieves the first data field at a specified location.
     * @param loc the LocationSpecification of the data field
     * @param neverReturnNull flag to return a null object on a not found object (false) or an EmptyField (true)
     * @return the first DataField that matches the descriptor
     */
    public DataField get(LocationSpecification loc, boolean neverReturnNull) {
        int pos = 0;
        for(HL7Segment segment : structure.getSegments()) {
            boolean lookAtThisOne = false;
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                if(loc.isSpecifiedSegmentPosition()) {
                    if (pos == loc.getSegmentRepPosition()) { lookAtThisOne = true; }
                } else {
                    lookAtThisOne = true;
                }
                pos++;
            }
            if (lookAtThisOne) {
                try {
                    int repFieldIndex = 0;
                    HL7RepeatingField rf = segment.getRepeatingField(loc.getFieldPosition());
                    if(loc.isSpecifiedFieldPosition()) {
                        repFieldIndex = loc.getRepeatingFieldIndex();
                    }

                    HL7Field field = rf.getField(repFieldIndex);
                    if (loc.getComponentPosition() != -1) {
                        HL7FieldComponent fieldComp = field.getFieldComponent(loc.getComponentPosition()-1);

                        if(loc.getSubcomponentPosition() != -1) {
                            HL7FieldSubcomponent sc = fieldComp.getFieldSubcomponent(loc.getSubcomponentPosition()-1);
                            return sc;
                        } else {
                            return fieldComp;
                        }

                    } else {
                        return field;
                    }

                } catch (Exception e) { /* silent kaboom */ }
            }

        }
        if (neverReturnNull) {
            return new EmptyField();
        } else {
            return null;
        } 
    }

    /**
     * Retrieves the all data fields matching a specified location, left to right, top to bottom. If the data field
     * does not exist, rather than erroring, it will return an empty list.
     * @param descriptor String description of location
     * @return all DataField object that match the descriptor, or an empty list if none do
     */
    public List<DataField> getAll(String descriptor) {
        LocationSpecification ls = LocationParser.parse(descriptor);
        return getAll(ls);
    }

    /**
     * Retrieves the all data fields matching a specified location, left to right, top to bottom. If the data field
     * does not exist, rather than erroring, it will return an empty list.
     * @param loc the LocationSpecification of the data field
     * @return all DataField object that match the location, or an empty list if none do
     */
    public List<DataField> getAll(LocationSpecification loc) { 
        ArrayList<DataField> retList = new ArrayList<DataField>();
        int pos = 0;
        for(HL7Segment segment : structure.getSegments()) {
            boolean lookAtThisOne = false;
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                if(loc.isSpecifiedSegmentPosition()) {
                    if (pos == loc.getSegmentRepPosition()) { lookAtThisOne = true; }
                } else {
                    lookAtThisOne = true;
                }
                pos++;
            }
            if (lookAtThisOne) { //if you care about this segment...
                try {
                    int repFieldIndex = 0;

                    HL7RepeatingField rf = segment.getRepeatingField(loc.getFieldPosition());
                    if(loc.isSpecifiedFieldPosition()) {
                        repFieldIndex = loc.getRepeatingFieldIndex();
                    } 

                    if (loc.isSpecifiedFieldPosition()) {
                        HL7Field field = rf.getField(repFieldIndex);
                        if (loc.getComponentPosition() != -1) {
                            HL7FieldComponent fieldComp = field.getFieldComponent(loc.getComponentPosition()-1);

                            if(loc.getSubcomponentPosition() != -1) {
                                HL7FieldSubcomponent sc = fieldComp.getFieldSubcomponent(loc.getSubcomponentPosition()-1);
                                retList.add(sc);
                            } else {
                                retList.add(fieldComp);
                            }

                        } else {
                            retList.add(field);
                        }
                    } else {
                        for(HL7Field field : rf.getFields()) {
                            if (loc.getComponentPosition() != -1) {
                                HL7FieldComponent fieldComp = field.getFieldComponent(loc.getComponentPosition()-1);

                                if(loc.getSubcomponentPosition() != -1) {
                                    HL7FieldSubcomponent sc = fieldComp.getFieldSubcomponent(loc.getSubcomponentPosition()-1);
                                    retList.add(sc);
                                } else {
                                    retList.add(fieldComp);
                                }

                            } else {
                                retList.add(field);
                            }
                        }
                    }

                } catch (Exception e) {  }
            }
        }

        return retList;
    }

    /**
     * Retrieves the first HL7Segment that matches the descriptor (top to bottom)
     * @param descriptor String description of location
     * @return the first HL7Segment that matches the descriptor
     */
    public HL7Segment getSegment(String descriptor) {
        LocationSpecification ls = LocationParser.parse(descriptor);
        return getSegment(ls);
    }
    /**
     * Retrieves the first HL7Segment that matches the LocationSpecification (top to bottom)
     * @param loc the LocationSpecification of the segment
     * @return the first HL7Segment that matches the LocationSpecification
     */
    public HL7Segment getSegment(LocationSpecification loc) {
        int positionCount = -1;
        for(HL7Segment segment : structure.getSegments()) {
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                positionCount++;
                if (!loc.isSpecifiedSegmentPosition() || (loc.isSpecifiedSegmentPosition() && positionCount == loc.getSegmentRepPosition())) {
                    return segment;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all segments that match the descriptor (from top to bottom)
     * @param descriptor String description of location
     * @return all HL7Segments that match the descriptor
     */
    public List<HL7Segment> getAllSegments(String descriptor) {
        LocationSpecification ls = LocationParser.parse(descriptor);
        return getAllSegments(ls);
    }
    
    /**
     * Retrieves all segments that match the LocationSpecification (from top to bottom)
     * @param loc the LocationSpecification of the segments
     * @return all HL7Segments that match the specification
     */
    public List<HL7Segment> getAllSegments(LocationSpecification loc) {
        ArrayList<HL7Segment> segments = new ArrayList<HL7Segment>();
        int positionCount = -1;
        for(HL7Segment segment : structure.getSegments()) {
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                positionCount++;
                if (!loc.isSpecifiedSegmentPosition() || (loc.isSpecifiedSegmentPosition() && positionCount == loc.getSegmentRepPosition())) {
                    segments.add(segment);
                }
            }
        }
        return segments;
    }

}
