package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.bean.IBeanDomain

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 3, 2008
* Time: 10:57:05 PM
* To change this template use File | Settings | File Templates.
*/
class BeanReferenceControl extends AbstractUIElement {

    IBeanReference beanReference
    Map domainBeans = [:]

    BeanReferenceControl(IBeanReference beanReference, BeanXForm xform) {
        super((Expando) beanReference.metadata, xform)
        this.beanReference = beanReference
        initialize()
    }

    void initialize() {
        super.initialize()
        domainBeans = getReferenceDomain()
    }

    // Temporary, poor man's bean picker -- a simple select1
    void build(def builder, String xf) {
        builder."$xf:group"(getAttributes()) {
            builder."$xf:label"(this.label)
            builder."$xf:select1"() {
                builder."$xf:label"('Choose one')
                domainBeans.each {label, bean ->
                    builder."$xf:item "{
                        builder."$xf:label"(label)
                        builder."$xf:value"() {
                            id(bean.id)
                        }
                    }
                }
            }
        }
    }

    private void getReferenceDomain() {
        IBeanDomain beanDomain = beanReference.domain
        String rootBeanId = beanDomain.id ?: this.xform.bean.id
        String rootBeanDb = beanDomain.db ?: beanReference.getDb()
        assert beanDomain, "domain must be defined in $beanReference"
        if (beanDomain.isDefined()) {  // if domain is not undefined
            use(NetKernelCategory, PersistentBeanCategory) {
                String queryUri = "${BeanXForm.INTERNAL_METAMODEL_QUERY_URI_PREFIX}/${this.xform.subjectName()}/${beanDomain.query}"
                String queryString = this.xform.context.sourceString(queryUri)
                GPathResult result = this.xform.context.sourceXML('active:data_memory',
                        [id: data(rootBeanId),
                                db: data(rootBeanDb),
                                args: map(beanDomain.args),
                                query: string(queryString)])
                // result = <beans><bean id="..." db="...">label</bean>...</beans>
                result.bean.each {el ->
                    IPersistentBean pb = this.xform.context.retrievePersistentBean("${el.@id}", "${el.@db}")
                    domainBeans += ["$el": pb]
                }
            }
        }
    }

}