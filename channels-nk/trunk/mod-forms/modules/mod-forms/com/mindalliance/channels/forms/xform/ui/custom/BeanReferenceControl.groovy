package com.mindalliance.channels.forms.xform.ui.custom

import com.mindalliance.channels.forms.xform.ui.AbstractUIElement
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.forms.xform.BeanXForm
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.data.util.PersistentBeanCategory
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.bean.BeanReference
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
    List domainBeans

    BeanReferenceControl(IBeanReference beanReference, BeanXForm xform) {
        super((Expando)beanReference.metadata, xform)
        this.beanReference = beanReference
        initialize()
    }

    void initialize() {
        super.initialize()
        domainBeans = getReferenceDomain()
    }

    // Temporary, poor man's bean picker -- a simple select1
    void build(def xf) {
        xf.group(getAttributes()) {
            xf.label(this.label)
            xf.select1() {
                xf.label('Choose one')
                domainBeans.each {bean ->
                    xf.item {
                        label(bean.metadata.label)
                        value {
                            id(bean.id)
                            }
                        }
                    }
                }
            }
        }

    private List getReferenceDomain() {
        List beans
        IBeanDomain beanDomain = beanReference.domain
        String rootBeanId = beanDomain.id ?: this.contextBean.id
        String rootBeanDb = beanDomain.db ?: this.getDb()
        assert beanDomain.query
        use(NetKernelCategory, PersistentBeanCategory) {
            def nvp = context.toNVP(beanDomain.args)
            String queryUri = "${BeanXForm.INTERNAL_METAMODEL_QUERY_URI_PREFIX}/${this.xform.subjectName()}/${beanDomain.query}"
            String queryString = context.sourceString(queryUri)
            GPathResult result = context.sourceXML('active:data_memory',
                            [id: data(rootBeanId),
                             db: data(rootBeanDb),
                             args: nvp,
                             query: string(queryString)])
            // result = <beans><bean id="..." db="...">label</bean>...</beans>
            result.each {el ->
                BeanReference br = new BeanReference(id: el.@id, db: el.@db)
                IPersistentBean pb = br.dereference()
                pb.metadata.label = el.text()
                beans.add(pb)
            }
        }
        return beans
    }

}