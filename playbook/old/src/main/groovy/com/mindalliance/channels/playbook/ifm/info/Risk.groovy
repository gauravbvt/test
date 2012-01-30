package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Defineable
import com.mindalliance.channels.playbook.ifm.Named

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 17, 2008
 * Time: 9:14:55 PM
 */
class Risk extends BeanImpl { // risk qualifying an event

    String description = ''
    float probability = 1.0
    List<Ref> possibleOutcomes = []
    float valuation = 0.0 // if risk is realized: negative means cost, positive means benefit - currency is US dollars

    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['valuationString', 'probabilityString'])
    }

    String toString() {
        return "$probabilityString% with ${valuation < 0 ? 'costs' : 'benefits'} of \$$valuationString"
    }

    void setProbability(float val) {
        probability = Math.min(1.0, Math.max(0.0, val))
    }

    void setValuationString(String s) {
        try {
            valuation = Float.valueOf(s)
        }
        catch (Exception e) {
            valuation = 0.0
        }
    }

    String getValuationString() {
        return "${String.format('%.2f',valuation)}"
    }

    void setProbabilityString(String s) {
        int val
         try {
             val = Integer.valueOf(s)
         }
         catch (Exception e) {
             val = 100
         }
        setProbability((val / 100) as float)
     }

     String getProbabilityString() {
         return "${Math.round(probability * 100)}"
     }

}