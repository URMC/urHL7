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

package org.urhl7.spark;

import org.urhl7.igor.HL7Structure;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple adapter that will behave has a HL7MessageListener, receive the data, then allow you to access it in an List of HL7Structures.
 * @author dmorgan
 */
public class HL7MessageListenerListAdapter implements HL7MessageListener {
    private ArrayList<HL7Structure> list = new ArrayList<HL7Structure>();

    /**
     * Called whenever a message is parsed completely. This method adds the HL7Structure to an internal list that can
     * be accessed when file parsing is complete, or periodically.
     * @param message the message recieved
     * @return always returns true (as per general collection contract)
     */
    public boolean messageReceived(HL7Structure message) {
        return list.add(message);
    }

    /**
     * Retrieves the list that has been populated in this listener
     * @return the list
     */
    public List<HL7Structure> getList() {
        return list;
    }

    /**
     * Removes all elements from the list
     */
    public void clearList() {
        list.clear();
    }
}
