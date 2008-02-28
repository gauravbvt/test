package com.mindalliance.channels.data.util

import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.NetKernelCategory
import com.ten60.netkernel.urii.IURRepresentation
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import com.mindalliance.channels.data.BeanMemory
import com.mindalliance.channels.data.util.BeanRequestContext
import com.ten60.netkernel.urii.aspect.StringAspect
import com.mindalliance.channels.nk.bean.IBeanReference
import com.mindalliance.channels.nk.bean.IBean
import com.mindalliance.channels.nk.bean.IBeanList
import com.ten60.netkernel.urii.aspect.IAspectString
import com.mindalliance.channels.nk.Registry

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:33:06 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanCategory {

    // ********** Context *************

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

    static IPersistentBean retrievePersistentBean(Context context, String id, String db) {
        IPersistentBean bean
        use(NetKernelCategory) {
            bean = sourcePersistentBean(context, 'active:data_bean', [id: data(id), db: data(db)])
        }
        return bean

    }

    static IPersistentBean toPersistentBean(Context context, String xml) {
        IAspectPersistentBean aspect = (IAspectPersistentBean) context.transrept(new StringAspect(xml), IAspectPersistentBean.class)
        return aspect.getPersistentBean()
    }

    static String toXml(Context context, IPersistentBean bean) {
        IAspectPersistentBean aspect = new PersistentBeanAspect(bean)
        String xml = ((IAspectString)context.transrept(aspect, IAspectString.class)).getString()
        return xml
    }


    // *********** IBeanReference **************

    static IPersistentBean dereference(IBeanReference beanReference) {
        IPersistentBean bean
        String id = beanReference.id
        String db = beanReference.getDb()
        String beanClassName = beanReference.beanClass
        if (id != null) {
            assert db != null && db.size() != 0, "id=$id and beanClass=$beanClassName"
            Context context = BeanRequestContext.getRequestContext()
            def beanMemory = BeanRequestContext.getBeanMemory()
            bean = beanMemory.retrieveBean(db, id, context)
            if (bean) {
                if (beanClassName) {   // type check
                    Class beanClass = Registry.getRegistry().classFor(beanClassName)
                    assert beanClass.isAssignableFrom(bean.class)
                }
            }
            else {
                // clean up dangling reference
                beanReference.id = null;
            }
        }
        return bean
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

    static void set(IBeanReference beanRef, String name, def value) {
        if (['id', 'db', 'beanClass', 'contextBean'].contains(name)) {
            beanRef.@"$name" = value
        }
        else {
            use(PersistentBeanCategory) {
                IPersistentBean bean = beanRef.dereference()
                value = bean."$name" = value
            }
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

    // ****************** Object *********************

    static IPersistentBean beanAt(Object obj, String id, String db) {
        Context context = BeanRequestContext.getRequestContext()
        BeanMemory beanMemory = BeanRequestContext.getBeanMemory()
        IPersistentBean bean = beanMemory.retrieveBean(db, id, context)
        return bean
    }

    static List trans(Object obj, String propName) {
        List set = []
        use(PersistentBeanCategory) {
            switch (obj) {
                case IBean:
                    List vals = [] + obj?."$propName"
                    vals.each {val ->
                        set.add(val)
                        List tc = val.trans(propName)
                        set.addAll(tc)
                    }
                    break
                case IBeanList:
                    obj.each {item ->
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


}