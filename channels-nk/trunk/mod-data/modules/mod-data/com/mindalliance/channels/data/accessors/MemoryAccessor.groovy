package com.mindalliance.channels.data.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.data.BeanMemory
import com.mindalliance.channels.nk.accessors.AbstractDataAccessor

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 16, 2008
* Time: 9:49:14 AM
* To change this template use File | Settings | File Templates.
*/

// Search or refresh
class MemoryAccessor extends AbstractDataAccessor {

    // Executes a named query from root bean by default or from identified bean
    // db: database name for root bean
    // id: root bean id
    // args: a NVP with query arguments (optional)
    // query: the uri of a query (a Groovy closure that produces XML when evaluated on the target bean)
    // Responds: xml
    void source(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            String query = context.sourceString("this:param:query")
            Map args = [:]
            if (context.'args?') args = context.sourceNVP('this:param:args')
            String xml = beanMemory.search(db, id, args, query, context)
            context.respond(string(xml))
        }
    }

    // Clears memory of all beans except for the root beans - will force reloads from databases
    // Responds: true
    void create(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
          beanMemory.refresh()
          context.respond(bool(true))
        }
    }

    // Remove all bean from a database and delete the database
    void delete(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
          String db = context.sourceString("this:param:db")
          beanMemory.deleteDB(db, context)
          context.respond(bool(true))
        }
    }

    // Add a batch of beans from xml
    // db: name of db
    // beans: xml containing batch of beans
    // Responds with the number of beans added
    void sink(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
        use(NetKernelCategory, PersistentBeanCategory) {
            String db = context.sourceString("this:param:db")
            String uri = context.beans
            int count = beanMemory.newBeans(db, uri, context)
            context.respond(string("$count"))
        }

    }

    // Is db known to memory?
    void exists(Context context) {
        BeanMemory beanMemory = BeanMemory.getInstance()
         use(NetKernelCategory, PersistentBeanCategory) {
           String db = context.sourceString("this:param:db")
           boolean exists = beanMemory.dbExists(db, context)
           context.respond(bool(exists))
         }
    }

}