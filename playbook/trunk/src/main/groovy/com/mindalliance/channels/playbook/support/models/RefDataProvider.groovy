package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.markup.repeater.data.IDataProvider
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.PathExpression
import com.mindalliance.channels.playbook.ref.impl.MetaProperty

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 7:16:56 PM
*/
class RefDataProvider implements IDataProvider {

    def source
    String path

    RefDataProvider(def obj, String path) {
        source = obj
        this.path = path
    }

    private List<Ref> allRefs() {
        return (List<Ref>)PathExpression.getNestedProperty(source, path)
    }

    Iterator iterator(int first, int count) {
        return allRefs().subList(first, first+count).iterator()
    }

    int size() {
        return allRefs().size()
    }

    IModel model(Object object) {
        Ref ref
        if (object instanceof Referenceable) {
               ref = object.reference
           }
           else if (object instanceof Ref) {
               ref = (Ref)object
           }
           else {
               throw new IllegalArgumentException("$object is neither a Ref or a Referenceable")
           }
        return new RefModel(ref)
    }

    List<MetaProperty> getColumns() {
        MetaProperty a
        Set<MetaProperty> set = new HashSet<MetaProperty>()
        allRefs().each {ref ->
            set.addAll(ref.metaProperties().findAll {it.isScalar()})
        }
        return set as List<MetaProperty>
    }

    void detach() {
       // Do nothing - nothing to detach with Ref's
    }

}