// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.data.reference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A javabean pattern that is matched against javabeans of a given
 * class. The pattern matches if all its attribute constraints are
 * satisfied.
 *
 * @author <a href="mailto:jf@mind-alliance.com">jf</a>
 * @version $Revision:$
 * @param <T> the type of matches
 */
public class Pattern<T> implements Serializable {

    private Class<T> beanClass;
    private List<AttributeConstraint> constraints;

    /**
     * Default constructor.
     * @param beanClass the class of the objects of the pattern.
     */
    public Pattern( Class<T> beanClass ) {
        this.beanClass = beanClass;
        constraints = new ArrayList<AttributeConstraint>();
    }

    /**
     * Match against a target value.
     * @param bean the value.
     * @return if there was a match or not
     */
    public boolean matches( T bean ) {
        // TODO
        return false;
    }

    /**
     * Return the class of the objects of the pattern.
     */
    public Class<T> getBeanClass() {
        return beanClass;
    }

    /**
     * Set the class of the objects of the pattern.
     * @param beanClass the beanClass to set
     */
    public void setBeanClass( Class<T> beanClass ) {
        this.beanClass = beanClass;
    }

    /**
     * Return the constraints of this pattern.
     */
    public List<AttributeConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Set the constraints of this pattern.
     * @param constraints the constraints to set
     */
    public void setConstraints( List<AttributeConstraint> constraints ) {
        this.constraints = constraints;
    }

    /**
     * Add a constraint.
     * @param constraint the constraint
     */
    public void addConstraint( AttributeConstraint constraint ) {
        constraints.add( constraint );
    }

    /**
     * Remove a constraint.
     * @param constraint the constraint
     */
    public void removeConstraint( AttributeConstraint constraint ) {
        constraints.remove( constraint );
    }

    /**
     * A constraint on an attribute of a javabean being matched. The
     * attribute is specified by an attribute path
     * (&lt;attribute&gt;.&lt;attribute&gt;...).
     * The value constraint is either a regex to be matched against a
     * data attribute value as string, or a set of types that must all
     * be implied by the types of the element referenced in an element
     * attribute.
     */
    class AttributeConstraint implements Serializable {

        private String path;
        private String regex;
        private TypeSet types;

        /**
         * Default constructor.
         */
        public AttributeConstraint() {
        }

        /**
         * Return the path to an attribute.
         */
        public String getPath() {
            return path;
        }

        /**
         * Set the path to an attribute.
         * @param path the path to set
         */
        public void setPath( String path ) {
            this.path = path;
        }

        /**
         * Return the regex to match against value attributes.
         */
        public String getRegex() {
            return regex;
        }

        /**
         * Set the regex to match against value attributes.
         * @param regex the regex to set
         */
        public void setRegex( String regex ) {
            this.regex = regex;
        }

        /**
         * Return the types to match against reference
         * attributes and TypeSet attributes.
         */
        public TypeSet getTypes() {
            return types;
        }

        /**
         * Set the type set.
         * @param types the types to set
         */
        public void setTypes( TypeSet types ) {
            this.types = types;
        }
    }
}
