package com.mindalliance.channels.nk.transreptors

import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.ten60.netkernel.urii.IURRepresentation
import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.XDAHelper
import org.ten60.netkernel.xml.representation.IAspectXDA
import org.ten60.netkernel.layer1.nkf.INKFResponse
import com.mindalliance.channels.nk.PersistentBeanHelper
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:51:13 PM
* To change this template use File | Settings | File Templates.
*/
class PersistentBeanToXml extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
        return aFrom.hasAspect(IAspectPersistentBean.class) && (aTo.isAssignableFrom(IAspectXDA.class));
    }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectPersistentBean aspect = (IAspectPersistentBean)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectPersistentBean.class)
        IPersistentBean bean = aspect.getPersistentBean()
        String xml = new PersistentBeanHelper(context).xmlFromBean(bean)
        XDAHelper xdaHelper = new XDAHelper(context)
        IAspectXDA xda = xdaHelper.makeXDAAspect(xml)
        INKFResponse response = context.createResponseFrom(xda);
		response.setMimeType("text/xml");
    }


}