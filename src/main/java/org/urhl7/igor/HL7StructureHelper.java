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

import java.util.*;


/**
 * Helper class that has methods that help traverse around the underlying data structure.
 * This is the most simple way to access data quickly and easily.
 * @author dmorgan
 */
public class HL7StructureHelper {
    private HL7Structure structure;
    private Map<HL7Location, DataField> _CACHE = null;
    private int cacheingDone = 0;

    /**
     * Create a HL7StructureHelper that is bound to the provided HL7Structure
     * @param structure the HL7Structure to bind to
     */
    public HL7StructureHelper(HL7Structure structure) {
        this.structure = structure;
        this._CACHE = new LinkedHashMap<HL7Location, DataField>();
        //this._KEYCACHE = new LinkedList<HL7Location>();
        refreshCache();
    }


    /**
     * Determine if this structure has a particular data field or segment
     * @param descriptor string descriptor of the location of the data field or segment
     * @return if the data field or segment exists
     */
    public boolean has(String descriptor) {
        return has(HL7Location.parse(descriptor));
    }


    /**
     * Determine if this structure has a particular data field or segment
     * @param loc the HL7Location of the data field
     * @return if the data field or segment exists
     */
    public boolean has(HL7Location loc) { //this is a very... simple way to do it. could be more efficient.
        if (loc.hasSegment() && !loc.hasField()) {
            HL7Segment segment = getSegment(loc);
            if (segment != null) {
                return true;
            }
        } else {
            if (structure.needsRecache) {
                refreshCache();
            }


            if (loc.isFullyQualified()) {
                return _CACHE.containsKey(loc);
            }

            for (Map.Entry<HL7Location, DataField> entry : _CACHE.entrySet()) {
                if (entry.getKey().matches(loc)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Retrieves the first HL7Segment that matches the descriptor (top to bottom)
     * @param descriptor String description of location
     * @return the first HL7Segment that matches the descriptor
     */
    public HL7Segment getSegment(String descriptor) {
        //LocationSpecification ls = LocationParser.parse(descriptor);
        return getSegment(HL7Location.parse(descriptor));
    }

    /**
     * Retrieves the first HL7Segment that matches the HL7Location (top to bottom)
     * @param loc the HL7Location of the segment
     * @return the first HL7Segment that matches the LocationSpecification
     */
    public HL7Segment getSegment(HL7Location loc) {
        int positionCount = -1;
        for(HL7Segment segment : structure.getSegments()) {
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                positionCount++;
                if(loc.getSegmentIndex() == positionCount) {
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
        //LocationSpecification ls = LocationParser.parse(descriptor);
        return getAllSegments(HL7Location.parse(descriptor));
    }

    /**
     * Retrieves all segments that match the HL7Location (from top to bottom)
     * @param loc the HL7Location of the segments
     * @return all HL7Segments that match the specification
     */
    public List<HL7Segment> getAllSegments(HL7Location loc) {
        ArrayList<HL7Segment> segments = new ArrayList<HL7Segment>();
        int positionCount = -1;
        for(HL7Segment segment : structure.getSegments()) {
            if (segment.getSegmentName().equalsIgnoreCase(loc.getSegmentName())) {
                positionCount++;
                if( loc.isSegmentIndexImplied() ) {
                    segments.add(segment);
                } else {
                    if(loc.getSegmentIndex() == positionCount) {
                        segments.add(segment);
                    }
                }
            }
        }
        return segments;
    }

    /**
     * Retrieves the first data field at a specified location. If the data field does not exist, rather than erroring,
     * it will return an NullField with no data.
     * @param descriptor String description of location
     * @return the first DataField that matches the descriptor
     */
    public DataField get(String descriptor) {
        return get(HL7Location.parse(descriptor));
    }

    /**
     * Retrieves the first data field at a specified location. If the data field does not exist, rather than erroring,
     * it will return an NullField with no data.
     * @param loc the HL7Location of the data field
     * @return the first DataField that matches the descriptor
     */
    public DataField get(HL7Location loc) {
        if (structure.needsRecache) {
            refreshCache();
        }


        if (loc.isFullyQualified()) {
            DataField possible = _CACHE.get(loc);
            if (possible != null) {
                return possible;
            }
        }

        for (Map.Entry<HL7Location, DataField> entry : _CACHE.entrySet()) {
            if (entry.getKey().matches(loc)) {
                return entry.getValue();
            }
        }
        
        return new NullField();
    }

    /**
     * Retrieves the all data fields matching a specified location, left to right, top to bottom. If the data field
     * does not exist, rather than erroring, it will return an empty list.
     * @param descriptor String description of location
     * @return all DataField object that match the descriptor, or an empty list if none do
     */
    public List<DataField> getAll(String descriptor) {
        return getAll(HL7Location.parse(descriptor));
    }

    /**
     * Retrieves the all data fields matching a specified location, left to right, top to bottom. If the data field
     * does not exist, rather than erroring, it will return an empty list.
     * @param loc the HL7Location of the data field
     * @return all DataField object that match the location, or an empty list if none do
     */
    public List<DataField> getAll(HL7Location loc) {
        if (structure.needsRecache) {
            refreshCache();
        }
        ArrayList<DataField> listOfFields = new ArrayList<DataField>();

        for (Map.Entry<HL7Location, DataField> entry : _CACHE.entrySet()) {
            if (entry.getKey().matches(loc)) {
                listOfFields.add(entry.getValue());
            }
        }
        
        return listOfFields;
    }


    private void refreshCache() {
        cacheingDone++;
        LinkedHashMap<HL7Location, DataField> table = new LinkedHashMap<HL7Location, DataField>();

        //for(HL7Segment segment : structure.getSegments()) {
        List<HL7Segment> segmentList = structure.getSegments();
        HashMap<String, Integer> mappingIndex = new HashMap<String,Integer>();

        for(int sIdx=0; sIdx<segmentList.size(); sIdx++) {
            HL7Segment segment = segmentList.get(sIdx);
            String segmentName = segment.getSegmentName();
            Integer segmentIndex = mappingIndex.get(segmentName);
            if (segmentIndex == null) {
                mappingIndex.put(segmentName, 0);
                segmentIndex = 0;
            } else {
                segmentIndex++;
                mappingIndex.put(segmentName, segmentIndex);
            }


            List<HL7RepeatingField> repeatingFieldList = segment.getRepeatingFields();
            for(int rfIdx=0; rfIdx<repeatingFieldList.size(); rfIdx++) {
                HL7RepeatingField rf = repeatingFieldList.get(rfIdx);
                    List<HL7Field> fieldList = rf.getFields();
                    for(int fIdx=0; fIdx<fieldList.size(); fIdx++){
                        HL7Field field = fieldList.get(fIdx);

                        //if (field.isBaseField() ){
                            HL7Location loc = new HL7Location(segmentName, segmentIndex, rfIdx, fIdx, -1, -1);
                            table.put(loc, field);
                        //} else {
                        if (!field.isBaseField() ){ //
                            List<HL7FieldComponent> fieldCompList = field.getFieldComponents();
                            for(int fcIdx=0; fcIdx<fieldCompList.size(); fcIdx++) {
                                HL7FieldComponent fieldcomp = fieldCompList.get(fcIdx);
                                //if(fieldcomp.isBaseField() ){
                                    HL7Location locfc = new HL7Location(segmentName, segmentIndex, rfIdx, fIdx, fcIdx, -1);
                                    table.put(locfc, fieldcomp);
                                //} else {
                                if(!fieldcomp.isBaseField() ){ //
                                    List<HL7FieldSubcomponent> fieldSubcompList = fieldcomp.getFieldSubcomponents();
                                    for(int fscIdx=0; fscIdx<fieldSubcompList.size(); fscIdx++) {
                                        HL7FieldSubcomponent fieldsub = fieldSubcompList.get(fscIdx);
                                        if(fieldsub.isBaseField() ){ 
                                            HL7Location locsc = new HL7Location(segmentName, segmentIndex, rfIdx, fIdx, fcIdx, fscIdx);
                                            table.put(locsc, fieldsub);
                                        }
                                    }
                                }
                            }
                        }
                    }
                //}
            }
        }

        _CACHE = table;
        structure.needsRecache = false;
    }

    //public void printCacheUsage() {
    //    //System.out.println("Cache run: " + cacheingDone);
    //}

}
