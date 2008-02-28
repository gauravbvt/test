package com.mindalliance.channels.nk.transreptors

import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.PersistentBeanHelper
import org.ten60.netkernel.layer1.representation.StringAspect
import org.ten60.netkernel.layer1.nkf.INKFResponse
import com.ten60.netkernel.urii.IURRepresentation
import com.ten60.netkernel.urii.aspect.IAspectBinaryStream
import com.ten60.netkernel.urii.aspect.IAspectString
import com.ten60.netkernel.urii.aspect.IAspectReadableBinaryStream
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 20, 2008
* Time: 7:42:48 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanToXMLBinaryStream extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
        return aFrom.hasAspect(IAspectPersistentBean.class) && (aTo.isAssignableFrom(IAspectString.class) || aTo.isAssignableFrom(IAspectBinaryStream.class) || aTo.isAssignableFrom(IAspectReadableBinaryStream.class));
    }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectPersistentBean aspect = (IAspectPersistentBean)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectPersistentBean.class)
        IPersistentBean bean = aspect.getPersistentBean()
        String xml = new PersistentBeanHelper(context).xmlFromBean(bean)
        StringAspect sa = new StringAspect(xml);
        INKFResponse resp = context.createResponseFrom(sa);
        resp.setMimeType("text/xml");
        context.setResponse(resp);
    }

}