package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Assigned task location is not communicated.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/20/11
 * Time: 2:44 PM
 */
public class AssignedTaskLocationNotCommunicated extends AbstractIssueDetector {

    public AssignedTaskLocationNotCommunicated() {
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Part part = (Part) modelObject;
        AssignedLocation assignedLocation = part.getLocation();
        if ( assignedLocation.isCommunicated() ) {
            final String info = assignedLocation.getSubject().getInfo();
            final String eoi = assignedLocation.getSubject().getContent();
            boolean communicated = CollectionUtils.exists(
                    part.getAllSharingReceives(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow flow = (Flow) object;
                            return Matcher.same( info, flow.getName() )
                                    && ( eoi.isEmpty() || flow.hasEoiNamed( eoi ) );
                        }
                    }
            );
            if ( !communicated ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, part );
                issue.setDescription( "The task's location is not communicated as "
                        + assignedLocation.getSubject()
                        + " by any source.");
                // remediation
                StringBuilder sb = new StringBuilder( );
                sb.append( "Add a flow received by the task named \"" );
                sb.append( info );
                sb.append( "\"" );
                if ( !eoi.isEmpty() ) {
                    sb.append( " with element \"" );
                    sb.append( eoi );
                    sb.append(  "\"" );
                }
                sb.append( "\nor name the assigned location in the task specification" );
                sb.append( "\nor make the assigned location be the assigned agent's jurisdiction" );
                sb.append( "\nor make the assigned location be the jurisdiction of the assigned agent's organization." );
                issue.setRemediation( sb.toString() );
                issue.setSeverity( queryService.computePartPriority( part ) );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Part;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Location of assigned task is not communicated";
    }
}
