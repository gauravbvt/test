package com.mindalliance.channels.util;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 21, 2009
 * Time: 12:53:49 PM
 */
public class PropertyComparator<T> implements Comparator<T> {

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
    public int compare( T object, T other ) {
        int comp;
        String sortProperty = sortParam.getProperty();
        String value = evaluatePropertyToString( object, sortProperty );
        String otherValue = evaluatePropertyToString( other, sortProperty );
        comp = value.compareTo( otherValue );
        return sortParam.isAscending() ? comp * -1 : comp;
    }

    private String evaluatePropertyToString( T object, String propPath ) {
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
        return value == null ? "" : value.toString();
    }

}
