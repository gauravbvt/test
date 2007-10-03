/******************************************************************************
  (c) Copyright 2002 - $Date: 2007/03/27 14:44:13 $ 1060 Research Ltd

  This Software is licensed to You, the licensee, for use under the terms of
  the 1060 Public License v1.0. Please read and agree to the 1060 Public
  License v1.0 [www.1060research.com/license] before using or redistributing
  this software.

  In summary the 1060 Public license has the following conditions.
  A. You may use the Software free of charge provided you agree to the terms
  laid out in the 1060 Public License v1.0
  B. You are only permitted to use the Software with components or applications
  that provide you with OSI Certified Open Source Code [www.opensource.org], or
  for which licensing has been approved by 1060 Research Limited.
  You may write your own software for execution by this Software provided any
  distribution of your software with this Software complies with terms set out
  in section 2 of the 1060 Public License v1.0
  C. You may redistribute the Software provided you comply with the terms of
  the 1060 Public License v1.0 and that no warranty is implied or given.
  D. If you find you are unable to comply with this license you may seek to
  obtain an alternative license from 1060 Research Limited by contacting
  license@1060research.com or by visiting www.1060research.com

  NO WARRANTY:  THIS SOFTWARE IS NOT COVERED BY ANY WARRANTY. SEE 1060 PUBLIC
  LICENSE V1.0 FOR DETAILS

  THIS COPYRIGHT NOTICE IS *NOT* THE 1060 PUBLIC LICENSE v1.0. PLEASE READ
  THE DISTRIBUTED 1060_Public_License.txt OR www.1060research.com/license

  File:          $RCSfile: DBXMLAccessorImpl.java,v $
  Version:       $Name:  $ $Revision: 1.7 $
  Last Modified: $Date: 2007/03/27 14:44:13 $
 *****************************************************************************/

package org.ten60.dbxml.accessor;


import org.ten60.dbxml.representation.DBXMLResultAspect;
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import org.ten60.netkernel.layer1.nkf.INKFRequestReadOnly;
import org.ten60.netkernel.layer1.nkf.INKFResponse;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import org.ten60.netkernel.layer1.representation.ByteArrayAspect;
import org.ten60.netkernel.xml.representation.IXAspect;
import org.ten60.netkernel.xml.xda.IXDAReadOnly;

import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlIndexDeclaration;
import com.sleepycat.dbxml.XmlIndexSpecification;
import com.sleepycat.dbxml.XmlInputStream;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlUpdateContext;
import com.ten60.netkernel.urii.IURAspect;
import com.ten60.netkernel.urii.aspect.IAspectReadableBinaryStream;
import com.ten60.netkernel.urii.aspect.StringAspect;

/**
 *	The DBXML Accessor class. Implements dbxmlxxxxx family of accessors
 *  @author  pjr
 */
public class DBXMLAccessorImpl extends NKFAccessorImpl
{	//General config not yet implemented
	//public static final String ARG_CONFIGURATION = "configuration";
	//public static final String DEFAULT_CONFIG="ffcpl:/etc/ConfigDBXML.xml";
	private XmlManager mManager;

	/** Creates a new instance of DBXMLAccessorImpl */
	public DBXMLAccessorImpl()
	{	super(0,SAFE_FOR_CONCURRENT_USE,INKFRequestReadOnly.RQT_SOURCE);
	}

	public void processRequest(INKFConvenienceHelper context) throws Exception
	{	/* General configuration not yet implemented
		String configURI;
		if (context.getThisRequest().argumentExists(ARG_CONFIGURATION))
		{	configURI= "this:param:"+ARG_CONFIGURATION;
		}
		else
		{	configURI=DEFAULT_CONFIG;
		}
		*/
		if(mManager==null)
		{	mManager=new XmlManager();
		}
		IURAspect resultantAspect=execute(mManager,context);
		INKFResponse response = context.createResponseFrom(resultantAspect);
		response.setCreationCost(32);
		context.setResponse(response);
	}

	public void destroy()
	{	try
		{	mManager.delete();
		}
		catch(Exception e)
		{	//Nothing we can do now...
			e.printStackTrace();
		}
	}

	private IURAspect execute(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	String action = context.getThisRequest().getActiveType();
		IURAspect resultantAspect=new org.ten60.netkernel.layer1.representation.VoidAspect();
		if (action.equals("dbxmlCreateContainer"))
		{	dbxmlCreateContainer(aManager, context);
		}
		else if (action.equals("dbxmlDeleteContainer"))
		{	dbxmlDeleteContainer(aManager, context);
		}
		else if (action.equals("dbxmlRenameContainer"))
		{	dbxmlRenameContainer(aManager, context);
		}
		else if (action.equals("dbxmlExistsContainer"))
		{	resultantAspect=dbxmlExistsContainer(aManager, context);
		}
		else if (action.equals("dbxmlPutDocument"))
		{	dbxmlPutDocument(aManager, context);
		}
		else if (action.equals("dbxmlGetDocument"))
		{	resultantAspect=dbxmlGetDocument(aManager, context);
		}
		else if (action.equals("dbxmlDeleteDocument"))
		{	dbxmlDeleteDocument(aManager, context);
		}
		else if (action.equals("dbxmlQuery"))
		{	resultantAspect=dbxmlQuery(aManager, context);
		}
		else if (action.equals("dbxmlBooleanQuery"))
		{	resultantAspect=dbxmlBooleanQuery(aManager, context);
		}
		else if (action.equals("dbxmlAddIndex"))
		{   dbxmlAddIndex(aManager, context);
		}
		else if (action.equals("dbxmlGetIndices"))
		{   resultantAspect=dbxmlGetIndices(aManager, context);
		}
		else if (action.equals("dbxmlDeleteIndex"))
		{   dbxmlDeleteIndex(aManager, context);
		}
		return resultantAspect;
	}

	private void dbxmlCreateContainer(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String name=opt.getText("/dbxml/name", true);
		XmlContainerConfig cfg=new XmlContainerConfig();
		cfg.setAllowValidation(opt.isTrue("/dbxml/allowValidation"));
		cfg.setIndexNodes(opt.isTrue("/dbxml/indexNodes"));
		cfg.setNodeContainer(opt.isTrue("/dbxml/nodeContainer"));
		XmlContainer c=null;
		try
		{	c=aManager.createContainer(name, cfg);
		}
		finally
		{	if(c!=null)
			{	c.delete();
				//c.close();
			}
		}
	}

	private void dbxmlDeleteContainer(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String name=opt.getText("/dbxml/name", true);
		aManager.removeContainer(name);
	}

	private void dbxmlRenameContainer(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String oldname=opt.getText("/dbxml/oldname", true);
		String newname=opt.getText("/dbxml/newname", true);
		aManager.renameContainer(oldname, newname);
	}

	private IURAspect dbxmlExistsContainer(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String name=opt.getText("/dbxml/name", true);
		return new org.ten60.netkernel.layer1.representation.BooleanAspect(aManager.existsContainer(name)>0);
	}

	private void dbxmlPutDocument(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String name=opt.getText("/dbxml/name", true);
		String container=opt.getText("/dbxml/container", true);
		IAspectReadableBinaryStream rbsa=(IAspectReadableBinaryStream)context.sourceAspect("this:param:operand", IAspectReadableBinaryStream.class);
		XmlContainer c=null;
		XmlUpdateContext uc=null;
		XmlInputStream is=null;
		try
		{	c=aManager.openContainer(container);
			uc=aManager.createUpdateContext();
			is=aManager.createInputStream(rbsa.getInputStream());
			c.putDocument(name, is, uc, null);
		}
		finally
		{	if(uc!=null)
			{	uc.delete();
			}
			if(is!=null)
			{	is.delete();
			}
			if(c!=null)
			{	c.delete();
				//c.close();
			}

		}
	}

	private IURAspect dbxmlGetDocument(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String name=opt.getText("/dbxml/name", true);
		String container=opt.getText("/dbxml/container", true);
		XmlContainer c=null;
		XmlDocument d=null;
		IURAspect result=null;
		try
		{	c=aManager.openContainer(container);
			d=c.getDocument(name);
			result=new ByteArrayAspect(d.getContent());
		}
		finally
		{	if(d!=null)
			{	d.delete();
			}
			if(c!=null)
			{	c.delete();
				//c.close();
			}
		}
		return result;
	}

	private void dbxmlDeleteDocument(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String name=opt.getText("/dbxml/name", true);
		String container=opt.getText("/dbxml/container", true);
		XmlContainer c=null;
		XmlUpdateContext uc=null;
		try
		{	c=aManager.openContainer(container);
			uc=aManager.createUpdateContext();
			c.deleteDocument(name, uc);
		}
		finally
		{	if(uc!=null)
			{	uc.delete();
			}
			if(c!=null)
			{	c.delete();
				//c.close();
			}
		}
	}

	private IURAspect dbxmlQuery(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
		String container=opt.getText("/dbxml/container", true);
		String xquery=opt.getText("/dbxml/xquery", true);
		XmlContainer c=null;
		IURAspect result=null;
		try
		{	c=aManager.openContainer(container);
			XmlQueryContext qc = aManager.createQueryContext();
			qc.setEvaluationType(XmlQueryContext.Eager);
			XmlQueryExpression qe=aManager.prepare(xquery, qc);
			XmlResults r=qe.execute(qc);
			qc.delete();
			result=new DBXMLResultAspect(r);
		}
		finally
		{	if(c!=null)
			{	c.delete();
				//c.close();
			}
		}
		return result;
	}

	private IURAspect dbxmlBooleanQuery(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	DBXMLResultAspect r=(DBXMLResultAspect)dbxmlQuery(aManager,context);
		return new org.ten60.netkernel.layer1.representation.BooleanAspect(!r.isEmpty());
	}

	private void dbxmlAddIndex(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{	IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
    	String container=opt.getText("/dbxml/name", true);
    	String indexNamespace= opt.isTrue("/dbxml/index/namespace")
    	    ? opt.getText("/dbxml/index/namespace", true) : "";
    	String indexNodeName=opt.getText("/dbxml/index/nodeName", true);
    	String indexType=opt.getText("/dbxml/index/type", true);

    	XmlContainer c=null;
    	try
    	{   c=aManager.openContainer(container);
        	XmlIndexSpecification is = c.getIndexSpecification();
        	is.addIndex(indexNamespace, indexNodeName, indexType);
        	XmlUpdateContext uc = aManager.createUpdateContext();
        	c.setIndexSpecification(is, uc);
    	} finally
    	{   if(c!=null)
        	{   c.delete();
        	}
    	}
	}

	private IURAspect dbxmlGetIndices(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{   IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
        String container=opt.getText("/dbxml/name", true);
        XmlContainer c=null;
        IURAspect result=null;

        try
        {   c=aManager.openContainer(container);
            XmlIndexSpecification is = c.getIndexSpecification();
            StringBuffer sb = new StringBuffer("<indices>");
            XmlIndexDeclaration idxDecl=null;

            while((idxDecl = (is.next())) != null)
            {  	sb.append("<index>");
            	sb.append("<nodeName>");
            	sb.append(idxDecl.name);
            	sb.append("</nodeName>");
            	sb.append("<type>");
            	sb.append(idxDecl.index);
            	sb.append("</type></index>");
            }
            sb.append("</indices>");

            result=new StringAspect(sb.toString());
        }
        finally
        {   if(c!=null)
            {   c.delete();
            }
        }
        return result;
	}

	private void dbxmlDeleteIndex(XmlManager aManager, INKFConvenienceHelper context) throws Exception
	{
		IXDAReadOnly opt=((IXAspect)context.sourceAspect("this:param:operator", IXAspect.class)).getXDA();
    	String container=opt.getText("/dbxml/name", true);
    	String indexNamespace= opt.isTrue("/dbxml/index/namespace")
    	    ? opt.getText("/dbxml/index/namespace", true) : "";
    	String indexNodeName=opt.getText("/dbxml/index/nodeName", true);
    	String indexType=opt.getText("/dbxml/index/type", true);

    	XmlContainer c=null;

    	try
    	{   c=aManager.openContainer(container);
        	XmlIndexSpecification is = c.getIndexSpecification();
        	is.deleteIndex(indexNamespace, indexNodeName, indexType);
        	XmlUpdateContext uc = aManager.createUpdateContext();
        	c.setIndexSpecification(is, uc);
    	}
    	finally
    	{   if(c!=null)
        	{   c.delete();
        	}
    	}
	}
}