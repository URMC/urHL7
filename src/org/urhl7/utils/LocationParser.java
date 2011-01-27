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

package org.urhl7.utils;

import org.urhl7.igor.LocationSpecification;

/**
 * LocationParser is a standalone class that provides a method to parse a textual descriptor into a specific location in an HL7 message.
 * @author dmorgan
 */
public class LocationParser {
    private LocationParser() {}

    /**
     * Parses a string into a LocationSpecification data object. The string should follow the format: <br />
     * SEGMENTNAME[SEGMENT_INDEX]FIELDNUMBER[REPEATING_FIELD_INDEX].COMPONENT.SUBCOMPONENT<br />
     * The minimum required entry is the segment name. The following are all valid descriptors:<br />
     * <ul>
     * <li>MSH4</li>
     * <li>PID5.1</li>
     * <li>OBX[0]5</li>
     * <li>OBX5[0]</li>
     * <li>OBX[0]5[0]</li>
     * <li>PID5[0].1</li>
     * <li>ZZZ3.4.1</li>
     * </ul>
     * @param descriptor textual description of the location.
     * @return a complete LocationSpecification.
     */
    public static LocationSpecification parse(String descriptor) {
        LocationSpecification loc = new LocationSpecification();

        //determine segment name
        loc.setSegmentName(descriptor.substring(0, 3));

        //now the field locations
        String nextData = descriptor.substring(3);
        if (!nextData.equals("")) {

            String[] dataChunks = StringHelper.explode(nextData, ".");
            String fieldLocationAndRep = dataChunks[0];

            if (fieldLocationAndRep.contains("[")) {
                if (fieldLocationAndRep.startsWith("[")) { //repeating segment index
                    //get seg index
                    loc.setSpecifiedSegmentPosition(true);
                    String repSegIndex = fieldLocationAndRep.substring(fieldLocationAndRep.indexOf('[')+1, fieldLocationAndRep.indexOf(']'));
                    loc.setSegmentRepPosition(Integer.parseInt(repSegIndex));

                    //get rest?
                    String fieldPositionAndRep = fieldLocationAndRep.substring(fieldLocationAndRep.indexOf(']')+1);
                    if (fieldPositionAndRep.contains("[")) {
                        loc.setSpecifiedFieldPosition(true);
                        
                        String fieldPosition = fieldPositionAndRep.substring(0, fieldPositionAndRep.indexOf('['));
                        if (loc.getSegmentName().equalsIgnoreCase("MSH")) {
                            loc.setFieldPosition(Integer.parseInt(fieldPosition)-1);
                        } else {
                            loc.setFieldPosition(Integer.parseInt(fieldPosition));
                        }

                        //more
                        String repFieldIndex = fieldPositionAndRep.substring(fieldPositionAndRep.indexOf('[')+1, fieldPositionAndRep.indexOf(']'));
                        loc.setRepeatingFieldIndex(Integer.parseInt(repFieldIndex));
                        
                    } else {
                        if (!fieldPositionAndRep.equals("")) {
                            if (loc.getSegmentName().equalsIgnoreCase("MSH")) {
                                loc.setFieldPosition(Integer.parseInt(fieldPositionAndRep)-1);
                            } else {
                                loc.setFieldPosition(Integer.parseInt(fieldPositionAndRep));
                            }
                        }
                    }
                } else {
                    String fieldPos = fieldLocationAndRep.substring(0, fieldLocationAndRep.indexOf('['));
                    if (loc.getSegmentName().equalsIgnoreCase("MSH")) {
                        loc.setFieldPosition(Integer.parseInt(fieldPos)-1);
                    } else {
                        loc.setFieldPosition(Integer.parseInt(fieldPos));
                    }
                    String repPos = fieldLocationAndRep.substring(fieldLocationAndRep.indexOf('[')+1, fieldLocationAndRep.indexOf(']') );
                    loc.setRepeatingFieldIndex(Integer.parseInt(repPos));
                    loc.setSpecifiedFieldPosition(true);
                }
            } else {
                if (loc.getSegmentName().equalsIgnoreCase("MSH")) {
                    loc.setFieldPosition(Integer.parseInt(fieldLocationAndRep)-1);
                } else {
                    loc.setFieldPosition(Integer.parseInt(fieldLocationAndRep));
                }
                loc.setSpecifiedFieldPosition(false);
                loc.setSpecifiedSegmentPosition(false);

            }

            if (dataChunks.length > 1) {
                loc.setComponentPosition(Integer.parseInt(dataChunks[1]));
                if (dataChunks.length > 2) {
                    loc.setSubcomponentPosition(Integer.parseInt(dataChunks[2]));
                }
            }

        }

        return loc;
    }
}
