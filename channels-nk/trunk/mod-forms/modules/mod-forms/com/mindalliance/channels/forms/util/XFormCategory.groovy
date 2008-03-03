package com.mindalliance.channels.forms.util

import com.mindalliance.channels.nk.PersistentBeanHelper
import com.mindalliance.channels.nk.bean.IBeanReference
import groovy.xml.MarkupBuilder
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.data.util.PersistentBeanCategory

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 28, 2008
* Time: 8:28:32 PM
* To change this template use File | Settings | File Templates.
*/
class XFormCategory {

    // Override PersistenBeanHelper.buildBeanReference to add name and description
    static void buildBeanReference(PersistentBeanHelper persistentBeanHelper, String propKey, IBeanReference beanReference, MarkupBuilder builder) {
        if (!beanReference.isCalculated()) {
            Map attributes = [beanRef: beanReference.beanClass]
            def id = (beanReference.id != null) ? "${beanReference.id}" : '';
            def db = (beanReference.db != null) ? "${beanReference.db}" : '';
            if (beanReference.isDomainBound()) attributes += [domain: beanReference.domain.toString()]
            builder."${propKey}"(attributes) {
                builder.db(db)
                builder.id(id)
                if (id) {
                    use(PersistentBeanCategory) {
                        IPersistentBean ref = context.retrievePersistentBean(id, db)
                        if (ref) {
                            builder.name(ref.name.value)
                            builder.description(ref.description.value)
                        }
                    }
                }
            }
        }
    }

}