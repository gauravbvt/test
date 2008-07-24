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

    boolean isReadOnly() {
        return true
    }

    boolean isReadWrite() {
        return false
    }

    boolean lock() {
        return false
    }

    boolean unlock() {
        return false
    }

    void detach() {
        analysisElement = null
    }

    Referenceable deref() {
        if (getId() == null) return null
        if (!analysisElement) {
            // query RuleBaseSession for AnalysisElement of same id. If none return null.
            analysisElement = (AnalysisElement)RuleBaseSession.current().deref(getId())
        }
        return analysisElement
    }

    boolean isModifiable() {
        return true
    }

    // Do nothing Ref methods

    boolean delete() {
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

    void begin() {}

    Ref persist() {
        return this
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