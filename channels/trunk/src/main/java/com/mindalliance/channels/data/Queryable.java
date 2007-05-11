/*
 * Created on Apr 28, 2007
 *
 */
package com.mindalliance.channels.data;

import java.util.Iterator;
import java.util.Map;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.support.Query;

/**
 * A queryable JavaBean
 * @author jf
 *
 */
public interface Queryable extends JavaBean {
	
	/**
	 * Execute the query in the context of itself with variable bindings and return all resulting elements.
	 * @param query A Query
	 * @param bindings Bindings for variables referenced in the query
	 * @return An iterator on resulting elements
	 */
	Iterator<Element> findAll(Query query, Map bindings);
	
	/**
	 * Execute the query in the context of itself with variable bindings and return first resulting element.
	 * @param query A Query
	 * @param bindings Bindings for variables referenced in the query
	 * @return An element
	 */
	Element findOne(Query query, Map bindings);

}
