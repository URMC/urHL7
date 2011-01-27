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

import java.util.ArrayList;

/**
 * StringHelper is a class that contains utility methods to manipulate strings.
 * @author dmorgan
 */
public class StringHelper {
    private StringHelper() {}
    /**
     * Operates a lot like the split function on String, but does not require the delimiter to be
     * a regular expression (this does not matter much, except when dynamically loading the delim
     * that may have regular expression special chars in it).
     * @param data the data to split
     * @param delim the delimiting string to use
     * @return an array of strings, without the delimiter
     */
    public static String[] explode(String data, String delim) {
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer sb = new StringBuffer(data);
        boolean splitting = true;
        int curpos=0;
        int delimpos = sb.indexOf(delim);
        while(splitting) {
            if (delimpos >= 0) {
                list.add(sb.substring(curpos, delimpos));
            } else {
                list.add(sb.substring(curpos));
                splitting = false;
            }
            curpos = delimpos+delim.length();
            delimpos = sb.indexOf(delim, curpos);
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * Duplicates PHP's "implode" functionality. Will glue an array of
     * strings together with the delimiter: <br />
     * foo[0] = "Dave,";<br />
     * foo[1] = "hello";<br />
     * foo[2] = "world";<br />
     * implode(foo, " ") => "Dave, hello world"
     *
     * @param ary the array of strings to glue
     * @param delim the glue
     * @return the imploded string
     */
    public static String implode(String[] ary, String delim) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<ary.length; i++) {
            if(i!=0) { sb.append(delim); }
            sb.append(ary[i]);
        }
        return sb.toString();
    }
}
