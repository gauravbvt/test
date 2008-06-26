package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.mem.NoSessionCategory

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 21, 2008
 * Time: 2:24:35 PM
 */
// Holds a constant, non-persistent referenceable
class ComputedRef extends RefImpl {

    private Referenceable referenceable

    static ComputedRef from(Class clazz, String method) {
        ComputedRef cref = new ComputedRef()
        cref.setId("${clazz.name},$method")
        return cref
    }

    String toString() {
        return "ConstantRef<$id,$db>"
    }

    boolean isComputed() {
        return true
    }

    Referenceable deref() {   // returns Referenceable from session change set or UNCOPIED referenceable from application
        if (!referenceable) {
            referenceable = computeReferenceable()
            referenceable.makeConstant()
        }
        return referenceable
    }

    Referenceable computeReferenceable() {
        List<String> list = id.tokenize(',')
        Class clazz = Class.forName((String)list[0])
        String methodName = (String)list[1]
        Referenceable result = null
        use (NoSessionCategory) {
            result = (Referenceable)this.metaClass.invokeStaticMethod(clazz, methodName, null)
        }
        return result
    }

    void delete() {
        throw new Exception("Can't delete a constant ref")
    }

    void commit() {
        // do nothing
    }

    boolean isModifiable() {
        return false
    }

    void changed(String propName) {
        throw new Exception("Can't change a constant ref")
    }

    List<Ref> referefences() {
        return []
    }

    boolean isModified() {
        return false
    }
}