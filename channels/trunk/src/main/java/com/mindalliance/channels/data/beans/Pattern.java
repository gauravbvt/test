/*
 * Created on Apr 26, 2007
 *
 */
package com.mindalliance.channels.data.beans;

import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.TypeSet;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * A javabean pattern that is matched against javabeans of a given class. 
 * The pattern matches if all its attribute constraints are satisfied.
 * TODO: Rethink using generics.
 * @author jf
 *
 * @param <T>
 */
public class Pattern<T extends JavaBean> extends AbstractJavaBean {
	
	private Class klass; // the class to match. Must inherit from T.
	private List<AttributeConstraint> constraints;

	/**
	 * A constraint on an attribute of a javabean being matched.
	 * The attribute is specified by an attribute path (<attribute>.<attribute>...).
	 * The value constraint is either a regex to be matched against a data attribute value as string,
	 * or a set of types that must all be implied by the types of the element referenced in an element attribute.
	 * @author jf
	 *
	 */
	class AttributeConstraint extends AbstractJavaBean {
		
		private String path; // to an attribute
		private String regex; // to match against value attributes
		private TypeSet types; // to match against reference attributes and TypeSet attributes

	}
	
	
	public boolean matches(T bean) {
		return false;
	}

}
