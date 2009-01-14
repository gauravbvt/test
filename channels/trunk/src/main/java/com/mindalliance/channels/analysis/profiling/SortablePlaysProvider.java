package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.pages.Project;
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
 * A sortable provider of plays.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:12:32 PM
 */
public class SortablePlaysProvider extends SortableDataProvider<Play> {

    /**
     * Plays found in scenarios for a given actor, role or organization.
     */
    private List<Play> plays;

    public SortablePlaysProvider( Resource resource ) {
        plays = Project.getProject().findAllPlays( resource );
        setSort( "part.scenario.name", true );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Play> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<Play> sortedPlays = new ArrayList<Play>();
        sortedPlays.addAll( plays );
        Collections.sort( sortedPlays, new Comparator<Play>() {
            /**
             * @param play the first object to be compared.
             * @param otherPlay the second object to be compared.
             * @return a negative integer, zero, or a positive integer as the
             *         first argument is less than, equal to, or greater than the
             *         second.
             * @throws ClassCastException if the arguments' types prevent them from
             *                            being compared by this comparator.
             */
            public int compare( Play play, Play otherPlay ) {
                int comp = 0;
                try {
                    String sortProperty = sortParam.getProperty();
                    Object value = PropertyUtils.getProperty( play, sortProperty );
                    Object otherValue = PropertyUtils.getProperty( otherPlay, sortProperty );
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
        return sortedPlays.subList( first, first + count ).iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return plays.size();
    }

    /**
     * {@inheritDoc}
     */
    public IModel<Play> model( Play play ) {
        return new Model<Play>( play );
    }

}
