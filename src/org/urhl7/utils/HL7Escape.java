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

/**
 * Class with static methods to escape/unescape HL7 messages
 * @author dmorgan
 */
public class HL7Escape {
    private HL7Escape() {}
    /**
     * Returns the data, properly unencoded
     * @return unencoded data
     */
    public static String unescape(char[] delimiters, String data) {
            String tdata = data.toString();

            if(tdata.contains( ""+delimiters[3] )) {
                char[] escapeEscCharSeq = {delimiters[3], 'E', delimiters[3]};
                String escEsc = new String(escapeEscCharSeq);

                char[] escapeFieldCharSeq = {delimiters[3], 'F', delimiters[3]};
                String escField = new String(escapeFieldCharSeq);

                char[] escapeRepCharSeq = {delimiters[3], 'R', delimiters[3]};
                String escRep = new String(escapeRepCharSeq);

                char[] escapeSubCharSeq = {delimiters[3], 'S', delimiters[3]};
                String escSub = new String(escapeSubCharSeq);

                char[] escapeSubSubCharSeq = {delimiters[3], 'T', delimiters[3]};
                String escSubSub = new String(escapeSubSubCharSeq);


                tdata = tdata.replace(escSubSub, delimiters[4]+"");
                tdata = tdata.replace(escSub, delimiters[1]+"");
                tdata = tdata.replace(escRep, delimiters[2]+"");
                tdata = tdata.replace(escField, delimiters[0]+"");
                tdata = tdata.replace(escEsc, delimiters[3]+"");
            }
            return tdata;
    }

    /**
     * Escape any encoding characters correctly.
     * @param data
     */
    public static String escape(char[] delimiters, String data) {
        if (data != null) {
            //StringBuffer theData = new StringBuffer(data);
            String inputData = data.toString();
            char[] escapeEscCharSeq = {delimiters[3], 'E', delimiters[3]};
            String escEsc = new String(escapeEscCharSeq);

            char[] escapeFieldCharSeq = {delimiters[3], 'F', delimiters[3]};
            String escField = new String(escapeFieldCharSeq);

            char[] escapeRepCharSeq = {delimiters[3], 'R', delimiters[3]};
            String escRep = new String(escapeRepCharSeq);

            char[] escapeSubCharSeq = {delimiters[3], 'S', delimiters[3]};
            String escSub = new String(escapeSubCharSeq);

            char[] escapeSubSubCharSeq = {delimiters[3], 'T', delimiters[3]};
            String escSubSub = new String(escapeSubSubCharSeq);


            inputData = inputData.replace(delimiters[3]+"", escEsc);
            inputData = inputData.replace(delimiters[0]+"", escField);
            inputData = inputData.replace(delimiters[2]+"", escRep);
            inputData = inputData.replace(delimiters[1]+"", escSub);
            inputData = inputData.replace(delimiters[4]+"", escSubSub);

            return inputData;
        } else {return data;}
    }
}
