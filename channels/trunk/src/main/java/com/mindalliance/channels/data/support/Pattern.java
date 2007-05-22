/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.JavaBean;
import com.mindalliance.channels.data.reference.TypeSet;

/**
 * A javabean pattern that is matched against javabeans of a given
 * class. The pattern matches if all its attribute constraints are
 * satisfied.
 * 
 * @author jf
 * @param <T>
 */
public class Pattern<T> implements Serializable {

    private Class<T> beanClass;
    private List<AttributeConstraint> constraints;

    public Pattern( Class<T> beanClass ) {
        this.beanClass = beanClass;
        constraints = new ArrayList<AttributeConstraint>();
    }

    /**
     * A constraint on an attribute of a javabean being matched. The
     * attribute is specified by an attribute path (<attribute>.<attribute>...).
     * The value constraint is either a regex to be matched against a
     * data attribute value as string, or a set of types that must all
     * be implied by the types of the element referenced in an element
     * attribute.
     * 
     * @author jf
     */
    class AttributeConstraint implements Serializable {

        private String path; // to an attribute
        private String regex; // to match against value attributes
        private TypeSet types; // to match against reference
                                // attributes and TypeSet attributes

        /**
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * @param path the path to set
         */
        public void setPath( String path ) {
            this.path = path;
        }

        /**
         * @return the regex
         */
        public String getRegex() {
            return regex;
        }

        /**
         * @param regex the regex to set
         */
        public void setRegex( String regex ) {
            this.regex = regex;
        }

        /**
         * @return the types
         */
        public TypeSet getTypes() {
            return types;
        }

        /**
         * @param types the types to set
         */
        public void setTypes( TypeSet types ) {
            this.types = types;
        }

    }

    public Class<T> getbeanClass() {
        return beanClass;
    }

    public boolean matches( JavaBean bean ) {
        return false;
    }

    /**
     * @return the beanClass
     */
    public Class<T> getBeanClass() {
        return beanClass;
    }

    /**
     * @param beanClass the beanClass to set
     */
    public void setBeanClass( Class<T> beanClass ) {
        this.beanClass = beanClass;
    }

    /**
     * @return the constraints
     */
    public List<AttributeConstraint> getConstraints() {
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints( List<AttributeConstraint> constraints ) {
        this.constraints = constraints;
    }

    /**
     * @param constraint
     */
    public void addConstraint( AttributeConstraint constraint ) {
        constraints.add( constraint );
    }

    /**
     * @param constraint
     */
    public void removeConstraint( AttributeConstraint constraint ) {
        constraints.remove( constraint );
    }

}
