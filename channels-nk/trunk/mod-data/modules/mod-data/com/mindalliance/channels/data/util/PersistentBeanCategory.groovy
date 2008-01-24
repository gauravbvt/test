package com.mindalliance.channels.data.util

import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.NetKernelCategory
import com.ten60.netkernel.urii.IURRepresentation
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.data.BeanMemory
import com.mindalliance.channels.data.BeanRequestContext
import com.ten60.netkernel.urii.aspect.StringAspect
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.nk.bean.IBean
import com.mindalliance.channels.nk.bean.IBeanList

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
        IPersistentBean bean = ((IAspectPersistentBean) context.transrept(representation, IAspectPersistentBean.class)).getPersistentBean()
        return bean
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
        IAspectPersistentBean aspect = (IAspectPersistentBean) context.transrept(new StringAspect(xml), IAspectPersistentBean.class)
        return aspect.getPersistentBean()
    }

    static IPersistentBean dereference(IBeanReference beanReference) {
        IPersistentBean bean
        String id = beanReference.id
        String db = beanReference.db
        String beanClass = beanReference.beanClass
        if (id != null) {
            assert db != null && db.size() != 0
            assert beanClass != null && beanClass.size() != 0
            Context context = BeanRequestContext.getRequestContext()
            BeanMemory beanMemory = BeanRequestContext.getBeanMemory()
            bean = beanMemory.retrieveBean(db, id, context)
            if (bean) {
                assert bean.class.name == beanClass
            }
            else {
                // clean up dangling reference
                beanReference.id = null;
            }
        }
        return bean
    }

    static List trans(Object obj, String propName) {
        List set = []
        use(PersistentBeanCategory) {
            switch (obj) {
                case IBean:
                    List vals = [] + obj?."$propName"
                    vals.each {val ->
                        String cn = val.class.name
                        set.add(val)
                        List tc = val.trans(propName)
                        set.addAll(tc)
                    }
                    break
                case IBeanList:
                    obj.each {item ->
                        String cn = item.class.name
                        List tc = item.trans(propName)
                        set.addAll(tc)
                    }
                    break
                case IBeanReference:
                    IPersistentBean bean = obj.dereference()
                    List tc = bean.trans(propName)
                    set.addAll(tc)
                    break
                default: break
            }
        }
        return set
    }

    static IAspectPersistentBean persistentBean(Object obj, IPersistentBean bean) {
        return new PersistentBeanAspect(bean)
    }

    static Object get(IBeanReference beanRef, String name) {
        if (['id', 'db', 'beanClass', 'contextBean'].contains(name)) {
            return beanRef.@"$name"
        }
        else {
            Object value
            use(PersistentBeanCategory) {
                IPersistentBean bean = beanRef.dereference()
                value = bean."$name"
            }
            return value
        }
    }

    static Object invokeMethod(IBeanReference beanRef, String name, Object args) {
        Object value
        use(PersistentBeanCategory) {
            IPersistentBean bean = beanRef.dereference()
            value = bean.invokeMethod(name, args)
        }
        return value

    }

    /*    // Intercept access to a property containing a BeanReference (dereference it if possible)
    static def getProperty(AbstractPersistentBean bean, String name) {
        def value = bean.@"$name" // access field directly
        if (value instanceof IBeanReference) {
            def beanReference = value
            IPersistentBean refBean = beanReference.dereference()
            return refBean
        }
        else {
            return value
        }
    }*/

}