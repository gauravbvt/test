package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Actual organization has placeholder parent.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/4/13
 * Time: 3:01 PM
 */
public class NonPlaceholderOrganizationHasPlaceholderParent extends AbstractIssueDetector {

    public NonPlaceholderOrganizationHasPlaceholderParent() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        Organization org = (Organization)modelObject;
        List<Issue> issues = new ArrayList<Issue>(  );
        Organization parent = org.getParent();
        if ( org.isActual() && !org.isPlaceHolder() && parent != null && parent.isPlaceHolder() ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, org );
            issue.setDescription( "The parent \"" + parent.getName() + "\" is a placeholder." );
            issue.setRemediation( "Remove the placeholder parent organization" +
                    "\nor change the organization a placeholder organization" +
                    "\nor make \"" + parent.getName() + "\" a non-placeholder organization." );
            issue.setSeverity( Level.High );
            issues.add( issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Non-placeholder organization has a placeholder as a parent";
    }
}
