package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Place;

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
    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        Flow.Restriction restriction = flow.getRestriction();
        if ( restriction != null && flow.isSharing() ) {
            Part source = (Part) flow.getSource();
            Part target = (Part) flow.getTarget();
            Organization sourceOrg = source.getOrganization();
            Organization targetOrg = target.getOrganization();
            Place sourceLoc = source.getLocation();
            Place targetLoc = target.getLocation();
            if ( ( restriction == Flow.Restriction.SameOrganization ||
                    restriction == Flow.Restriction.SameTopOrganization ||
                    restriction == Flow.Restriction.DifferentOrganizations )
                    && sourceOrg != null
                    && targetOrg != null
                    && sourceOrg.isActual()
                    && targetOrg.isActual() ) {
                Issue issue = makeIssue( Issue.VALIDITY, flow );
                issue.setDescription( "The restriction adds nothing because " +
                        "the organizations named in specifying both source " +
                        "and target tasks are actual organizations." );
                issue.setRemediation( "Remove the restriction" +
                        "\nor change the source and/or target specification to use types of organizations or none at all." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            } else if ( ( restriction == Flow.Restriction.SameLocation ||
                    restriction == Flow.Restriction.DifferentLocations )
                    && sourceLoc != null
                    && targetLoc != null
                    && sourceLoc.isActual()
                    && targetLoc.isActual() ) {
                Issue issue = makeIssue( Issue.VALIDITY, flow );
                issue.setDescription( "The restriction adds nothing because " +
                        "the locations named in specifying both source " +
                        "and target tasks are actual places." );
                issue.setRemediation( "Remove the restriction" +
                        "\nor change the source and/or target specification to use types of places or none at all." );
                issue.setSeverity( Level.Low );
                issues.add( issue );
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
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
    protected String getLabel() {
        return "Meaningless flow restriction";
    }
}
