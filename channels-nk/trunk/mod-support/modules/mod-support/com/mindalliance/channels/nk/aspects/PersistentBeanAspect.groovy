package com.mindalliance.channels.nk.aspects

import com.mindalliance.channels.nk.bean.IPersistentBean
import com.ten60.netkernel.urii.IURRepresentation
import com.ten60.netkernel.urii.IURMeta
import org.ten60.netkernel.layer1.representation.MonoRepresentationImpl

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:50:47 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanAspect implements IAspectPersistentBean {

    private IPersistentBean persistentBean

    public PersistentBeanAspect(IPersistentBean bean) {
        persistentBean = (IPersistentBean)bean.deepCopy()
    }

    public IPersistentBean getPersistentBean() {
        return persistentBean
    }


    /** Create an IURRepresentation holding the immutable NVPImpl
     * @param aMeta the meta for the representation
     * @param aString the value of the string aspect
     */
    public static IURRepresentation create(IURMeta aMeta, IPersistentBean bean){
        return new MonoRepresentationImpl(aMeta, new PersistentBeanAspect(bean));
    }

}