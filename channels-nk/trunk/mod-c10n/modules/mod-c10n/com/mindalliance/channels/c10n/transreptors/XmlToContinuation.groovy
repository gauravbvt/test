package com.mindalliance.channels.c10n.transreptors

import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import com.ten60.netkernel.urii.IURRepresentation
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import org.ten60.netkernel.xml.representation.IAspectXDA
import com.mindalliance.channels.nk.XDAHelper
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.c10n.aspects.ContinuationAspect
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import org.ten60.netkernel.layer1.nkf.INKFResponse

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 11, 2008
* Time: 8:28:30 PM
*/
class XmlToContinuation  extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
         return aFrom.hasAspect(IAspectXDA.class) && aTo.isAssignableFrom(IAspectContinuation.class)
     }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectXDA xda = (IAspectXDA)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectXDA.class)
        String doc = new XDAHelper(context).asXML(xda)
        IContinuation continuation = Continuation.fromXml(doc, context)
        IAspectContinuation continuationAspect = new ContinuationAspect(continuation)
        INKFResponse response = context.createResponseFrom(continuationAspect)
        response.setMineType("text/xml")
    }
}