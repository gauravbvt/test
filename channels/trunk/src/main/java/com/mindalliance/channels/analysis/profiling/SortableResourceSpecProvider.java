package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.ResourceSpec;
import org.apache.commons.beanutils.PropertyUtils;
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
 * A sortable provider of resources for a given role or organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:16:32 PM
 */
public class SortableResourceSpecProvider extends SortableDataProvider<ResourceSpec> {
    /**
     * Known applicable resources from parts in the current project's scenarios
     */
    private List<ResourceSpec> resourceSpecs;

    public SortableResourceSpecProvider( ResourceSpec resourceSpec ) {
        resourceSpecs = Project.getProject().findAllResourcesNarrowingOrEqualTo( resourceSpec );
        setSort( "name", true );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<ResourceSpec> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<ResourceSpec> sortedResources = new ArrayList<ResourceSpec>();
        sortedResources.addAll( resourceSpecs );
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
                    String value = PropertyUtils.getProperty( resourceSpec, sortProperty ).toString();
                    String otherValue = PropertyUtils.getProperty( other, sortProperty )
                            .toString();
                    comp = value.compareTo( otherValue );
                } catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                } catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                } catch ( NoSuchMethodException e ) {
                    e.printStackTrace();
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
        return resourceSpecs.size();
    }

    /**
     * {@inheritDoc}
     */
    public IModel<ResourceSpec> model( ResourceSpec resourceSpec ) {
        return new Model<ResourceSpec>( resourceSpec );
    }

}
