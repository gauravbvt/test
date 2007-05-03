/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.JavaBean;

/**
 * A javabean pattern that is matched against javabeans of a given class. 
 * The pattern matches if all its attribute constraints are satisfied.
 * @author jf
 *
 * @param <T>
 */
public class Pattern<T> implements Serializable {
	
	private Class<T> beanClass;
	private List<AttributeConstraint> constraints;
	
	public Pattern(Class<T> beanClass) {
		this.beanClass = beanClass;
		constraints = new ArrayList<AttributeConstraint>();
	}

	/**
	 * A constraint on an attribute of a javabean being matched.
	 * The attribute is specified by an attribute path (<attribute>.<attribute>...).
	 * The value constraint is either a regex to be matched against a data attribute value as string,
	 * or a set of types that must all be implied by the types of the element referenced in an element attribute.
	 * @author jf
	 *
	 */
	class AttributeConstraint implements Serializable {
		
		private String path; // to an attribute
		private String regex; // to match against value attributes
		private TypeSet types; // to match against reference attributes and TypeSet attributes

	}
	
	public Class<T> getbeanClass() {
		return beanClass;
	}
	
	public boolean matches(JavaBean bean) {
		return false;
	}

}
