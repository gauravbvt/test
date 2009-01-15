package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.analysis.Issue;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.ResourceSpec;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 13, 2009
 * Time: 7:45:54 PM
 */
public class SortableResourceIssuesProvider extends SortableDataProvider<Issue> {
    /**
     * List of issues found
     */
    private List<Issue> issues;

    public SortableResourceIssuesProvider( ResourceSpec resourceSpec ) {
        issues = Project.getProject().findAllIssuesFor( resourceSpec );
        setSort( "about.name", true );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Issue> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<Issue> sortedIssues = new ArrayList<Issue>();
        sortedIssues.addAll( issues );
        Collections.sort( sortedIssues, new Comparator<Issue>() {
            /**
             * @param issue the first object to be compared.
             * @param otherIssue the second object to be compared.
             * @return a negative integer, zero, or a positive integer as the
             *         first argument is less than, equal to, or greater than the
             *         second.
             * @throws ClassCastException if the arguments' types prevent them from
             *                            being compared by this comparator.
             */
            public int compare( Issue issue, Issue otherIssue ) {
                int comp = 0;
                try {
                    String sortProperty = sortParam.getProperty();
                    Object value = PropertyUtils.getProperty( issue, sortProperty );
                    Object otherValue = PropertyUtils.getProperty( otherIssue, sortProperty );
                    String stringValue = ( value == null ) ? "" : value.toString();
                    String otherStringValue = ( otherValue == null ) ? "" : otherValue.toString();
                    comp = stringValue.compareTo( otherStringValue );
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
        return sortedIssues.subList( first, first + count ).iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return issues.size();
    }

    /**
     * {@inheritDoc}
     */
    public IModel<Issue> model( Issue issue ) {
        return new Model<Issue>( issue );
    }

}
