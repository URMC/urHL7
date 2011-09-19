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

import org.urhl7.utils.*;

/**
 * Specifies which rules should be applied to portions of an HL7 message/structure.
 * @author dmorgan
 */
public class HL7Rule {
    
    private HL7Location loc;
    private Rule ruleToEnforce;

    private HL7Rule() { }

    /**
     * Define an HL7Rule that has location and a rule to apply
     * @param descriptor the description of the location using HL7 notation
     * @param rule the rule to apply to this segment or field
     */
    public HL7Rule(String descriptor, Rule rule) {
        setLocationSpecification(HL7Location.parse(descriptor));
        setRuleToEnforce(rule);
    }

    /**
     * Define an HL7Rule that has location and a rule to apply
     * @param loc the location of the field or segment
     * @param rule the rule to apply to this segment or field
     */
    public HL7Rule(HL7Location loc, Rule rule) {
        setLocationSpecification(loc);
        setRuleToEnforce(rule);
    }

    /**
     * Get the locationSpecification target of this HL7Rule
     * @return the location
     */
    public HL7Location getLocationSpecification() {
        return loc;
    }

    /**
     * Set the locationSpecification target of this HL7Rule
     * @param loc the location to set
     */
    public void setLocationSpecification(HL7Location loc) {
        this.loc = loc;
    }

    /**
     * Get the rule to apply to the specifed location
     * @return the rule to enforce
     */
    public Rule getRuleToEnforce() {
        return ruleToEnforce;
    }

    /**
     * Define the rule to apply to the specifed location
     * @param ruleToEnforce the rule to enforce to set
     */
    public void setRuleToEnforce(Rule ruleToEnforce) {
        this.ruleToEnforce = ruleToEnforce;
    }


}


