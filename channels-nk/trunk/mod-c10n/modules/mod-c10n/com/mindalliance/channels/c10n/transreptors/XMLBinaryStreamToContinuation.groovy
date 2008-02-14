package com.mindalliance.channels.c10n.transreptors

import org.ten60.netkernel.layer1.nkf.impl.NKFTransreptorImpl
import com.ten60.netkernel.urii.IURRepresentation
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper
import com.ten60.netkernel.urii.aspect.IAspectString
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.c10n.aspects.ContinuationAspect
import org.ten60.netkernel.layer1.nkf.INKFResponse
import com.ten60.netkernel.urii.aspect.IAspectBinaryStream
import com.ten60.netkernel.urii.aspect.IAspectReadableBinaryStream
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly
import com.mindalliance.channels.c10n.util.Continuation

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 11, 2008
* Time: 8:33:30 PM
* To change this template use File | Settings | File Templates.
*/
class XMLBinaryStreamToContinuation extends NKFTransreptorImpl {

    public boolean supports(IURRepresentation aFrom, Class aTo) {
         return (aFrom.hasAspect(IAspectString.class) || aFrom.hasAspect(IAspectBinaryStream.class)  || aFrom.hasAspect(IAspectReadableBinaryStream.class)) && aTo.isAssignableFrom(IAspectContinuation.class)
     }

    protected void transrepresent(INKFConvenienceHelper context) {
        IAspectString ias = (IAspectString)context.sourceAspect(INKFRequestReadOnly.URI_SYSTEM, IAspectString.class)
        String doc = ias.getString()
        IContinuation continuation = Continuation.fromXml(doc, context)
        IAspectContinuation continuationAspect = new ContinuationAspect(continuation)
        INKFResponse response = context.createResponseFrom(continuationAspect)
        response.setMimeType("text/xml")
    }

}