package com.mindalliance.channels.c10n.transreptors

import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import com.ten60.netkernel.urii.IURRepresentation
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.c10n.util.IContinuation
import com.ten60.netkernel.urii.aspect.StringAspect
import org.ten60.netkernel.layer1.nkf.INKFResponse
import com.ten60.netkernel.urii.aspect.IAspectString
import com.ten60.netkernel.urii.aspect.IAspectBinaryStream
import com.ten60.netkernel.urii.aspect.IAspectReadableBinaryStream
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 11, 2008
* Time: 8:25:24 PM
* To change this template use File | Settings | File Templates.
*/
class ContinuationToXMLBinaryStream  extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
        return aFrom.hasAspect(IAspectContinuation.class) && (aTo.isAssignableFrom(IAspectString.class) || aTo.isAssignableFrom(IAspectBinaryStream.class) || aTo.isAssignableFrom(IAspectReadableBinaryStream.class));
    }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectContinuation aspect = (IAspectContinuation)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectContinuation.class)
        IContinuation continuation = aspect.getContinuation()
        String xml = continuation.toXml(context)
        StringAspect sa = new StringAspect(xml);
        INKFResponse resp = context.createResponseFrom(sa);
        resp.setMimeType("text/xml");
        context.setResponse(resp);
    }

}