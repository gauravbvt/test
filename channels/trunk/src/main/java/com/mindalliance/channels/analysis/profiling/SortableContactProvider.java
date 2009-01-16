package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.ResourceSpec;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A sortable provider of contacts for specified resources.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:16:32 PM
 */
public class SortableContactProvider extends SortableDataProvider<ResourceSpec> {

    /**
     * Contacts
     */
    private List<ResourceSpec> contacts;

    public SortableContactProvider( ResourceSpec resourceSpec, boolean isSelf ) {
        contacts = Project.getProject().getDao().findAllContacts( resourceSpec, isSelf );
        setSort( "name", true );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<ResourceSpec> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<ResourceSpec> sortedResources = new ArrayList<ResourceSpec>();
        sortedResources.addAll( contacts );
        Collections.sort( sortedResources, new Comparator<ResourceSpec>() {
            /**
             * @param resourceSpec the first object to be compared.
             * @param other the second object to be compared.
             * @return a negative integer, zero, or a positive integer as the
             *         first argument is less than, equal to, or greater than the
             *         second.
             * @throws ClassCastException if the arguments' types prevent them from
             *                            being compared by this comparator.
             */
            public int compare( ResourceSpec resourceSpec, ResourceSpec other ) {
                int comp = 0;
                try {
                    String sortProperty = sortParam.getProperty();
                    Object value = PropertyUtils.getProperty( resourceSpec, sortProperty );
                    Object otherValue = PropertyUtils.getProperty( other, sortProperty );
                    String valueString = (value == null ? "" : value.toString());
                    String otherValueString = (otherValue == null ? "" : otherValue.toString());
                    comp = valueString.compareTo( otherValueString );
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
        } );
        return sortedResources.subList( first, first + count ).iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return contacts.size();
    }

    /**
     * {@inheritDoc}
     */
    public IModel<ResourceSpec> model( ResourceSpec resourceSpec ) {
        return new Model<ResourceSpec>( resourceSpec );
    }

}
