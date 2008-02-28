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
    String beanClass // used for type checking when set
    IBeanDomain domain // when domain-bound

    boolean isDomainBound() {
        return domain != null
    }

    boolean isOwned() {
        return !isCalculated() && !isDomainBound()
    }

     // Must be a JavaBean with id and, optionally, db properties
    void initializeFrom(Object initializer) {
        if (isCalculated()) throw new Exception("Can't set a calculated BeanReference")
        assert initializer.id
        id = initializer.id
        db = initializer.db
    }

    IBeanReference deepCopy() {
        IBeanReference copy
        if (isCalculated()) {
           copy = new BeanReference(beanClass: beanClass, calculate: this.calculate)
        }
        else if (isDomainBound()) {
           copy = new BeanReference(beanClass: beanClass, db: this.@db, id: id, domain:domain)
        }
        else {
            copy = new BeanReference(beanClass: beanClass, db: this.@db, id: id)
        }
        return copy
    }

    String getDb() {
        return db ?: (contextBean ? contextBean.db : null)
    }

    String getId() {
        if (isCalculated()) {    // The calculated ID must be in the same db as the context bean
            return (String)calculate()
        }
        else {
            return id
        }
    }

    void setId(String id) {
        if (isCalculated()) throw new Exception("Can't set ID of derived Bean Reference")
        this.id = id
    }

    String getSchemaType() {
        return ":beanref"  // starts with ':' means custom type
    } 

    void initContextBean(IPersistentBean bean) {
        assert bean
        super.initContextBean(bean)
    }

}