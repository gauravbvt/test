package com.mindalliance.channels.nk.bean

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 14, 2008
* Time: 12:50:22 PM
* To change this template use File | Settings | File Templates.
*/
class BeanReference  extends AbstractBeanPropertyValue implements IBeanReference {

    String db
    String id
    String beanClass // optional
    IBeanDomain domain  // optional but only when BeanReference is generated from a query

    IBeanReference deepCopy() {
        IBeanReference copy = new BeanReference(beanClass: beanClass, db: this.@db, id: id, domain:domain)
        return copy
    }

    String getDb() {
        return db ?: (contextBean ? contextBean.db : null)
    }

    String getSchemaType() {
        return ":beanref"     // starts with ':' means custom type
    } 
                                                              
}