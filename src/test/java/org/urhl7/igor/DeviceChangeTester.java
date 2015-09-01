package org.urhl7.igor;

import org.urhl7.igor.HL7Structure;
import org.urhl7.igor.Igor;

/**
 *
 * @author dmorgan
 */
public class DeviceChangeTester {
    @org.junit.Test public void testDevChange() {
        String testMsg = "MSH|^~\\&|CPN|HISOut|||||ORU^R01|QS1104192313801|P|2.4|1||||||||\r" +
                "PID|||TEST11-5-10||TEST^VS^SMH1|||||||||||||||||||||||||||||||||\r" +
                "PV1|||3-14^Triage C|||||||||||||||||||||||||||||||||||||||||||||||||\r" +
                "OBR||1099678993^CPN|2|^Maternal Vital Signs|||201011041923|||||||||||||||201011041923|||||^^^^^^^||||||||||||||||||||\r" +
                "OBX|1|NM|Systolic BP^BP Systolic||120||0-300||||F|||201011041900||JCleme|||\r" +
                "OBX|1|NM|Systolic BP^BP Systolic||111||0-300||||F|||201011041822||JCleme|||\r" +
                "OBX|2|NM|Diastolic BP^BP Diastolic||50||0-180||||F|||201011041822||JCleme|||\r" +
                "OBX|3|NM|Pulse^Pulse labor||56||0-300||||F|||201011041822||JCleme|||\r" +
                "OBX|4|NM|O2 Sat^SaO2||95||0-105||||F|||201011041822||JCleme|||\r";

        HL7Structure struct = Igor.structure(testMsg);
        struct.helper().get("PV13").setData(struct.helper().get("PV13").getData().replace('^', '-').replace('&', '-'));
        System.out.println(struct.marshal());
    }
}
