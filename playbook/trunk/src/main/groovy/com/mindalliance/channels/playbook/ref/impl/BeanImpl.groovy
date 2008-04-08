package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.persistence.PersistentRef
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.support.persistence.CacheEntryBean

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 26, 2008
* Time: 6:56:49 AM
*/
class BeanImpl implements Bean {

    String version

    String getVersion() {
        return '1.0.0' // default
    }

    Bean copy() {
        Bean copy = (Bean) this.class.newInstance()
        beanProperties().each {name, val ->
            try {
                def value
                switch (val) {
                    case Bean.class: value = val.copy(); break
                    case Cloneable.class: value = val.clone(); break
                    default: value = val
                }
                copy."$name" = value
            }
            catch (Exception e) {// Read-only/computed field
                // TODO -- put a warning in log
                // System.out.println("Can't set field $name in $copy")
            }
        }
        return copy
    }

    protected List transientProperties() {
        return ['class', 'metaClass']
    }

    Map beanProperties() {
        return getProperties().findAll {name, val -> !transientProperties().contains(name)}
    }

    void setFrom(Bean bean) {
        if (bean) {
            if (!this.class.isAssignableFrom(bean.class)) throw new IllegalArgumentException("Can't copy from $bean")
            bean.beanProperties().each {name, val ->
                try {
                    this."$name" = val
                }
                catch (Exception e) {
                    // TODO -- put a warning in log
                    System.out.println("Can't set field $name in ${bean.class.name}")
                }
            }
        }
    }

    // Detach any field value that should or can not be serialized
    void detach() {} // do nothing

    // ****** Persistence support for YAML

    static Bean fromMap(Map map) {
        String className = (String) map.get(CacheEntryBean.CLASS_NAME_KEY)
        Class aClass = Class.forName(className)
        Bean bean = (Bean) aClass.newInstance()
        bean.initFromMap(map)
        return bean
    }

    void initFromMap(Map map) {
        // TODO -- manage versioning here
        Set propNames = beanProperties().keySet();
        map.each {key, val ->
            if (propNames.contains(key)) {
                def value = valueFromPersisted(val)
                this."$key" = value
            }
        }
    }

    static def valueFromPersisted(def val) {
        def value
        switch (val) {
            case PersistentRef:
                value = val.toRef()
                break
            case Map:
                if (val.containsKey(CacheEntryBean.CLASS_NAME_KEY)) {
                    value = fromMap(val)
                }
                else {
                    value = val
                }
                break
            case List:
                val.each {item ->
                    value = []
                    value.add(valueFromPersisted(item))
                }
                break
            default:
                value = val
        }
        return value
    }

    Map toMap() {
        Map map = [:]
        beanProperties().each {key, val ->
            def value = toPersistedValue(val)
            map.put(key, value)
        }
        map.put(CacheEntryBean.CLASS_NAME_KEY, this.class.name)
        return map
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
            case Bean:
                value = val.toMap()
                break
            default:
                value = val
        }
        return value
    }

}