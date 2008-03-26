package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Bean

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 26, 2008
* Time: 6:56:49 AM
*/
class BeanImpl implements Bean {

    Bean copy() {
        Bean copy = (Bean) this.class.newInstance()
        getProperties().each {name, val ->
            if (!transientProperties().contains(name)) {
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
        }
        return copy
    }

    protected List transientProperties() {
        return ['class', 'metaClass']
    }

    // Detach any field value that should or can not be serialized
    void detach() {} // do nothing

}