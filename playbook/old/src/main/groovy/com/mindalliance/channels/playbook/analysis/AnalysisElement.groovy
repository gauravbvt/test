package com.mindalliance.channels.playbook.analysis

import com.mindalliance.channels.playbook.Identified
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.impl.InferredRef
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.ref.impl.RefMetaProperty
import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 2:41:33 PM
*/
abstract class AnalysisElement extends ReferenceableImpl implements Identified {

    String id
    String rationale = ''

    protected List<String> transientProperties() {
        return super.transientProperties() + ['rationale']
    }
    

    AnalysisElement() {
       id = RefUtils.makeUUID()
    }

    String about() {
        return toString() // DEFAULT
    }

    String toString() {
        return "${this.type} ($rationale)" 

    }

    Ref getReference() {
       return InferredRef.from(this)
    }

    Referenceable deref() {
         return this
    }

    Class formClass() {
        String type = getType()
        return PlaybookApplication.current().formClassFor(type)
    }


    String getDb() {
        return null
    }

    void changed() {}

    void changed(String propName) {}

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

    String shortClassName() {
      return RefUtils.shortClassName(this)
    }

    String getType() {
        return shortClassName()
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

    String makeLabel(int maxWidth) {
        return RefUtils.asText("${RefUtils.deCamelCase(this.type)}: ${this.labelText()}", maxWidth)
    }

    String labelText() { // DEFAULT
        return rationale
    }


    Map toMap() {
        throw new RuntimeException("Analysis element $id can not be exported")
    }

    void initFromMap(Map map) {
        throw new RuntimeException("Analysis element $id can not be imported")  
    }
}