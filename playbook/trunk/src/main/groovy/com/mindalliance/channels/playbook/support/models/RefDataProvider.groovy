package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.markup.repeater.data.IDataProvider
import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.support.PathExpression

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 7:16:56 PM
*/
class RefDataProvider implements IDataProvider {

    def source
    List<Ref> refs
    String path

    RefDataProvider(def object, String path) {
        source = obj
        this.path = path
    }

    RefDataProvider(List items) {
       refs = []
       items.each() {item ->
           if (item instanceof Referenceable) {
               refs.add(item.reference)
           }
           else if (item instanceof Ref) {
               refs.add(item)
           }
           else {
               throw new IllegalArgumentException("$item is neither a Ref or a Referenceable")
           }
       }
    }

    private List<Ref> allRefs() {
        if (refs) {
            return refs
        }
        else {
            assert path && source
            return (List<Ref>)PathExpression.eval(source, path)
        }
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

    void detach() {
       // Do nothing - nothing to detach with Ref's
    }

}