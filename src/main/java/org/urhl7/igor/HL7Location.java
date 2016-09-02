/*
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
import java.util.*;

/**
 *
 * @author dmorgan
 */
public class HL7Location {
    private HL7Location() {}

    /* underlying data */
    private String segmentName = null;
    private int segmentIndex = 0;
    private int repeatingFieldIndexInSegment = 0;
    private int fieldIndexInRepeatingField = 0;
    private int componentIndexInField = -1;
    private int subcomponentIndexInComponent = -1;
    private boolean hasSegment = false;
    private boolean hasField = false;
    private boolean hasComponent = false;
    private boolean hasSubcomponent = false;

    private boolean isSegmentIndexImplied = false;
    private boolean isFieldIndexImplied = false;


    //kept for debugging purposes
    private void report() {
        System.out.println("Location breakdown: ");
        System.out.println("\tSegment Name: " + segmentName);
        System.out.println("\tSegment Index: " + segmentIndex);
        System.out.println("\tRepeatingFieldIndexInSegment: " + repeatingFieldIndexInSegment);
        System.out.println("\tFieldIndexInRepeatingField: " + fieldIndexInRepeatingField);
        System.out.println("\tComponentIndexInField: " + componentIndexInField);
        System.out.println("\tSubComponentIndexInComponent: " + subcomponentIndexInComponent);
        System.out.println("-------------------------------------------------------");
        System.out.println("Has Segment?: " + hasSegment());
        System.out.println("Has Field?: " + hasField());
        System.out.println("Has Component?: " + hasComponent());
        System.out.println("Has Subcomponent?: " + hasSubcomponent());
        System.out.println("-------------------------------------------------------");
        System.out.println("Segment Index Implied?: " + isSegmentIndexImplied);
        System.out.println("Field Index Implied?: " + isFieldIndexImplied);
        System.out.println("-------------------------------------------------------");
        System.out.println("Short Location: " + getShortHL7Location());
        System.out.println("Location: " + getHL7Location());
        System.out.println("Full Location: " + getFullyQualifiedHL7Location());
    }

    /**
     * Creates a HL7Location with the provided information. Generally these should not be generated manually.
     *
     * @param segmentName Name of the segment
     * @param segmentIndex Instance number of segment with that name in message
     * @param repFieldIndex Field position in segment
     * @param fieldIndex Index of Field in repeating field, which is in segment
     * @param componentIndex Index of the component of a field
     * @param subcomponentIndex Index of the subcomponent of a component
     */
    public HL7Location(String segmentName, int segmentIndex, int repFieldIndex, int fieldIndex, int componentIndex, int subcomponentIndex) {
        this.segmentName = segmentName;
        this.segmentIndex = segmentIndex;

        this.hasSegment = true;
        this.isSegmentIndexImplied = false;

        if (repFieldIndex >= 0) {
            this.repeatingFieldIndexInSegment = repFieldIndex;
            this.fieldIndexInRepeatingField = fieldIndex;

            this.hasField = true;
            this.isFieldIndexImplied = false;

            if (componentIndex >= 0) {
                this.componentIndexInField = componentIndex;
                this.hasComponent = true;

                if(subcomponentIndex >= 0) {
                    this.subcomponentIndexInComponent = subcomponentIndex;
                    this.hasSubcomponent = true;
                }
            }
        }
    }

    /**
     * Expresses if this HL7Location is fully qualified with no ambiguity (no implied fields)
     * @return true if no indexes are implied
     */
    public boolean isFullyQualified() {
        return ((!isSegmentIndexImplied) && (!isFieldIndexImplied));
    }

    /**
     * Tests if an HL7Location is the same specific location as this location
     * @param o the HL7Location
     * @return true if o matches this HL7Location
     */
    @Override
    public boolean equals(Object o) {
        boolean areWeEqual = true;
        if (o instanceof HL7Location) {
            HL7Location other = (HL7Location)o;


            areWeEqual = areWeEqual && this.segmentName.equals(other.segmentName);
            if (areWeEqual) {
                areWeEqual = areWeEqual && (this.segmentIndex == other.segmentIndex);
                areWeEqual = areWeEqual && (this.isSegmentIndexImplied() == other.isSegmentIndexImplied());
                if (areWeEqual) {
                    areWeEqual = areWeEqual && (this.repeatingFieldIndexInSegment == other.repeatingFieldIndexInSegment);
                    if (areWeEqual) {
                        areWeEqual = areWeEqual && (this.fieldIndexInRepeatingField == other.fieldIndexInRepeatingField);
                        areWeEqual = areWeEqual && (this.isFieldIndexImplied() == other.isFieldIndexImplied());
                        if (areWeEqual) {
                            areWeEqual = areWeEqual && (this.componentIndexInField == other.componentIndexInField);
                            if (areWeEqual) {
                                areWeEqual = areWeEqual && (this.subcomponentIndexInComponent == other.subcomponentIndexInComponent);
                            }
                        }
                    }
                }
            }
        }
        return areWeEqual;
    }

    /**
     * Predictably hashes this HL7Location
     * @return returns a hashcode for this object
     */
    @Override
    public int hashCode() {
        return (segmentIndex*1000)+(fieldIndexInRepeatingField+(repeatingFieldIndexInSegment*100))+(componentIndexInField+10)+subcomponentIndexInComponent;
    }

    /**
     * This object in String form
     * @return the string provided by getFullQualifiedHL7Location()
     */
    @Override
    public String toString() {
        return this.getFullyQualifiedHL7Location();
    }

    /**
     * Does the loc specified (perhaps more generic) match this location (more specific). This allows a PID-3 to match PID[0]-3 AND PID[1]-3.
     * @param loc the location to test against
     * @return if loc matches
     */
    public boolean matches(HL7Location loc) {
        boolean matchSoFar = loc.getSegmentName().equals(this.getSegmentName());
        matchSoFar = matchSoFar && (this.hasField == loc.hasField);
        matchSoFar = matchSoFar && (this.hasComponent == loc.hasComponent);
        matchSoFar = matchSoFar && (this.hasSubcomponent == loc.hasSubcomponent);

        if(matchSoFar) {
            if(loc.isSegmentIndexImplied || this.isSegmentIndexImplied) {
                
            } else {
                matchSoFar = (loc.segmentIndex == this.segmentIndex);
            }
            
            if (matchSoFar) {
                if (loc.hasField) {
                    matchSoFar = (this.repeatingFieldIndexInSegment == loc.repeatingFieldIndexInSegment);

                    if (matchSoFar) {
                        if (loc.isFieldIndexImplied || this.isFieldIndexImplied) {

                        } else {
                            matchSoFar = (loc.fieldIndexInRepeatingField == this.fieldIndexInRepeatingField);
                        }

                        if (matchSoFar && loc.hasComponent) {
                            matchSoFar = (loc.componentIndexInField == this.componentIndexInField);

                            if (matchSoFar && loc.hasSubcomponent) {
                                matchSoFar = (loc.subcomponentIndexInComponent == this.subcomponentIndexInComponent);
                            }
                        }
                    }
                }
            }
            
        }
        return matchSoFar;
    }


    /**
     * Parses a plain text string into a HL7Location. Examples of valid strings are:
     * <ul>
     * <li>PID-3</li>
     * <li>PID-3.1</li>
     * <li>OBX[2]-5</li>
     * </ul>
     * <br />
     * You can omit the hyphen, for segments that are properly formatted with a 3-character segment name
     * @param location A String location representation
     * @return A prepared HL7Location as described by location.
     */
    public static HL7Location parse(String location) {

        try {
            HL7Location loc = new HL7Location();
            if(location.contains("-")) {
                String segDetails = location.substring(0, location.indexOf("-"));
                String fieldDetails = location.substring(location.indexOf("-")+1);
                
                //segment information
                loc.hasSegment = true;
                if (segDetails.contains("[") && segDetails.contains("]")) {
                    loc.segmentName = segDetails.substring(0, segDetails.indexOf("["));
                    loc.segmentIndex = Integer.parseInt(segDetails.substring(segDetails.indexOf("[")+1, segDetails.indexOf("]")));
                } else {
                    loc.isSegmentIndexImplied = true;
                    loc.segmentName = segDetails;
                    loc.segmentIndex = 0;
                }

                //field information
                String[] fieldPieces = StringHelper.explode(fieldDetails, ".");
                if (fieldPieces.length > 0) {
                    loc.hasField = true;
                    String fieldInfo = fieldPieces[0];

                    int repFieldPositionProvided = 0;
                    if(fieldInfo.contains("[") && fieldInfo.contains("]")) {
                        repFieldPositionProvided = Integer.parseInt(fieldInfo.substring(0, fieldInfo.indexOf("[")));
                        loc.fieldIndexInRepeatingField = Integer.parseInt(fieldInfo.substring(fieldInfo.indexOf("[")+1, fieldInfo.indexOf("]")));
                    } else {
                        loc.isFieldIndexImplied = true;
                        loc.fieldIndexInRepeatingField = 0;
                        repFieldPositionProvided = Integer.parseInt(fieldPieces[0]);
                    }

                    if (loc.segmentName.equals("MSH")) {
                        if (repFieldPositionProvided > 1) {
                            loc.repeatingFieldIndexInSegment = repFieldPositionProvided-1;
                        } else {
                            loc.repeatingFieldIndexInSegment = repFieldPositionProvided;
                        }
                    } else {
                        loc.repeatingFieldIndexInSegment = repFieldPositionProvided;
                    }
                }
                if (fieldPieces.length > 1) {
                    if(Integer.parseInt(fieldPieces[1]) >= 1) {
                        loc.hasComponent = true;
                        loc.componentIndexInField = Integer.parseInt(fieldPieces[1])-1;
                    }
                }
                if (fieldPieces.length > 2) {
                    if(Integer.parseInt(fieldPieces[2]) >= 1) {
                        loc.hasSubcomponent = true;
                        loc.subcomponentIndexInComponent = Integer.parseInt(fieldPieces[2])-1;
                    }
                }

            } else {
                if(location.length() <= 3) { //just a segmentName
                    loc.hasSegment = true;
                    loc.segmentName = location;
                    loc.isSegmentIndexImplied = true;
                    loc.segmentIndex = 0;
                } else { //segmentName plus other stuff
                    if (location.charAt(3) == '[') { //segment and index implied
                        if (location.indexOf("]")+1 == location.length()) {
                            loc.hasSegment = true;
                            loc.segmentName = location.substring(0, location.indexOf("["));
                            loc.segmentIndex = Integer.parseInt(location.substring(location.indexOf("[")+1, location.indexOf("]")));
                        } else { //index, add a hyphen and try again
                            StringBuffer sb = new StringBuffer(location);
                            sb.insert(location.indexOf("]")+1, '-');
                            return HL7Location.parse(sb.toString());
                        }
                    } else { //no index, add a hyphen and try again
                        StringBuffer sb = new StringBuffer(location);
                        sb.insert(3, '-');
                        return HL7Location.parse(sb.toString());
                    }
                }

            }

            return loc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid HL7 location: " + location);
        }
    }

    /**
     * Determines the specific HL7Location of an arbitrary GenericStructure object in an HL7Structure.
     * @param gs The GenericStructure to locate
     * @return a fully qualified HL7Location
     */
    public static HL7Location determine(GenericStructure gs) {
        HL7Location loc = new HL7Location();
        loc = applyLocationInfo(loc, gs);
        return loc;
    }

    //the magic method
    private static HL7Location applyLocationInfo(HL7Location loc, GenericStructure gs) {
        if (gs instanceof HL7FieldSubcomponent) {
            HL7FieldSubcomponent fieldsubcomp = (HL7FieldSubcomponent)gs;
            loc.subcomponentIndexInComponent = fieldsubcomp.getParent().getFieldSubcomponents().indexOf(fieldsubcomp);
            loc.hasSubcomponent = true;
            return applyLocationInfo(loc, fieldsubcomp.getParent());
        } else if (gs instanceof HL7FieldComponent) {
            HL7FieldComponent fieldcomp = (HL7FieldComponent)gs;
            loc.componentIndexInField = fieldcomp.getParent().getFieldComponents().indexOf(fieldcomp);
            loc.hasComponent = true;
            return applyLocationInfo(loc, fieldcomp.getParent());
        } else if (gs instanceof HL7Field) {
            HL7Field field = (HL7Field)gs;
            loc.fieldIndexInRepeatingField = field.getParent().getFields().indexOf(field);
            loc.repeatingFieldIndexInSegment = field.getParent().getParent().getRepeatingFields().indexOf(field.getParent());
            loc.hasField = true;

            if (field.getParent().getFields().size() != 1) {
                loc.isFieldIndexImplied = false;
            } else {
                loc.isFieldIndexImplied = true;
            }
            
            return applyLocationInfo(loc, field.getParent().getParent()); //skip right to segment!
        } else if (gs instanceof HL7RepeatingField) {
            HL7RepeatingField repeatingfield = (HL7RepeatingField)gs;
            loc.fieldIndexInRepeatingField = 0;
            loc.repeatingFieldIndexInSegment = repeatingfield.getParent().getRepeatingFields().indexOf(repeatingfield);
            loc.hasField = true;    
            loc.isFieldIndexImplied = true;
            return applyLocationInfo(loc, repeatingfield.getParent());
        } else if (gs instanceof HL7Segment) {
            HL7Segment segment = (HL7Segment)gs;
            loc.segmentName = segment.getSegmentName();
            //loc.segmentIndex = segment.getParent().helper().getAllSegments(loc.segmentName).indexOf(segment);
            List<HL7Segment> allSegments = segment.getParent().getSegments();
            List<HL7Segment> specialSegments = new ArrayList<HL7Segment>();

            for(HL7Segment thisSegment : allSegments) {
                if(thisSegment.getSegmentName().equals(loc.segmentName)) {
                    specialSegments.add(thisSegment);
                }
            }
            loc.segmentIndex = specialSegments.indexOf(segment);
            
            if (specialSegments.size() != 1) {
                loc.isSegmentIndexImplied = false;
            } else {
                loc.isSegmentIndexImplied = true;
            }

            loc.hasSegment = true;
            return loc;
        }
        return loc;
    }

    /**
     * Represent this HL7Location object as a full string description
     * @return this location as a string
     */
    public String getFullyQualifiedHL7Location() {
        StringBuffer fqLoc = new StringBuffer();
        if(hasSegment()) {
            fqLoc.append(getSegmentName()).append("[").append(getSegmentIndex()).append("]");
            if(hasField()) {
                fqLoc.append("-");
                fqLoc.append(getRepeatingFieldHL7Position()).append("[").append(getFieldIndex()).append("]");
                if(hasComponent()) {
                    fqLoc.append(".");
                    fqLoc.append(getComponentHL7Position());
                    if(hasSubcomponent()) {
                        fqLoc.append(".");
                        fqLoc.append(getSubcomponentHL7Position());
                    }
                }
            }
        }
        return fqLoc.toString();
    }

    /**
     * Represent this HL7Location as a shorthanded String
     * @return this location as a string
     */
    public String getHL7Location() {
        StringBuffer fqLoc = new StringBuffer();
        if(hasSegment()) {
            fqLoc.append(getSegmentName());
            if (!isSegmentIndexImplied) {
                fqLoc.append("[").append(getSegmentIndex()).append("]");
            }

            if(hasField()) {
                fqLoc.append("-");
                fqLoc.append(getRepeatingFieldHL7Position());
                if (!isFieldIndexImplied) {
                    fqLoc.append("[").append(getFieldIndex()).append("]");
                }
                if(hasComponent()) {
                    fqLoc.append(".");
                    fqLoc.append(getComponentHL7Position());
                    if(hasSubcomponent()) {
                        fqLoc.append(".");
                        fqLoc.append(getSubcomponentHL7Position());
                    }
                }
            }
        }
        return fqLoc.toString();
    }

    /**
     * Represent this HL7Location as a condensed string
     * @return this location as a string
     */
    public String getShortHL7Location() {
        StringBuffer fqLoc = new StringBuffer();
        if(hasSegment()) {
            fqLoc.append(getSegmentName());

            if(hasField()) {
                fqLoc.append("-");
                fqLoc.append(getRepeatingFieldHL7Position());

                if(hasComponent()) {
                    fqLoc.append(".");
                    fqLoc.append(getComponentHL7Position());
                    if(hasSubcomponent()) {
                        fqLoc.append(".");
                        fqLoc.append(getSubcomponentHL7Position());
                    }
                }
            }
        }
        return fqLoc.toString();
    }

    /**
     * The name of the segment defined in this HL7Location
     * @return Segment name
     */
    public String getSegmentName() {
        return segmentName;
    }

    /**
     * The index of the segment defined in this HL7Location
     * @return Segment index
     */
    public int getSegmentIndex() {
        return segmentIndex;
    }

    /**
     * The position (in the HL7 definition) of a repeating field in an HL7Structure
     * @return RepeatingField position
     */
    public int getRepeatingFieldHL7Position() {
        if(getSegmentName().equals("MSH")) {
            if (repeatingFieldIndexInSegment != 0) {
                return repeatingFieldIndexInSegment+1;
            } else {
                return 0;
            }
        } else {
            return repeatingFieldIndexInSegment;
        }
    }

    /**
     * Gets the Field index
     * @return index of field
     */
    public int getFieldIndex() {
        return fieldIndexInRepeatingField;
    }
    /**
     * Gets the RepeatingField index
     * @return index of repeatingfield
     */
    public int getRepeatingFieldIndex() {
        return repeatingFieldIndexInSegment;
    }

    /**
     * Gets the component index
     * @return index of component
     */
    public int getComponentIndex() {
        return componentIndexInField;
    }

    /**
     * Gets the component HL7 position (index+1)
     * @return position of component
     */
    public int getComponentHL7Position() {
        return componentIndexInField+1;
    }

    /**
     * Gets the subcomponent index
     * @return index of subcomponent
     */
    public int getSubcomponentIndex() {
        return subcomponentIndexInComponent;
    }

    /**
     * Gets the subcomponent HL7 position (index+1)
     * @return position of subcomponent
     */
    public int getSubcomponentHL7Position() {
        return subcomponentIndexInComponent+1;
    }

    /**
     * Flag to determine if the segment index in this object is implied
     * @return true if segment index is implied, not explicity specified
     */
    public boolean isSegmentIndexImplied() {
        return isSegmentIndexImplied;
    }

    /**
     * Flag to determine if the field index in this object is implied
     * @return true if field index is implied, not explicity specified
     */
    public boolean isFieldIndexImplied() {
        return isFieldIndexImplied;
    }

    /**
     * Switch to determine if this HL7Location has a segment specified
     * @return true if data was specified
     */
    public boolean hasSegment() {
        return hasSegment;
    }

    /**
     * Switch to determine if this HL7Location has a field specified
     * @return true if data was specified
     */
    public boolean hasField() {
        return hasField;
    }

    /**
     * Switch to determine if this HL7Location has a component specified
     * @return true if data was specified
     */
    public boolean hasComponent() {
        return hasComponent;
    }

    /**
     * Switch to determine if this HL7Location has a subcomponent specified
     * @return true if data was specified
     */
    public boolean hasSubcomponent() {
        return hasSubcomponent;
    }
}
