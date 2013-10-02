package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/1/13
 * Time: 7:48 PM
 */
public class OrganizationWithMultiParticipationHasLinkedJobs extends AbstractIssueDetector {

    public OrganizationWithMultiParticipationHasLinkedJobs() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization
                && ( (Organization) modelObject ).isActual();
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = ( (Organization) modelObject );
        if ( org.isPlaceHolder() && !org.isSingleParticipation() ) {
            boolean hasLinkedJobs = CollectionUtils.exists(
                    org.getJobs(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return ( (Job) object ).isLinked();
                        }
                    }
            );
            if ( hasLinkedJobs ) {
                Issue issue = makeIssue( queryService, Issue.VALIDITY, org );
                issue.setDescription( "Placeholder organization " + org.getName() + " has linked jobs even though " +
                        "it allows more than one participation. This may lead to unintended participation by users " +
                        "as the agents with the linked jobs." );
                issue.setRemediation( "Make the linked jobs non-linked (primary/hiring) jobs\n" +
                        "or make the organization a known organization (not a placeholder)\n" +
                        "or remove all linked jobs from the organization." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Linked jobs in placeholder organization allowing multiple participation";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
