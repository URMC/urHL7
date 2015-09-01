/*
 * The MIT License
 * 
 * Copyright (c) 2010 David Morgan, University of Rochester Medical Center
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
import org.testng.annotations.Test;

/**
 *
 * @author David Morgan
 */
public class DelimiterTesting {
    @Test 
    public void testTerser() {


        String origMsg = "MSH|^~\\&|FLOWCAST|IDX|URMC\\S\\12|ENGINE|201001111101||ADT^A13|61234_22333_DC|P|2.3||||||ASCII|\r"+
           "PID|1||1133445^^^IDX^MRN||MORGAN^JESSICA^^^^||19871012|F||WH|123\\S\\ MILL RD^^ROCHESTER^NY^14526^^^^||(585)555-5555||||||000-11-0000||||||||N|||N|\r"+
           "NK1|1|MORGAN^BILL^^^^|SP||(315)555-5555||NK||||\r"+
           "NK1|1|MORGAN^JOE^^^^|SP||(3*5)555-5555~(315)555-4444||NK||||\r" +
           "ZZZ|||^2.1\\S\\-10^10&15^SAMPLE\r";
        HL7Structure struct = Igor.structure(origMsg);
        

        String finMsg = "MSH|*~\\`|FLOWCAST|IDX|URMC^12|ENGINE|201001111101||ADT*A13|61234_22333_DC|P|2.3||||||ASCII|\r"+
           "PID|1||1133445***IDX*MRN||MORGAN*JESSICA****||19871012|F||WH|123^ MILL RD**ROCHESTER*NY*14526****||(585)555-5555||||||000-11-0000||||||||N|||N|\r"+
           "NK1|1|MORGAN*BILL****|SP||(315)555-5555||NK||||\r"+
           "NK1|1|MORGAN*JOE****|SP||(3\\S\\5)555-5555~(315)555-4444||NK||||\r" +
           "ZZZ|||*2.1^-10*10`15*SAMPLE\r";

        char[] chars = {'|', '*', '~', '\\', '`'};
        struct.changeDelims(chars);

        HL7Structure s2 = struct.copy();
        
        System.out.println(origMsg.replaceAll("\r", "\r\n"));
        System.out.println("----");
        System.out.println(finMsg.replaceAll("\r", "\r\n"));
        System.out.println("----");
        System.out.println(s2.marshal().replaceAll("\r", "\r\n"));
        System.out.println("----");

        if (!finMsg.equals(s2.marshal())) {
            throw new RuntimeException("Failed Basic Test");
        }

        char[] chars2 = {'|', '^', '~', '\\', '&'};
        s2.changeDelims(chars2);

        char[] chars3 = {':', '^', '~', '\\', '`'};
        s2.changeDelims(chars3);
        

        String finAdvMsg =  "MSH:^~\\`:FLOWCAST:IDX:URMC\\S\\12:ENGINE:201001111101::ADT^A13:61234_22333_DC:P:2.3::::::ASCII:\r"+
           "PID:1::1133445^^^IDX^MRN::MORGAN^JESSICA^^^^::19871012:F::WH:123\\S\\ MILL RD^^ROCHESTER^NY^14526^^^^::(585)555-5555::::::000-11-0000::::::::N:::N:\r"+
           "NK1:1:MORGAN^BILL^^^^:SP::(315)555-5555::NK::::\r"+
           "NK1:1:MORGAN^JOE^^^^:SP::(3*5)555-5555~(315)555-4444::NK::::\r" +
           "ZZZ:::^2.1\\S\\-10^10`15^SAMPLE\r";

        System.out.println(finAdvMsg.replaceAll("\r", "\r\n"));
        System.out.println("----");
        System.out.println(s2.marshal().replaceAll("\r", "\r\n"));
        System.out.println("----");
        
        if (!finAdvMsg.equals(s2.marshal())) {
            throw new RuntimeException("Failed Advanced Test");
        }
    }
}
