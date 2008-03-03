package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.nk.bean.IBeanList
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.ui.SelectOneOrMany

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Mar 1, 2008
* Time: 3:19:17 PM
* To change this template use File | Settings | File Templates.
*/
class DomainBoundReferenceList extends AbstractUIElement {

    SelectOneOrMany select
    IBeanList beanList

    DomainBoundReferenceList(IBeanList beanList, BeanXForm xform) {
        super((Expando)beanList.metadata, xform)
        this.beanList = beanList
        initialize()
    }

    void initialize() {
        super.initialize()
        // Compose URI to get the domain of the BeanReference property
        metadata.choices = "${BeanXForm.DOMAIN_QUERY_URI_PREFIX}/${xform.bean.id}/${xform.bean.db}/${metadata.propertyName}"
        // Create a Select with itemset from instance
        select = new SelectOneOrMany(BeanXForm.MANY, metadata, xform)
    }

    // Temporary, poor man's bean picker -- a simple select1
    void build(def builder, String xf) {
        builder."$xf:group"() {
            select.build(builder, xf)
        }
    }

}