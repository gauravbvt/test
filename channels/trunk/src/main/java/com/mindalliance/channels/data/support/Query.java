/*
 * Created on Apr 27, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import com.mindalliance.channels.data.elements.Element;

/**
 * A query in some language.
 * @author jf
 *
 */
public class Query implements Serializable {
	
	enum Language {JXPATH, OGNL };
	
	private Language language;
	private String expression;
	
	public Iterator findAll(Element context, Map bindings) {
		return null;
	}

	public Element findOne(Element context, Map bindings) {
		return null;
	}

}
