package com.mindalliance.channels.analysis.detectors;

import com.mindalliance.channels.analysis.AbstractIssueDetector;
import com.mindalliance.channels.analysis.DetectedIssue;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Role;

import java.util.List;
import java.util.ArrayList;

/**
 * A person actor is given a role reserved for systems.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 17, 2009
 * Time: 10:49:47 AM
 */
public class PersonInSystemRole extends AbstractIssueDetector {

    public PersonInSystemRole() {
    }

    public List<Issue> detectIssues( ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Actor person = (Actor) modelObject;
        for ( Role role : getQueryService().findAllRolesOf( person ) ) {
            if ( role.isSystem() ) {
                DetectedIssue issue = makeIssue( Issue.VALIDITY, person );
                issue.setDescription( "Person assigned a role meant for systems." );
                issue.setRemediation( "Make the actor a system, or do not assign the role." );
                issue.setSeverity( Issue.Level.Minor );
                issues.add( issue );
            }
        }
        return issues;
    }

    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Actor && ( (Actor) modelObject ).isPerson();
    }

    public String getTestedProperty() {
        return null;
    }

    protected String getLabel() {
        return "Person in system role";
    }
}
