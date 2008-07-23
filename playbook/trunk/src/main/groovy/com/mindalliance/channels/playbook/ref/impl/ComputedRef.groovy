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
class ComputedRef extends RefImpl {  // TODO - implement AbstractRefImpl and subclass

    Referenceable computedValue

    static ComputedRef from(Class clazz, String method) {
        ComputedRef cref = new ComputedRef()
        cref.setId("${clazz.name},$method")
        return cref
    }

    String toString() {
        return "ComputedRef<$id,$db>"
    }

    boolean isComputed() {
        return true
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

    Referenceable deref() {   // returns Referenceable from session change set or UNCOPIED referenceable from application
        if (!computedValue) {
            computedValue = computeReferenceable()
            computedValue.makeConstant()
        }
        return computedValue
    }

    void detach() {
        use (NoSessionCategory) {
            computed = null
        }
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

    boolean isFresh() {
        return true
    }
}