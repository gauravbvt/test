package com.mindalliance.channels.playbook.support.models

import org.apache.wicket.model.IModel
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ref.Referenceable

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 23, 2008
* Time: 7:19:31 PM
*/
class RefModel implements IModel {

    Ref ref

    RefModel(def obj) {
        setObject(obj)
    }

    // Returns a Ref
    Object getObject() {
       return ref
    }

    void setObject(Object object) {
        if (object instanceof Referenceable) {
            ref = object.reference
        }
        else {   // must be a Ref
            ref = (Ref)object
        }
    }

    void detach() {
        // Nothing to do
    }

}