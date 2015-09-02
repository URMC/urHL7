/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urhl7.igor;

import java.util.ArrayList;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author dmorgan
 */
public class HL7StructureHelperNGTest {
    HL7Structure message;
    
    public HL7StructureHelperNGTest() {

    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        String msg =    "MSH|^~\\&|||||||ORU^R01|HP128978937126197|P|2.3||||||8859/1\r" +
                        "PID|||E12345^^^^EPI~858585^^^^SMHMRN~222444^^^^HHHMRN||Smith^John||\"\"|U\r" +
                        "PV1||I|8-3600^^8-3604&4&1\r" +
                        "OBR||||||||||||||20111114214931\r" +
                        "OBX||NM|0002-4bb8^SpO2^MDIL|0|98|0004-0220^%^MDIL|||||F\r" +
                        "OBX||NM|0002-5000^SML^MDIL|0|2.73x10\\S\\-7|0004-0ae0^rpm^MDIL|||||F\r" +
                        "OBX||NM|0002-f125^pNN50^MDIL|0|0.00|0004-0220^%^MDIL|||||F\r" +
                        "OBX||NM|0002-4182^HR^MDIL|0|87|0004-0aa0^bpm^MDIL|||||F\r" +
                        "OBX||NM|0002-4261^PVC^MDIL|0|0|0004-0aa0^bpm^MDIL|||||F\r" +
                        "OBX||NM|0002-4822^Pulse^MDIL|0|87|0004-0aa0^bpm^MDIL|||||F\r" +
                        "OBX||NM|0002-f081^SD NN^MDIL|0|3.00|0004-0aa0^bpm^MDIL|||||F\r";
        message = Igor.structure(msg);
    }

    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasField1() {
        String descriptor = "PV1-3";
        HL7StructureHelper instance = message.helper();
        boolean expResult = true;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }

    
    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasField2() {
        String descriptor = "PV1-4";
        HL7StructureHelper instance = message.helper();
        boolean expResult = false;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }
    
    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasField3() {
        String descriptor = "PID[0]-3";
        HL7StructureHelper instance = message.helper();
        boolean expResult = true;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }    

    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasField4() {
        String descriptor = "PID[0]-3[1]";
        HL7StructureHelper instance = message.helper();
        boolean expResult = true;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }    
    
    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasField5() {
        String descriptor = "PID[1]-3[1]";
        HL7StructureHelper instance = message.helper();
        boolean expResult = false;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }    
    
    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasField6() {
        String descriptor = "PID-3[9]";
        HL7StructureHelper instance = message.helper();
        boolean expResult = false;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }    
    
    
    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasFieldRepeatingSegements1() {
        String descriptor = "OBX-2";
        HL7StructureHelper instance = message.helper();
        boolean expResult = true;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }
    
    /**
     * Test of has method, of class HL7StructureHelper.
     */
    @Test
    public void testHasFieldRepeatingSegements2() {
        String descriptor = "OBX-12";
        HL7StructureHelper instance = message.helper();
        boolean expResult = false;
        boolean result = instance.has(descriptor);
        assertEquals(result, expResult);
    }

    /**
     * Test of getSegment method, of class HL7StructureHelper.
     */
    @Test
    public void testGetSegmentPID() {
        String descriptor = "PID";
        HL7StructureHelper instance = message.helper();
        HL7Segment expResult = message.getSegment(1);
        HL7Segment result = instance.getSegment(descriptor);
        assertEquals(result, expResult);
    }

    /**
     * Test of getSegment method, of class HL7StructureHelper.
     */
    @Test
    public void testGetSegmentOBR() {
        String descriptor = "OBR";
        HL7StructureHelper instance = message.helper();
        HL7Segment expResult = message.getSegment(3);
        HL7Segment result = instance.getSegment(descriptor);
        assertEquals(result, expResult);
    }

    /**
     * Test of getSegment method, of class HL7StructureHelper.
     */
    @Test
    public void testGetSegmentFirstOBX() {
        String descriptor = "OBX";
        HL7StructureHelper instance = message.helper();
        HL7Segment expResult = message.getSegment(4);
        HL7Segment result = instance.getSegment(descriptor);
        assertEquals(result, expResult);
    }
    
    /**
     * Test of getSegment method, of class HL7StructureHelper.
     */
    @Test
    public void testGetSegmentOtherOBX() {
        String descriptor = "OBX[2]";
        HL7StructureHelper instance = message.helper();
        HL7Segment expResult = message.getSegment(6);
        HL7Segment result = instance.getSegment(descriptor);
        assertEquals(result, expResult);
    }
    
    /**
     * Test of getSegment method, of class HL7StructureHelper.
     */
    @Test
    public void testGetSegmentMSH() {
        String descriptor = "MSH";
        HL7StructureHelper instance = message.helper();
        HL7Segment expResult = message.getSegment(0);
        HL7Segment result = instance.getSegment(descriptor);
        assertEquals(result, expResult);
    }
    
    /**
     * Test of getSegment method, of class HL7StructureHelper.
     */
    @Test
    public void testGetSegmentOtherMSH() {
        String descriptor = "MSH[1]";
        HL7StructureHelper instance = message.helper();
        HL7Segment expResult = null; //should return a null, cuz it doesn't exist
        HL7Segment result = instance.getSegment(descriptor);
        assertEquals(result, expResult);
    }



    /**
     * Test of getAllSegments method, of class HL7StructureHelper.
     */
    @Test
    public void testGetAllSegmentsOBX() {
        String descriptor = "OBX";
        HL7StructureHelper instance = message.helper();
        List expResult = message.getSegments().subList(4, 11);
        List result = instance.getAllSegments(descriptor);
        assertEquals(result, expResult);
    }

    /**
     * Test of getAllSegments method, of class HL7StructureHelper.
     */
    @Test
    public void testGetAllSegmentsPID() {
        String descriptor = "PID";
        HL7StructureHelper instance = message.helper();
        List expResult = message.getSegments().subList(1, 2);
        List result = instance.getAllSegments(descriptor);
        assertEquals(result, expResult);
    }
    
    /**
     * Test of getAllSegments method, of class HL7StructureHelper.
     */
    @Test
    public void testGetAllSegmentsZID() {
        String descriptor = "ZID";
        HL7StructureHelper instance = message.helper();
        List expResult = new ArrayList<HL7Segment>(); //should return an empty list
        List result = instance.getAllSegments(descriptor);
        assertEquals(result, expResult);
    }


    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_FieldLevel1() {
        String descriptor = "PV1-2";
        HL7StructureHelper instance = message.helper();
        String expResult = "I";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_FieldLevel2() {
        String descriptor = "OBX-5";
        HL7StructureHelper instance = message.helper();
        String expResult = "98";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_FieldLevel3() {
        String descriptor = "OBR-5";
        HL7StructureHelper instance = message.helper();
        String expResult = "";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_FieldLevelInSpecificSegment1() {
        String descriptor = "OBX[1]-5";
        HL7StructureHelper instance = message.helper();
        String expResult = "2.73x10^-7"; //tests getData escape char redo
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_FieldLevelInSpecificSegment2() {
        String descriptor = "OBX[2]-5";
        HL7StructureHelper instance = message.helper();
        String expResult = "0.00";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }    
    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevel1() {
        String descriptor = "PID-5.1";
        HL7StructureHelper instance = message.helper();
        String expResult = "Smith";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevel2() {
        String descriptor = "PID-5.2";
        HL7StructureHelper instance = message.helper();
        String expResult = "John";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevel3() {
        String descriptor = "OBX-3.2";
        HL7StructureHelper instance = message.helper();
        String expResult = "SpO2";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevel4() {
        String descriptor = "PID-3.1";
        HL7StructureHelper instance = message.helper();
        String expResult = "E12345";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificField1() {
        String descriptor = "PID-3[1].1";
        HL7StructureHelper instance = message.helper();
        String expResult = "858585";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  

    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificField2() {
        String descriptor = "PID-3[4].1";
        HL7StructureHelper instance = message.helper();
        String expResult = "";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificField3() {
        String descriptor = "PID-3[1].5";
        HL7StructureHelper instance = message.helper();
        String expResult = "SMHMRN";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificField4() {
        String descriptor = "PID-3[4].5";
        HL7StructureHelper instance = message.helper();
        String expResult = "";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificSegment1() {
        String descriptor = "OBX[1]-3.2";
        HL7StructureHelper instance = message.helper();
        String expResult = "SML";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificSegment2() {
        String descriptor = "OBX[5]-3.2";
        HL7StructureHelper instance = message.helper();
        String expResult = "Pulse";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificSegment3() {
        String descriptor = "OBX[90]-3.2";
        HL7StructureHelper instance = message.helper();
        String expResult = "";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  

    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_ComponentLevelInSpecificSegment4() {
        String descriptor = "OBX[1]-3.18";
        HL7StructureHelper instance = message.helper();
        String expResult = "";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  
    
    
    /**
     * Test of get method, of class HL7StructureHelper.
     */
    @Test
    public void testGet_GetData_SubComponentLevel1() {
        String descriptor = "PV1-3.3.2";
        HL7StructureHelper instance = message.helper();
        String expResult = "4";
        String result = instance.get(descriptor).getData();
        assertEquals(result, expResult);
    }  

    /**
     * Test of getAll method, of class HL7StructureHelper.
     */
    @Test
    public void testGetAllField() {
        String descriptor = "OBX-2";
        HL7StructureHelper instance = message.helper();
        
        List result = instance.getAll(descriptor);
        assertTrue(result.size() == 7);
        for(Object item : result) {
            DataField df = (DataField) item;
            assertEquals(df.getData(), "NM");
        }
    }
    
    /**
     * Test of getAll method, of class HL7StructureHelper.
     */
    @Test
    public void testGetAllSubField() {
        String descriptor = "OBX-3.3";
        HL7StructureHelper instance = message.helper();
        
        List result = instance.getAll(descriptor);
        assertTrue(result.size() == 7);
        for(Object item : result) {
            DataField df = (DataField) item;
            assertEquals(df.getData(), "MDIL");
        }
    }

    
    /**
     * Test of getAll method, of class HL7StructureHelper.
     */
    @Test
    public void testGetAllSubFieldInSpecificSegment() {
        String descriptor = "OBX[1]-3.3";
        HL7StructureHelper instance = message.helper();
        
        List result = instance.getAll(descriptor);
        assertTrue(result.size() == 1);
        for(Object item : result) {
            DataField df = (DataField) item;
            assertEquals(df.getData(), "MDIL");
        }
    }

    
}
