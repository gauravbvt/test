/*
 * DBXMLResultToXML.java
 *
 * Created on 02 August 2006, 14:50
 */

package org.ten60.dbxml.transreptor;

import org.ten60.netkernel.layer1.nkf.*;
import org.ten60.netkernel.layer1.nkf.impl.*;

import org.ten60.dbxml.representation.DBXMLResultAspect;
import com.sleepycat.dbxml.*;
import org.ten60.netkernel.layer1.representation.StringAspect;
import com.ten60.netkernel.urii.*;

/**
 *
 * @author  pjr
 */
public class DBXMLResultToXML extends NKFTransreptorImpl
{
	public boolean supports(com.ten60.netkernel.urii.IURRepresentation aFrom, Class aTo)
	{	return aFrom.hasAspect(DBXMLResultAspect.class);
	}
	
	protected void transrepresent(INKFConvenienceHelper context) throws Exception
	{	IURRepresentation rep=context.source(INKFRequestReadOnly.URI_SYSTEM);
		DBXMLResultAspect ra=(DBXMLResultAspect)rep.getAspect(DBXMLResultAspect.class);
		if(ra.hasXmlDocument())
		{	XmlResults r=ra.getXmlResultsReadOnly();
			XmlValue v=r.peek();
			IURAspect asp=context.transrept(new StringAspect(v.asString()), context.getThisRequest().getAspectClass() );
			INKFResponse resp=context.createResponseFrom(asp);
			resp.setMimeType(rep.getMeta().getMimeType());
		}
		else throw new Exception("Cannot Transrept DBXMLResultAspect to XML: result contains multiple values. Use com.sleepycat.dbxml.XmlResults API to iterate over values.");
	}
	
}
