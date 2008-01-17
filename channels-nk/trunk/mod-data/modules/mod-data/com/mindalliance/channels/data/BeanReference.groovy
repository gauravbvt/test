package com.mindalliance.channels.data

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.data.BeanContext
import com.mindalliance.channels.nk.channels.IPersistentBean

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 12:50:22 PM
 * To change this template use File | Settings | File Templates.
 */
class BeanReference {

    String db;
    String id;
    String beanClass;

    IPersistentBean dereference() {
        IPersistentBean bean
        if (id.size()) {
            assert db.size() != 0
            assert beanClass.size() != 0
            Context context = BeanContext.getRequestContext()
            BeanMemory beanMemory = BeanContext.getBeanMemory()
            bean = beanMemory.retrieveBean(beanClass, db, id, context)
            if (bean) {
                assert bean.class.name == beanClass
            }
            else {
                // clean up dangling reference
                id = null;
            }
        }
        return bean
    }

}