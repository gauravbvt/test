package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.support.PlaybookApplication

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 29, 2008
 * Time: 1:31:00 PM
 */
abstract class AbstractReferenceableImpl implements Referenceable {

    public Set<Class<?>> childClasses() {
        return [] as Set<Class<?>>
    }

    String getDb() {
        return null;
    }

    public Referenceable deref() {
        return this;
    }

    public List<RefMetaProperty> metaProperties() {
        return [];
    }

    Class formClass() {
        String type = getType()
        return PlaybookApplication.current().formClassFor(type)
    }

    void changed() {}

    void changed(String propName) {}

    void beforeStore(ApplicationMemory memory) {}

    void afterStore() {}

    void afterRetrieve() {}

    Ref persist() {
        return this.reference;
    }

    void delete() {}

    void commit() {}

    void reset() {}

    String getType() {
        return RefUtils.shortClassName(this);
    }

    Ref find(String listPropName, Map<String, Object> args) {
        return null;
    }

    List<Ref> references() {
        return null;
    }

    boolean save() {
        return false;
    }

    void afterDelete() {}

    void makeConstant() {}

    Set hiddenProperties() {
        return new HashSet();
    }

    Set keyProperties() {
        return new HashSet();
    }

    List<Ref> children() {
        return [];
    }

    List<Ref> family() {
        return [];
    }

    void markDeleted() {}

    Bean copy() {
        return this;
    }

    void detach() {}

    void setFrom(Bean bean) {}

    Map beanProperties() {
        return [:];
    }

    String shortClassName() {
        return RefUtils.shortClassName(this);
    }

    Map<String,Object> toMap() {
        return null;
    }

    void initFromMap(Map<String,Object> map) {}

}