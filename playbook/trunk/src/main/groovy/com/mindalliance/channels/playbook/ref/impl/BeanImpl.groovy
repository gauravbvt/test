package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ref.Ref
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.support.persistence.Mappable
import com.mindalliance.channels.playbook.support.Mapper
import com.mindalliance.channels.playbook.ifm.Named
import com.mindalliance.channels.playbook.support.PlaybookApplication

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2008
 * Time: 6:56:49 AM
 */
abstract class BeanImpl implements Bean {

    static Map<Class,List<MetaBeanProperty>> WritableProperties = [:]

    String version         // TODO -- belongs in IfmElement

    String getVersion() {
        return '1.0.0' // default
    }

   /* synchronized */ static List<MetaBeanProperty> writablePropertiesOf(BeanImpl beanImpl) {
        List metaBeanProperties = (List<MetaBeanProperty>)WritableProperties[beanImpl.class]
        if (!metaBeanProperties) {
            metaBeanProperties = beanImpl.metaClass.getProperties().findAll {mp ->
               !beanImpl.transientProperties().contains(mp.name)
            }
            WritableProperties[beanImpl.class] = metaBeanProperties
        }
        return metaBeanProperties
    }

    // WARNING: Copy sets to null Refs that are dangling in APPLICATION scope
    Bean copy() {
        Bean copy = (Bean) this.class.newInstance()
        beanProperties().each {name, val ->
            try {
                def value = BeanImpl.makeClone(val)
                copy."$name" = value
            }
            catch (Exception e) {// Read-only/computed field
                Logger.getLogger(this.getClass().getName()).warn("Can't copy field $name in ${this.class.name}")
            }
        }
        return copy
    }

    static Object makeClone(Object val) {
        def value
        switch(val) {
           case {it instanceof Class}: value = val; break
           case ComputedRef.class: value = val; break
           case Ref.class:
                if (PlaybookApplication.current().getMemory().isFresh((Ref)val)) {
                    value = val
                 }
                 else {
                    Logger.getLogger(Bean.class).warn("Stale reference $val replaced by null")
                    value = null
                 }
                 break
            case Bean.class: value = val.copy(); break
            case Collection.class:   // filter out nulls, (dangling Refs are cloned to null)
                value = val.class.newInstance()
                val.each {item ->
                    def clone = BeanImpl.makeClone(item)
                    if (clone != null) value.add(clone)
                }
                break
            case Cloneable.class: value = val.clone(); break
            default: value = val
        }
        return value
    }

    protected List<String> transientProperties() {
        return ['class', 'metaClass', 'from', 'writableProperties']
    }

    Map beanProperties() {
        Map<String,Object> properties = [:]
        BeanImpl.writablePropertiesOf(this).each {mbp ->
            properties[mbp.name] = mbp.getProperty(this)
        }
        return properties
        // return getProperties().findAll {name, val -> !transientProperties().contains(name)} // horrendously inefficient
    }

    boolean isWritableProperty(String name) {
        boolean writable = BeanImpl.writablePropertiesOf(this).any {mbp -> mbp.name == name}
        return writable
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
        map.each {key, val ->
            if (isWritableProperty(key)) {
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
        // System.out.println(this.class.name + ' to map')
        Map map = [:]
        beanProperties().each {key, val ->
            assert this != val
            def value = Mapper.toPersistedValue(val)
            map.put(key, value)
        }
        map.put(Mappable.CLASS_NAME_KEY, this.class.name)
        return map
    }

    String shortClassName() {// Default
        String cn = this.class.name
        String name = "${cn.substring(cn.lastIndexOf('.') + 1)}"
        return name
    }

    String makeLabel(int maxWidth) {
        String label = shortClassName()
        String name
        if (this instanceof Named) {
            name = ((Named)this).name ?: '?'
        }
        else {
            name = toString()
        }
        if (name.size() > maxWidth) name = name[0, maxWidth - 1]
        label += "\n$name"
        return label
    }

    String about() {   // DEFAULT
        return toString()
    }


}