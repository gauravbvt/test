package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.forms.xform.ui.SelectOneOrMany
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.nk.bean.IBeanDomain
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 3, 2008
* Time: 10:57:05 PM
* To change this template use File | Settings | File Templates.
*/
class BeanReferenceControl extends AbstractUIElement {

    IBeanReference beanReference
    Map domainBeans
    SelectOneOrMany select

    BeanReferenceControl(IBeanReference beanReference, BeanXForm xform) {
        super((Expando) beanReference.metadata, xform)
        this.beanReference = beanReference
        initialize()
    }

    void initialize() {
        super.initialize()
        // domainBeans = getReferenceDomain()
        createElements()
    }

    void createElements() {
        metadata.choices = getReferenceDomain()
        select = new SelectOneOrMany(!BeanXForm.MANY, metadata, xform)
    }

    // Temporary, poor man's bean picker -- a simple select1
    void build(def builder, String xf) {
        builder."$xf:group"() {
            select.build(builder, xf)
        }
    }

    private Map getReferenceDomain() {
        Map beans = [:]
        IBeanDomain beanDomain = beanReference.domain
        String rootBeanId = beanDomain.id ?: this.xform.bean.id
        String rootBeanDb = beanDomain.db ?: beanReference.getDb()
        assert beanDomain, "domain must be defined in $beanReference"
        if (beanDomain.isDefined()) {  // if domain is not undefined
            use(NetKernelCategory, PersistentBeanCategory) {
                String queryUri = "${this.xform.internalQueryUriPrefix}/${this.xform.subjectName()}/${beanDomain.query}"
                String queryString = this.xform.context.sourceString(queryUri)
                def result = this.xform.context.sourceDOM('active:data_memory',
                        [id: data(rootBeanId),
                                db: data(rootBeanDb),
                                args: map(beanDomain.args),
                                query: string(queryString)])
                // result = <items><item label=aLabel>xml</item>...</items>
                result.item.each {item ->
                    def value = item.children()[0]
                    value.@xmlns = ''
                    beans["${item.@label}"] =value.toXml()
                }
            }
        }
        return beans
    }

}