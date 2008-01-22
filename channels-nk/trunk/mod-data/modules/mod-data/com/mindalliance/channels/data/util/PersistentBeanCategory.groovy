package com.mindalliance.channels.data.util

import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.bean.BeanReference
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.NetKernelCategory
import com.ten60.netkernel.urii.IURRepresentation
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.data.BeanMemory
import com.mindalliance.channels.data.BeanRequestContext
import com.ten60.netkernel.urii.aspect.StringAspect

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:33:06 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanCategory {

    static IPersistentBean sourcePersistentBean(Context context, String uri) {
        IPersistentBean bean = ((IAspectPersistentBean) context.sourceAspect(uri, IAspectPersistentBean.class)).getPersistentBean()
        return bean
    }

    static IPersistentBean getPersistentBean(Context context, IURRepresentation representation) {
       IPersistentBean bean = ((IAspectPersistentBean)context.transrept(representation, IAspectPersistentBean.class)).getPersistentBean()
    }

    static IPersistentBean sourcePersistentBean(Context context, String uri, Map args) {
        IPersistentBean bean
        use(NetKernelCategory) {
            def rep = context.subrequest(uri, args)
            bean = getPersistentBean(context, rep)
        }
        return bean
    }

    static IPersistentBean toPersistentBean(Context context, String xml) {
        IAspectPersistentBean aspect = (IAspectPersistentBean)context.transrept(new StringAspect(xml), IAspectPersistentBean.class)
        return aspect.getPersistentBean()
    }

    static IPersistentBean dereference(BeanReference beanReference) {
        IPersistentBean bean
        if (beanReference.id.size()) {
            assert beanReference.db.size() != 0
            assert beanReference.beanClass.size() != 0
            Context context = BeanRequestContext.getRequestContext()
            BeanMemory beanMemory = BeanRequestContext.getBeanMemory()
            bean = beanMemory.retrieveBean(beanReference.db, beanReference.id, context)
            if (bean) {
                assert bean.class.name == beanReference.beanClass
            }
            else {
                // clean up dangling reference
                beanReference.id = null;
            }
        }
        return bean
    }

    static  IAspectPersistentBean persistentBean(Object obj, IPersistentBean bean) {
        return new PersistentBeanAspect(bean)
    }

    // Intercept access to a property containing a BeanReference (dereference it if possible)
    static def getProperty(AbstractPersistentBean bean, String name) {
        def value = bean.@"$name" // access field directly
        if (value instanceof BeanReference) {
            def beanReference = value
            IPersistentBean refBean = beanReference.dereference()
            return refBean
        }
        else {
            return value
        }
    }

}