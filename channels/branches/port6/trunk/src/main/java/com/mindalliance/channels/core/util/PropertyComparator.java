package com.mindalliance.channels.core.util;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 12:53:49 PM
 */
public class PropertyComparator<T> implements Comparator<T>, Serializable {

    private SortParam sortParam;

    public PropertyComparator( SortParam sortParam ) {
        this.sortParam = sortParam;
    }

    /**
     * @param object the first object to be compared.
     * @param other  the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     * @throws ClassCastException if the arguments' types prevent them from
     *                            being compared by this comparator.
     */
    @Override
    public int compare( T object, T other ) {
        int comp;
        String sortProperty = sortParam.getProperty();
        Comparable value = evaluatePropertyToComparable( object, sortProperty );
        Comparable otherValue = evaluatePropertyToComparable( other, sortProperty );
        if ( value == null && otherValue == null )
            comp = 0;
        else if ( value == null )
            comp = -1;
        else if ( otherValue == null )
            comp = 1;
        else
            comp = value.compareTo( otherValue );
        return sortParam.isAscending() ? comp * -1 : comp;
    }

    private Comparable evaluatePropertyToComparable( T object, String propPath ) {
        Object value;
        try {
            value = PropertyUtils.getProperty( object, propPath );
        } catch ( NestedNullException e ) {
            value = null;
        } catch ( IllegalAccessException e ) {
            System.out.println( e );
            value = null;
        } catch ( InvocationTargetException e ) {
            System.out.println( e );
            value = null;
        } catch ( NoSuchMethodException e ) {
            System.out.println( e );
            value = null;
        }
        if ( value != null ) {
            if ( value instanceof Comparable ) {
                return (Comparable)value;
            } else {
                return value.toString();
            }
        } else {
            return null;
        }
    }

}
