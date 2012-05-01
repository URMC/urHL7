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

/**
 * An NullField is a DataField that has no data, and no references to anything. However,
 * a HL7Structure may reference it. It is usually used as a placeholder for where a DataField
 * needs to exist, but doesn't for some reason (usually an error condition beind supressed).
 * @author dmorgan
 */
public class NullField implements DataField {
    private String data = "";

    /**
     * Creates a new NullField that has no data, and no reference attachments.
     */
    public NullField() {}

    /**
     * Gets the data of this NullField, which is usually an empty string, unless you've modified it.
     * @return the data in this object
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data of this NullField, which will never be seen, unless accessed using getData() in the future.
     * @param data String to store in this NullField
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Returns the data in this field
     * @return
     */
    public String marshal() {
        return data;
    }

    /**
     * Unmarshals the data into this field, which is the same as setData(data)
     */
    public void unmarshal(String data) {
        this.data = data;
    }

    /**
     * Displays this field as a String. Will return "NullField*[dataStored]"
     * @return a String "NullField*" plus data that was stored on this field+
     */
    @Override
    public String toString() {
        return "NullField*" + marshal();
    }

    /**
     * Will always return null, as this object is orphaned.
     * @return a null value
     */
    public Object getParent() {
        return null;
    }
}
