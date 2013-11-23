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
 * Place holder organization with no identified custodian.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/19/12
 * Time: 3:19 PM
 */
public class PlaceholderOrganizationWithoutCustodian extends AbstractIssueDetector {

    public PlaceholderOrganizationWithoutCustodian() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization)modelObject;
        if ( org.isPlaceHolder() && org.getCustodian() == null ) {
            Issue issue = makeIssue( communityService, Issue.COMPLETENESS, org );
            issue.setDescription( "Organization \""
                    + org.getName()
                    + "\" is a placeholder for dynamically participating organizations but it has no identified custodian." +
                    " Planners will be the default custodians." );
            issue.setRemediation( "Make the organization NOT a placeholder organization" +
                    "\nor identify the custodian agent." );
            issue.setSeverity( Level.Low );
            issues.add(  issue );
        }
        return issues;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Placeholder organization has no custodian agent";
    }
}
