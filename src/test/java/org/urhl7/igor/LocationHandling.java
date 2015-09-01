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

import org.urhl7.igor.HL7Structure;
import org.urhl7.igor.Igor;
import java.util.*;

/**
 *
 * @author David Morgan
 */
public class LocationHandling {
    public LocationHandling() {}

    @org.junit.Test public void testTerser() {
        String origMsg = "MSH|^~\\&|FLOWCAST|IDX|URMC|ENGINE|201001111101||ADT^A13|61234_22333_DC|P|2.3||||||ASCII|\r"+
           "PID|1||1133445^^^IDX^MRN||MORGAN^JESSICA^^^^||19871012|F||WH|123 MILL RD^^ROCHESTER^NY^14526^^^^||(585)555-5555||||||000-11-0000||||||||N|||N|\r"+
           "NK1|1|MORGAN^BILL^^^^|SP||(315)555-5555||NK&&KID||||\r"+
           "NK1|1|MORGAN^JOE^^^^|SP||(315)555-5555~(315)555-4444||NK||||\r" +
           "ZZZ|||^^10&15^SAMPLE\r" +
           "ZRP|||ONE~TWO~THREE||\r";
        HL7Structure struct = Igor.structure(origMsg);

        String finMsg = "MSH|^~\\&|IGOR|URHL7|URMC|ENGINE|201001111101||ADT^A13|61234_22333_DC|P|2.5.1||||||ASCII|\r"+
           "PID|1||1133445^^^IDX^MRN||MORGAN^DAVID^^^^||19871012|F||WH|123 MILL RD^^ROCHESTER^NY^14526^^^^||(585)555-5555||||||000-11-0000||||||||N|||N|\r"+
           "NK1|1|MORGAN^BILL^^^^|SP||(315)555-5588||NK&&CHILD||||\r"+
           "NK1|1|MORGAN^JOE^^^^|SP||(315)555-5555~(315)555-4433||SB||||\r" +
           "ZZZ|||^^5&15^SAMPLE\r" +
           "ZRP|||1~2~3||\r";

        //MSH3=IGOR,4=URHL7
        struct.helper().get("MSH3").setData("IGOR");
        struct.helper().get("MSH4").setData("URHL7");

        //PID5.2=DAVID
        struct.helper().get("PID5.2").setData("DAVID");

        //NK1(second)7=SB
        struct.helper().get("NK1[1]7").setData("SB");

        //NK1(second)5(first)=(315)555-4433
        struct.helper().get("NK1[1]5[1]").setData("(315)555-4433");

        //NK1(first)5=(315)555-5588
        struct.helper().get("NK1[0]5").setData("(315)555-5588");

        //NK1(first)7.1.3=CHILD
        struct.helper().get("NK1[0]7.1.3").setData("CHILD");

        //ZZZ3.3.1=5
        struct.helper().get("ZZZ3.3.1").setData("5");

        struct.helper().get("MSH12").setData("2.5.1");

        List<DataField> list = struct.helper().getAll("ZRP3");
        int pos = 1;
        for(DataField df : list) {
            df.setData(""+pos);
            pos++;
        }


        //System.out.println("testTerser() passed? : " + finMsg.equals(struct.marshal()));
        if (!finMsg.equals(struct.marshal())) {
            System.out.println(finMsg);
            System.out.println("");
            System.out.println(struct.marshal());
            System.out.println("");
            System.out.println("");

            throw new RuntimeException("Failed Test");
        }

    }
}
