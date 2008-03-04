package com.mindalliance.channels.data.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.data.BeanMemory
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.data.util.PersistentBeanCategory
import com.mindalliance.channels.nk.accessors.AbstractDataAccessor

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
    // bean: a persistent bean (@id may not be set)
    // Responds with id of bean
    void create(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
           String db = context.sourceString("this:param:db")
            // Instantiate bean and initialize it from xml
               IPersistentBean bean = context.sourcePersistentBean("this:param:bean")
               String id
               boolean isRoot = false
               if (context.'id?') {  // if id given on create then bean is a root bean (not to be unloaded from memory unless deleted)
                   id = context.sourceString("this:param:id")
                   isRoot = true
               }
               else {
                id = BeanMemory.makeGUID(context)
               }
               bean.id = id
               bean.rooted = isRoot
               bean.db = db
               bean.createdOn = new Date()

               // Add bean to memory
               beanMemory.newBean(bean, context)
               context.respond(string(id))
        }
        // Update WorkingMemory
        // Golden thread?
    }
    // Get a persistent bean given its id
    // db: name of database
    // id: bean id
    // Responds with persistent bean
    void source(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            IPersistentBean bean = beanMemory.retrieveBean(db, id, context)
            context.respond(new PersistentBeanAspect(bean))
        }
    }
    // Update a bean from xml given its id
    // db: name of database
    // id: id of bean
    // bean: persistent bean
    // Responds with boolean
    void sink(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            IPersistentBean bean = context.sourcePersistentBean("this:param:bean")
            bean.db = db
            bean.id = id
            beanMemory.updateBean(bean, context)
        }
    }
    // Does a bean exist at a given id?
    // db: name of database
    // id: bean id
    // Responds with boolean
    void exists(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            boolean exists = beanMemory.isBeanExists(db, id, context)
            context.respond(bool(exists))
        }
    }
    // Delete a bean given its db name and id
    // db: name of database
    // id: bean id
    // Responds with boolean
    void delete(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            beanMemory.removeBean(db, id, context)
            context.respond(bool(true))
        }
    }


}