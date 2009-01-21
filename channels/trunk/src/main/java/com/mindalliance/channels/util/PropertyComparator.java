package com.mindalliance.channels.util;

import com.mindalliance.channels.Scenario;

import java.util.Comparator;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

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
     * @param object      the first object to be compared.
     * @param other the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     * @throws ClassCastException if the arguments' types prevent them from
     *                            being compared by this comparator.
     */
    public int compare( T object, T other ) {
        int comp = 0;
        try {
            String sortProperty = sortParam.getProperty();
            Object value = PropertyUtils.getProperty( object, sortProperty );
            Object otherValue = PropertyUtils.getProperty( other, sortProperty );
            String stringValue = ( value == null ) ? "" : value.toString();
            String otherStringValue = ( otherValue == null ) ? "" : otherValue.toString();
            comp = stringValue.compareTo( otherStringValue );
        } catch ( NestedNullException e ) {
            System.out.println( e );
        } catch ( IllegalAccessException e ) {
            System.out.println( e );
        } catch ( InvocationTargetException e ) {
            System.out.println( e );
        } catch ( NoSuchMethodException e ) {
            System.out.println( e );
        }
        return sortParam.isAscending() ? comp : comp * -1;
    }

}
