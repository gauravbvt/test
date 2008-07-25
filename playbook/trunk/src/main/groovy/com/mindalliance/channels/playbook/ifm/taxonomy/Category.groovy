package com.mindalliance.channels.playbook.ifm.taxonomy

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
abstract class Category extends TaxonomyElement implements Named, Described {

    String name = ''           // -- required
    String description = ''
    List<Ref> narrowedTypes  = []

    Set keyProperties() {
        return (super.keyProperties() + ['name', 'description']) as Set
    }

    String toString() {
        return name ?: "Unnamed"
    }

     boolean isCategory() {
        return true
    }

    void broaden(Ref type) {
       type.addNarrowedType(this.reference)
    }

    void narrow (Ref type) {
        this.addNarrowedType(type)
    }

    boolean narrows(Ref type) {
        return this.narrowedTypes.contains(type)
    }

    boolean broadens(Ref type) {
        return type.narrowedTypes.contains(this.reference)
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