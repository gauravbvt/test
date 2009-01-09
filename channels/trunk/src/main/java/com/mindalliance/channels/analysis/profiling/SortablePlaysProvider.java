package com.mindalliance.channels.analysis.profiling;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:12:32 PM
 */
public class SortablePlaysProvider extends SortableDataProvider<Play> {

    private List<Play> plays;

    public SortablePlaysProvider( Role role ) {
        plays = findAllPlaysOfRole( role );
        setSort( "part.scenario.name", true );
    }

    public SortablePlaysProvider( Actor actor ) {
        // TODO
    }

    public SortablePlaysProvider( Organization organization ) {
        // TODO
    }

    public Iterator<Play> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<Play> sortedPlays = new ArrayList<Play>();
        Collections.copy( sortedPlays, plays );
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
                String sortProperty = sortParam.getProperty();
                try {
                    String value = PropertyUtils.getProperty( play, sortProperty ).toString();
                    String otherValue = PropertyUtils.getProperty( otherPlay, sortProperty ).toString();
                    comp = value.compareTo( otherValue );
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
                return sortParam.isAscending() ? comp : comp * -1;
            }
        } );
        return sortedPlays.subList( first, first + count ).iterator();
    }

    public int size() {
        return plays.size();
    }

    public IModel<Play> model( Play play ) {
        return new Model<Play>( play );
    }

    private List<Play> findAllPlaysOfRole( Role role ) {
        ArrayList<Play> list = new ArrayList<Play>();
        Iterator<Scenario> scenarios = Project.getProject().getDao().scenarios();
        while ( scenarios.hasNext() ) {
            Scenario scenario = scenarios.next();
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( flow.getSource().isPart() && ( (Part) flow.getSource() ).getRole() == role ) {
                    // role sends
                    Play play = new Play( ( (Part) flow.getSource() ), flow, true );
                    list.add( play );
                }
                if ( flow.getTarget().isPart() && ( (Part) flow.getTarget() ).getRole() == role ) {
                    // role receives
                    Play play = new Play( ( (Part) flow.getTarget() ), flow, false );
                    list.add( play );
                }
            }
        }
        return list;
    }


}
