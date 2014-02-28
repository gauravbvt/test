package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.WorkTime;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        List<Assignment> assignments = queryService.findAllAssignments( part, false );
        List<WorkTime> availabilities = (List<WorkTime>) CollectionUtils.collect(
                assignments,
                new Transformer() {
                    public Object transform( Object input ) {
                        return ( (Assignment) input ).getActor().getAvailability();
                    }
                }
        );
        if ( !availabilities.isEmpty() ) {
            boolean noFullTime = !CollectionUtils.exists(
                    availabilities,
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ((WorkTime)object).isAlways();
                        }
                    }
            );
            if ( noFullTime ) {
                Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, part );
                issue.setDescription( "No one is available to do task \"" + part.getTask() + "\" full time." );
                issue.setSeverity( computeTaskFailureSeverity( queryService, part ) );
                issue.setRemediation( "Change the availabilities of assigned agents so that at least one agent is full time" +
                        "\nor change the specification of the task so that more agents are assigned to fill the gaps" );
                issues.add( issue );
            }
        }
        return issues;
    }

  /*  private List<TimeGap> findGaps( List<WorkTime> availabilities ) {
        List<TimeGap> gaps = new ArrayList<TimeGap>();
        for ( WorkTime availability : availabilities ) {
            // Someone is 24/7, no gaps
            if ( availability.isAlways() ) return gaps;
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
*/
    /** {@inheritDoc} */
    public boolean appliesTo( Identifiable modelObject ) {
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


}
