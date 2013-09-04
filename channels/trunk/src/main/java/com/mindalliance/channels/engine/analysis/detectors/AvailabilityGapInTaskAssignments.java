package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Availability;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.TimePeriod;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * There is a time period where a task noone assigned is available.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 22, 2010
 * Time: 9:22:21 PM
 */
public class AvailabilityGapInTaskAssignments extends AbstractIssueDetector {

    public AvailabilityGapInTaskAssignments() {
    }

    /** {@inheritDoc} */
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        List<Assignment> assignments = queryService.findAllAssignments( part, false );
        List<Availability> availabilities = (List<Availability>) CollectionUtils.collect(
                assignments,
                new Transformer() {
                    public Object transform( Object input ) {
                        return ( (Assignment) input ).getActor().getAvailability();
                    }
                }
        );
        if ( !availabilities.isEmpty() ) {
            List<TimeGap> gaps = findGaps( availabilities );
            for ( TimeGap gap : gaps ) {
                Issue issue = makeIssue( queryService, Issue.ROBUSTNESS, part );
                issue.setDescription( "No one is available to do task \"" + part.getTask() + "\" on " + gap + "." );
                issue.setSeverity( this.computeTaskFailureSeverity( queryService, part ) );
                issue.setRemediation( "Change the availabilities of assigned agents so that they leave no gap" +
                        "\nor change the specification of the task so that more agents are assigned to fill the gaps" );
                issues.add( issue );
            }
        }
        return issues;
    }

    private List<TimeGap> findGaps( List<Availability> availabilities ) {
        List<TimeGap> gaps = new ArrayList<TimeGap>();
        for ( Availability availability : availabilities ) {
            // Someone is 24/7, no gaps
            if ( availability == null ) return gaps;
        }
        for ( int day = 0; day < 7; day++ ) {
            gaps.addAll( findDayGaps( day, availabilities ) );
        }
        return gaps;
    }

    private List<TimeGap> findDayGaps( int day, List<Availability> availabilities ) {
        List<TimePeriod> periodGaps = new ArrayList<TimePeriod>();
        periodGaps.add( TimePeriod.allDayPeriod() );
        for ( Availability availability : availabilities ) {
            TimePeriod period = availability.getTimePeriod( day );
            periodGaps = subtract( period, periodGaps );
        }
        List<TimeGap> timeGaps = new ArrayList<TimeGap>();
        for ( TimePeriod periodGap : periodGaps ) {
            timeGaps.add( new TimeGap( day, periodGap ) );
        }
        return timeGaps;
    }

    private List<TimePeriod> subtract( TimePeriod subtractor, List<TimePeriod> periods ) {
        List<TimePeriod> results = new ArrayList<TimePeriod>();
        for ( TimePeriod period : periods ) {
            List<TimePeriod> result = period.subtract( subtractor );
            results.addAll( result );
        }
        return results;
    }

    /** {@inheritDoc} */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    /** {@inheritDoc} */
    public String getTestedProperty() {
        return null;
    }

    protected String getKindLabel() {
        return "Availability gap in task assignments";
    }
   /** {@inheritDoc} */
    public boolean canBeWaived() {
        return true;
    }

    private class TimeGap {

        private int day;
        private TimePeriod timePeriod;

        TimeGap( int day, TimePeriod timePeriod ) {
            this.day = day;
            this.timePeriod = timePeriod;
        }

        public int getDay() {
            return day;
        }

        public TimePeriod getTimePeriod() {
            return timePeriod;
        }

        public String toString() {
            return Availability.dayOfWeek( day ) + " " + timePeriod.toString();
        }
    }
}
