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

/**
 * A data object that carries sufficient information to location a particular position in an
 * HL7 message
 * @author dmorgan
 */
public class LocationSpecification {
    private String segmentName;
    private int fieldPosition = -1;
    private int repeatingFieldIndex = -1;
    private int componentPosition = -1;
    private int subcomponentPosition = -1;
    private int segmentRepPosition = -1;
    private boolean specifiedSegmentPosition = false;
    private boolean specifiedFieldPosition = false;

    /**
     * Creates a blank LocationSpecification
     */
    public LocationSpecification() {}
    /**
     * Returns the segment name that was specified
     * @return the segmentName
     */
    public String getSegmentName() {
        return segmentName;
    }

    /**
     * Sets the segment name of this LocationSpecification
     * @param segmentName the segmentName to set
     */
    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    /**
     * Gets the field position specified (or -1 if not specified)
     * @return the fieldPosition
     */
    public int getFieldPosition() {
        return fieldPosition;
    }

    /**
     * Sets the field position
     * @param fieldPosition the fieldPosition to set
     */
    public void setFieldPosition(int fieldPosition) {
        this.fieldPosition = fieldPosition;
    }

    /**
     * Gets the repeating field index (or -1 if not specified)
     * @return the repeatingFieldNumber
     */
    public int getRepeatingFieldIndex() {
        return repeatingFieldIndex;
    }

    /**
     * Sets the repeating field index
     * @param repeatingFieldIndex the repeatingFieldNumber to set
     */
    public void setRepeatingFieldIndex(int repeatingFieldIndex) {
        this.repeatingFieldIndex = repeatingFieldIndex;
    }

    /**
     * Gets the component position (or -1 if not specified)
     * @return the componentPosition
     */
    public int getComponentPosition() {
        return componentPosition;
    }

    /**
     * Sets the component position
     * @param componentPosition the componentPosition to set
     */
    public void setComponentPosition(int componentPosition) {
        this.componentPosition = componentPosition;
    }

    /**
     * Gets the subcomponent position (or -1 not specified)
     * @return the subcomponentPosition
     */
    public int getSubcomponentPosition() {
        return subcomponentPosition;
    }

    /**
     * Sets the subcomponent position
     * @param subcomponentPosition the subcomponentPosition to set
     */
    public void setSubcomponentPosition(int subcomponentPosition) {
        this.subcomponentPosition = subcomponentPosition;
    }

  /*  public String toString() {
        
        String ret = "segName: " + getSegmentName();
        //if (isSpecifiedSegmentPosition()) {
            ret += "\nsegRepPosition: " + getSegmentRepPosition();
        //}
        ret +=  "\nrepFieldIndx: " + getRepeatingFieldIndex();
        ret +=  "\nfieldPos: " + getFieldPosition() +
                "\ncompPos: " + getComponentPosition() +
                "\nsubcompPos: " + getSubcomponentPosition() +
                "\nisSpecifiedSegmentPosition: " + isSpecifiedSegmentPosition() +
                "\nisSpecifiedFieldPosition: " + isSpecifiedFieldPosition();
        return ret;
    } */

    /**
     * Gets the segment repetition position (or -1 if not specified)
     * @return the segmentRepPosition
     */
    public int getSegmentRepPosition() {
        return segmentRepPosition;
    }

    /**
     * Sets the segment repetition position
     * @param segmentRepPosition the segmentRepPosition to set
     */
    public void setSegmentRepPosition(int segmentRepPosition) {
        this.segmentRepPosition = segmentRepPosition;
    }

    /**
     * Returns if the segment repetition was set
     * @return the specifiedSegmentPosition
     */
    public boolean isSpecifiedSegmentPosition() {
        return specifiedSegmentPosition;
    }

    /**
     * Sets if the segment repetition was set
     * @param specifiedSegmentPosition the specifiedSegmentPosition to set
     */
    public void setSpecifiedSegmentPosition(boolean specifiedSegmentPosition) {
        this.specifiedSegmentPosition = specifiedSegmentPosition;
    }

    /**
     * Returns if the specified field repetition position was set
     * @return the specifiedFieldPosition
     */
    public boolean isSpecifiedFieldPosition() {
        return specifiedFieldPosition;
    }

    /**
     * Sets if the field position repetition position was set
     * @param specifiedFieldPosition the specifiedFieldPosition to set
     */
    public void setSpecifiedFieldPosition(boolean specifiedFieldPosition) {
        this.specifiedFieldPosition = specifiedFieldPosition;
    }
}
