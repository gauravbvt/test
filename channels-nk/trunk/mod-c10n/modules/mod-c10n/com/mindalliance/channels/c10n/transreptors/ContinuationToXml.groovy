package com.mindalliance.channels.c10n.transreptors

import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import com.ten60.netkernel.urii.IURRepresentation
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.nk.XDAHelper
import org.ten60.netkernel.xml.representation.IAspectXDA
import org.ten60.netkernel.layer1.nkf.INKFResponse
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 11, 2008
* Time: 8:19:42 PM
* To change this template use File | Settings | File Templates.
*/
class ContinuationToXml  extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
        return aFrom.hasAspect(IAspectContinuation.class) && (aTo.isAssignableFrom(IAspectXDA.class));
    }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectContinuation aspect = (IAspectContinuation)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectContinuation.class)
        IContinuation continuation = aspect.geContinuation()
        String xml = continuation.toXml(context)
        XDAHelper xdaHelper = new XDAHelper(context)
        IAspectXDA xda = xdaHelper.makeXDAAspect(xml)
        INKFResponse response = context.createResponseFrom(xda);
		response.setMimeType("text/xml");
    }

}