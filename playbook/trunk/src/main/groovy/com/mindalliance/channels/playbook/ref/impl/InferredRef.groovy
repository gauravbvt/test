package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.analysis.AnalysisElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.support.drools.RuleBaseSession

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2008
 * Time: 11:25:36 AM
 */
class InferredRef extends RefImpl {

    AnalysisElement analysisElement

    InferredRef() {}


    static InferredRef from(AnalysisElement analysisElement) {
        InferredRef ir = new InferredRef()
        ir.setId(analysisElement.id)
        ir.@analysisElement = analysisElement
        return ir
    }

    String toString() {
        return "InferredRef<$id,$db>"
    }

    boolean isInferred() {
        return true
    }

    boolean isAttached() {
        return analysisElement != null
    }

    void detach() {
        analysisElement = null
    }

    Referenceable deref() {
        if (getId() == null) return null
        if (!analysisElement) {
            // query RuleBaseSession for AnalysisElement of same id. If none return null.
            RuleBaseSession ruleBaseSession = PlaybookApplication.current().ruleBaseSession
            analysisElement = (AnalysisElement)ruleBaseSession.deref(getId())
        }
        return analysisElement
    }

    // Do nothing Ref methods

    void delete() {
        throw new Exception("Can't delete a computed ref")
    }

    void commit() {
        // do nothing
    }

    void reset() {
        // do nothing
    }

    boolean save() {
        return true
    }

    Ref persist() {
        return this
    }

    boolean isModifiable() {
        return false
    }

    void changed(String propName) {
        throw new RuntimeException("Can't change a constant ref")
    }

    List<Ref> references() {
        return []
    }

    boolean isModified() {
        return false
    }
}