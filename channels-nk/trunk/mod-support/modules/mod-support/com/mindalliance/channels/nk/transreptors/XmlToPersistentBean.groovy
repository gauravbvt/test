package com.mindalliance.channels.nk.transreptors

import com.mindalliance.channels.nk.bean.IPersistentBean
import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.ten60.netkernel.urii.IURRepresentation
import org.ten60.netkernel.xml.representation.IAspectXDA
import com.mindalliance.channels.nk.XDAHelper
import groovy.util.slurpersupport.GPathResult
import com.mindalliance.channels.nk.aspects.IAspectPersistentBean
import com.mindalliance.channels.nk.aspects.PersistentBeanAspect
import org.ten60.netkernel.layer1.nkf.INKFResponse
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import com.mindalliance.channels.nk.PersistentBeanHelper

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 8:51:36 PM
* To change this template use File | Settings | File Templates.
*/
class XmlToPersistentBean  extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
         return aFrom.hasAspect(IAspectXDA.class) && aTo.isAssignableFrom(IAspectPersistentBean.class)
     }
     
    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectXDA xda = (IAspectXDA)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectXDA.class)
        String doc = new XDAHelper(context).asXML(xda)
        IPersistentBean bean = new PersistentBeanHelper(context).persistentBeanFromXml(doc)
        IAspectPersistentBean persistenBeanAspect = new PersistentBeanAspect(bean)
        INKFResponse response = context.createResponseFrom(persistenBeanAspect)
        response.setMineType("text/xml")
    }

}