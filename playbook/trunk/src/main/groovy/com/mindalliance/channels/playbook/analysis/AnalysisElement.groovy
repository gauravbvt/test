package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.Identified
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.InferredRef
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty
import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.support.RefUtils

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:41:33 PM
*/
class AnalysisElement implements Referenceable, Identified {

    String id
    String rationale = ''

    AnalysisElement() {
       id = "${UUID.randomUUID()}"
    }

    Ref getReference() {
       return InferredRef.from(this)
    }

    Referenceable deref() {
         return this
    }

    Class formClass() { // TODO
        return null;
    }

    String getDb() {
        return null
    }

    void changed() {
        throw new RuntimeException("Analysis element $id not modifiable")
    }

    void changed(String propName) {
        throw new RuntimeException("Analysis element $id not modifiable")
    }

    void beforeStore(ApplicationMemory memory) { }

    void afterStore() { }

    void afterRetrieve() { }

    Ref persist() {
        return this.reference;
    }

    void delete() {
        throw new RuntimeException("Analysis element $id can not be deleted")
    }

    void commit() {}

    void reset() {}

     List<RefMetaProperty> metaProperties() {
        return [];
    }

    String getType() {
        return RefUtils.capitalize(shortClassName());
    }

    Ref find(String listPropName, Map<String, Object> args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<Ref> references() {
        return []
    }

    boolean save() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    void afterDelete() {}

    void makeConstant() {}

    boolean isConstant() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    Bean copy() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    void detach() {}

    void setFrom(Bean bean) {
        throw new RuntimeException("Analysis element $id can not be set from $bean")
    }

    Map beanProperties() {
        return [:]
    }

    String shortClassName() {// Default
        String cn = this.class.name
        String name = "${cn.substring(cn.lastIndexOf('.') + 1)}"
        return name
    }

   String makeLabel(int maxWidth) {
         return RefUtils.summarize(shortClassName(), maxWidth)
    }

    Map toMap() {
        throw new RuntimeException("Analysis element $id can not be exported")
    }

    void initFromMap(Map map) {
        throw new RuntimeException("Analysis element $id can not be imported")  
    }
}