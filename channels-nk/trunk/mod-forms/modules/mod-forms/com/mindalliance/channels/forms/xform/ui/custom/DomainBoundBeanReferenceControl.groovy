package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.ui.SelectOneOrMany
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.forms.xform.XForm

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 3, 2008
* Time: 10:57:05 PM
* To change this template use File | Settings | File Templates.
*/
class DomainBoundBeanReferenceControl extends AbstractBeanReferenceControl {

    SelectOneOrMany select

    DomainBoundBeanReferenceControl(IBeanReference beanReference, BeanXForm xform) {
        super(beanReference, xform)
        initialize()
    }

    void initialize() {
        super.initialize()
        // Compose URI to get the domain of the BeanReference property
        metadata.choices = "${BeanXForm.DOMAIN_QUERY_URI_PREFIX}/${xform.bean.id}/${xform.bean.db}/${metadata.propertyName}"
        // Create a Select1 with itemset from instance
        select = new SelectOneOrMany(BeanXForm.ONE, metadata, xform)
    }

    // Temporary, poor man's bean picker -- a simple select1
    void build(def builder, String xf) {
        builder."$xf:group"() {
            select.build(builder, xf)
        }
    }

}