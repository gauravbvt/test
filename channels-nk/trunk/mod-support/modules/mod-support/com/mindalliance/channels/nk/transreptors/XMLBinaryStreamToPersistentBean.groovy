package com.mindalliance.channels.nk.transreptors

import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.ten60.netkernel.urii.IURRepresentation
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import org.ten60.netkernel.layer1.nkf.INKFResponse
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import com.mindalliance.channels.nk.PersistentBeanHelper
import com.ten60.netkernel.urii.aspect.IAspectString
import com.ten60.netkernel.urii.aspect.IAspectBinaryStream
import com.ten60.netkernel.urii.aspect.IAspectReadableBinaryStream

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 4:22:50 PM
 * To change this template use File | Settings | File Templates.
 */
class XMLBinaryStreamToPersistentBean  extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
         return (aFrom.hasAspect(IAspectString.class) || aFrom.hasAspect(IAspectBinaryStream.class)  || aFrom.hasAspect(IAspectReadableBinaryStream.class)) && aTo.isAssignableFrom(IAspectPersistentBean.class)
     }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectString ias = (IAspectString)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectString.class)
        String doc = ias.getString()
        IPersistentBean bean = new PersistentBeanHelper(context).persistentBeanFromXml(doc)
        IAspectPersistentBean persistenBeanAspect = new PersistentBeanAspect(bean)
        INKFResponse response = context.createResponseFrom(persistenBeanAspect)
        response.setMimeType("text/xml")
    }

}