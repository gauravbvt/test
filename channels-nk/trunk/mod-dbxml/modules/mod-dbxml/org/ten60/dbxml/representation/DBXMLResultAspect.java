/*
 * DBXMLResultAspect.java
 *
 * Created on 02 August 2006, 13:13
 */

package org.ten60.dbxml.representation;

import com.ten60.netkernel.urii.IURAspect;
import com.sleepycat.dbxml.*;

/**
 * Aspect for Berkely DBXML Results
 * @author  pjr
 */

public class DBXMLResultAspect implements IURAspect
{
	private XmlResults mResults;

	/** Creates a new instance of DBXMLResultAspect */
	public DBXMLResultAspect(XmlResults aResults)
	{	mResults=aResults;
	}
	
	public XmlResults getXmlResultsReadOnly()
	{	return mResults;
	}
	
	public boolean isEmpty() throws Exception
	{	return mResults.size()==0;		
	}
	
	public boolean hasXmlDocument() throws Exception
	{	if (mResults.size()!=1) return false;
		XmlValue val=mResults.peek();
		try
		{	val.asDocument();
			return true;
		}
		catch(Exception e)
		{	return false;			
		}
		finally
		{	val.delete();			
		}
	}

	protected void finalize()
	{	mResults.delete();		
	}
}
