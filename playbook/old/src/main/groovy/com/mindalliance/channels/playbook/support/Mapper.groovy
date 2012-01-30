package com.mindalliance.channels.playbook.support

import com.mindalliance.channels.playbook.support.persistence.Mappable
import com.mindalliance.channels.playbook.support.persistence.PersistentRef
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Playbook

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 20, 2008
 * Time: 7:01:07 PM
 */
class Mapper {

    static Mappable fromMap(Map map) {
        String className = (String) map.get(Mappable.CLASS_NAME_KEY)
        Class aClass = Class.forName(className)
        Mappable mappable = (Mappable) aClass.newInstance()
        mappable.initFromMap(map)
        return mappable
    }

    static def valueFromPersisted(def val) {
        def value
        switch (val) {
            case PersistentRef:
                value = val.toRef()
                break
            case Map:
                if (val.containsKey(Mappable.CLASS_NAME_KEY)) {
                    value = Mapper.fromMap(val)
                }
                else {
                    value = val
                }
                break
            case List:
                value = []
                val.each {item ->
                    value.add(valueFromPersisted(item))
                }
                break
            default:
                value = val
        }
        return value
    }

    static def toPersistedValue(def val) {
        def value
        switch (val) {
            case Ref:
                value = PersistentRef.fromRef((Ref) val)
                break
            case List:
                List pList = []
                val.each {item ->
                    pList.add(toPersistedValue(item))
                }
                value = pList
                break
            case Mappable:
                value = val.toMap()
                break
            default:
                value = val
        }
        return value
    }
}