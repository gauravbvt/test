package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.Named

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:48:16 PM
*/
abstract class ElementType extends ModelElement implements Named, Described {

    String name = ''           // -- required
    String description = ''
    List<? extends ElementType> narrowedTypes  = []

    String toString() {
        return name
    }

    void broaden(Ref type) {
       type.addNarrowedType(this)
    }

    void narrow (Ref type) {
        this.addNarrowedType(type)
    }

    boolean narrows(Ref type) {
        return this.narrowedTypes.contains(type)
    }

    boolean broadens(Ref type) {
        return type.narrowedTypes.contains(this)
    }

    List<Ref> ancestors() {
        List<Ref> ancestors = []
        ancestors.addAll(narrowedTypes)
        narrowedTypes.each {ancestors.addAll(it.ancestors())}
        return ancestors
    }

    boolean implies(Ref type) {
        return this == type || ancestors().contains(type)
    }

}