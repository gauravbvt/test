package com.mindalliance.channels.playbook.ifm.taxonomy

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Channels

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 30, 2008
 * Time: 2:43:50 PM
 */
abstract class TaxonomyElement extends IfmElement implements InTaxonomy {

    private Ref cachedTaxonomy

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['taxonomyElement', 'cachedTaxonomy', 'taxonomy'])
    }

    Set keyProperties() {
        return (super.keyProperties() + ['name', 'description']) as Set
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['taxonomyElement']) as Set
    }

     void detach() {
        super.detach()
        cachedTaxonomy = null
    }

    boolean isTaxonomyElement() {
        return true
    }

    Ref getTaxonomy() {
         if (cachedTaxonomy == null) {
             cachedTaxonomy = (Ref)Query.execute(Channels.instance(), "findTaxonomyOfElement", this.reference)
         }
         return cachedTaxonomy

     }


}