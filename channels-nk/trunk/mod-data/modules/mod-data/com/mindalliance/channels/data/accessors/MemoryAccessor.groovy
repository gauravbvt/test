package com.mindalliance.channels.data.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 16, 2008
 * Time: 9:49:14 AM
 * To change this template use File | Settings | File Templates.
 */

// Search or refresh
class MemoryAccessor extends AbstractAccessor {

    // operator query | refresh
    void source(Context context) {
        use(NetKernelCategory) {
            String operator = context.sourceString("this:param:operator")
            switch (operator) {
                case 'query': query(context); break;
                case 'refresh': refresh(context); break;
                default: throw new IllegalArgumentException("Invalid operator $operator ")
            }
        }
    }

    // Executes a named query from root bean by default or from identified bean
    // db: database name
    // id: bean id
    // beanClass: bean class name [optional if bean is rooted]
    // query: the uri of a query (a Groovy closure that produces XML when evaluated on the target bean)
    // Responds: xml
    void search(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
            String db = context.sourceString("this:param:db")
            String id = context.sourceString("this:param:id")
            String beanClass
            if (context.'beanClass?') beanClass = context.sourceString("this.param:beanClass")
            String xml = getBeanMemory().search(beanClass, db, id, context.query, context)
            context.respond(string(xml))
        }
    }

    // Clears memory of all beans except for the root beans - forcing reloads from databases
    // Responds: true
    void refresh(Context context) {
        initBeanContext(context)
        use(NetKernelCategory) {
          BeanContext.getBeanMemory().refresh()
          respond(bool(true))
        }
    }

}