package com.mindalliance.channels.data.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractDataAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.data.beans.Channels
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.data.BeanMemory
import com.mindalliance.channels.nk.channels.IPersistentBean
import com.mindalliance.channels.data.BeanXMLConverter

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 13, 2008
* Time: 8:07:18 PM
* To change this template use File | Settings | File Templates.
*/
class BeanAccessor extends AbstractDataAccessor {

    // Add a new bean from xml to bean graph
    // db: name of database
    // id: a unique and well-known id (the bean becomes "rooted" if given)  [optional]
    // doc: xml document describing the bean (@id not set)
    // Responds with id of bean as <id>someId</id>
    void create(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
            // Instantiate bean and initialize it from xml
           GPathResult xml = context.sourceXML("this:param:doc")
           String db = context.sourceString("this:param:db")
           String id
           boolean isRoot = false
           if (context.'id?') {  // if id given on create then bean is a root bean (not to be unloaded from memory unless deleted)
               id = context.sourceString("this:param:id")
               isRoot = true
           }
           else {
            id = BeanMemory.makeGUID(context)
           }
           IPersistentBean bean = AbstractPersistentBean.newPersistentBean(db, id, beanClass)
           bean.rooted = true
           new BeanXMLConverter(context).initBeanFromXml(bean, xml)
           // Add bean to memory
           BeanContext.getBeanMemory().newBean(bean, context)
           context.respond(string("<id>$id</id>"))
        }
        // Update WorkingMemory
        // Golden thread?
    }
    // Get xml from a bean given its id
    // If a query is named, then the xml is from the query applied to the IDed bean
    // else the xml for the IDed bean is returned
    // db: name of database
    // id: bean id
    // beanClass: the bean's class name
    // Responds with xml
    void source(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            String beanClass = context.sourceString("this.param:beanClass")
            IPersistentBean bean = BeanContext.getBeanMemory().retrieveBean(beanClass, db, id, context)
            String xml = new BeanXMLConverter(context).xmlFromBean(bean)
            context.respond(string(xml))
        }
    }
    // Update a bean from xml given its id
    // db: name of database
    // doc: xml document serializing the bean (@id set)
    // Responds with boolean
    void sink(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            GPathResult xml = context.sourceXML("this:param:doc")
            BeanContext.getBeanMemory().updateBean()
        }
    }
    // Does a bean exist at a given id?
    // db: name of database
    // id: bean id
    // Responds with boolean
    void exists(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            boolean exists = BeanContext.getBeanMemory().isBeanExists(db, id, context)
            context.respond(bool(exists))
        }
    }
    // Delete a bean given its db name and id
    // db: name of database
    // id: bean id
    // Responds with boolean
    void delete(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            boolean exists = BeanContext.getBeanMemory().removeBean(db, id, context)
            context.respond(bool(exists))
        }
    }

    // Put context and beanGraph into thread local
    private void initBeanContext(Context context) {
        use(NetKernelCategory) {
            BeanMemory beanMemory = BeanMemory.getInstance()
            BeanContext.setRequestContext(context)
            BeanContext.setBeanMemory(beanMemory)
        }
    }

}