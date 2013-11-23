package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Commitment restriction put on a flow is meaningless.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 5, 2010
 * Time: 4:36:34 PM
 */
public class MeaninglessFlowRestriction extends AbstractIssueDetector {

    public MeaninglessFlowRestriction() {
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        List<Flow.Restriction> restrictions = flow.getRestrictions();
        if ( !restrictions.isEmpty() && flow.isSharing() ) {
            Part source = (Part) flow.getSource();
            Part target = (Part) flow.getTarget();
            Organization sourceOrg = source.getOrganization();
            Organization targetOrg = target.getOrganization();
            Place sourceLoc = source.getKnownLocation();
            Place targetLoc = target.getKnownLocation();
            Actor sourceActor = source.getActor();
            Actor targetActor = target.getActor();
            if ( ( restrictions.contains( Flow.Restriction.SameOrganization ) ||
                    restrictions.contains( Flow.Restriction.SameTopOrganization ) ||
                    restrictions.contains(  Flow.Restriction.DifferentOrganizations ) ||
                    restrictions.contains(  Flow.Restriction.DifferentTopOrganizations ) )
                    && sourceOrg != null
                    && targetOrg != null
                    && sourceOrg.isActual()
                    && targetOrg.isActual() ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "The restriction adds nothing because " +
                        "the organizations named in specifying both source " +
                        "and target tasks are actual organizations." );
                issue.setRemediation( "Remove the restriction" +
                        "\nor change the source and/or target specification to use types of organizations or none at all." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            } else if ( ( restrictions.contains(  Flow.Restriction.SameLocation ) ||
                    restrictions.contains( Flow.Restriction.DifferentLocations )  )
                    && sourceLoc != null
                    && targetLoc != null
                    && sourceLoc.isActual()
                    && targetLoc.isActual() ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "The restriction adds nothing because " +
                        "the locations named in specifying both source " +
                        "and target tasks are actual places." );
                issue.setRemediation( "Remove the restriction" +
                        "\nor change the source and/or target specification to use types of places or none at all." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }  else if ( ( restrictions .contains(  Flow.Restriction.Supervisor ) || restrictions.contains(  Flow.Restriction.Supervised) )
                    && sourceActor != null
                    && targetActor != null
                    && sourceActor.isActual()
                    && targetActor.isActual() ) {
                Issue issue = makeIssue( communityService, Issue.VALIDITY, flow );
                issue.setDescription( "The restriction adds nothing because " +
                        "the agents named in specifying both source " +
                        "and target tasks are actual agents." );
                issue.setRemediation( "Remove the restriction" +
                        "\nor change the source and/or target specification to use types of agents or none at all." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow;
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    protected String getKindLabel() {
        return "Meaningless flow restriction";
    }
}
