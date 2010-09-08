package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * An actor belongs to more than one organization.
 * Can be waived.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 12, 2009
 * Time: 3:20:21 PM
 */
public class ActorNotInOneOrganization extends AbstractIssueDetector {

    public ActorNotInOneOrganization() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canBeWaived() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String getLabel() {
        return "Agent in no organization or in too many";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public List<Issue> detectIssues( ModelObject modelObject ) {
        Actor actor = (Actor) modelObject;
        List<Issue> issues = new ArrayList<Issue>();
        List<Organization> employers = getQueryService().findEmployers( actor );
        if ( employers.size() != 1 ) {
            if ( employers.size() == 0 ) {
                DetectedIssue issue = makeIssue( Issue.COMPLETENESS, actor );
                issue.setSeverity( Level.Low );
                issue.setDescription( actor + " does not belong to any organization." );
                issue.setRemediation( "Specify an organization in a task played by the agent\n "
                        + "or register the agent as an employee of an organization." );
                issues.add( issue );
            } else {
                for ( Organization org : employers ) {
                    final List<Organization> parents = org.ancestors();
                    List<Organization> ancestors = (List<Organization>) CollectionUtils.select(
                            employers,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    Organization employer = (Organization) obj;
                                    return parents.contains( employer );
                                }
                            } );
                    for ( Organization ancestor : ancestors ) {
                        DetectedIssue issue = makeIssue( Issue.VALIDITY, actor );
                        issue.setSeverity( Level.Low );
                        issue.setDescription( actor
                                + " belongs to " + org.getName()
                                + " and to " + ancestor.getName()
                                + ", and " + ancestor.getName()
                                + " is a parent organization of " + org.getName() );
                        issue.setRemediation( "Make sure " + actor + " belongs to only one organization." );
                        issues.add( issue );
                    }
                }
            }
        }
        return issues;
    }

    /**
     * {@inheritDoc}
     */
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Actor && ( (ModelEntity) modelObject ).isActual();
    }

    /**
     * {@inheritDoc}
     */
    public String getTestedProperty() {
        return null;
    }
}
