package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.support.persistence.PersistentRef
import com.mindalliance.channels.playbook.support.persistence.CacheEntryBean
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.support.persistence.Mappable
import com.mindalliance.channels.playbook.support.Mapper

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2008
 * Time: 6:56:49 AM
 */
class BeanImpl implements Bean {

    String version         // TODO -- belongs in IfmElement

    String getVersion() {
        return '1.0.0' // default
    }

    Bean copy() {
        Bean copy = (Bean) this.class.newInstance()
        beanProperties().each {name, val ->
            try {
                def value
                switch (val) {
                    case {it instanceof Class}: value = val; break
                    case Bean.class: value = val.copy(); break
                    case Cloneable.class: value = val.clone(); break
                    default: value = val
                }
                copy."$name" = value
            }
            catch (Exception e) {// Read-only/computed field
                Logger.getLogger(this.getClass().getName()).warn("Can't copy field $name in ${this.class.name}")
            }
        }
        return copy
    }

    protected List<String> transientProperties() {
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
                    Logger.getLogger(this.getClass().getName()).warn("Can't set field $name in ${bean.class.name}", e)
                }
            }
        }
    }

    // Detach any field value that should or can not be serialized
    void detach() {} // do nothing

    // ****** Persistence support for YAML

    void initFromMap(Map map) {
        // TODO -- manage versioning here
        Set propNames = beanProperties().keySet();
        map.each {key, val ->
            if (propNames.contains(key)) {
                def value = Mapper.valueFromPersisted(val)
                this."$key" = value
            }
        }
    }

    List<Ref> references() {
        List<Ref> references = findAllReferencesIn(this)
        return references
    }

    static List<Ref> findAllReferencesIn(def obj) {
        List<Ref> references = []
        switch (obj) {
            case Ref:
                references.add(obj)
                break
            case Bean:
                obj.beanProperties().each{key, val ->
                    references.addAll(findAllReferencesIn(val))
                }
                break
            case List:
                obj.each { references.addAll(findAllReferencesIn(it)) }
        }
        return references
    }

    Map toMap() {
        Map map = [:]
        beanProperties().each {key, val ->
            def value = Mapper.toPersistedValue(val)
            map.put(key, value)
        }
        map.put(Mappable.CLASS_NAME_KEY, this.class.name)
        return map
    }

}