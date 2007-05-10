/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data.system;

import java.util.Iterator;
import java.util.Map;

import com.mindalliance.channels.data.Element;
import com.mindalliance.channels.data.support.Query;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Holds queryable, top-level javabeans (i.e. not contained in others).
 * @author jf
 *
 */
abstract public class AbstractQueryable extends AbstractJavaBean implements Queryable {
	
	public Iterator<Element> findAll(Query query, Map bindings) {
		return null;
	}

	public Element findOne(Query query, Map bindings) {
		return null;
	}
	

}
