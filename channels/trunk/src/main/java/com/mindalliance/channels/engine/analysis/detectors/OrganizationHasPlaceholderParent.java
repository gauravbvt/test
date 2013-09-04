package com.mindalliance.channels.engine.analysis.detectors;

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
public class OrganizationHasPlaceholderParent extends AbstractIssueDetector {

    public OrganizationHasPlaceholderParent() {
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        Organization org = (Organization)modelObject;
        List<Issue> issues = new ArrayList<Issue>(  );
        Organization parent = org.getParent();
        if ( org.isActual() && parent != null && parent.isPlaceHolder() ) {
            Issue issue = makeIssue( queryService, Issue.VALIDITY, org );
            issue.setDescription( "The parent \"" + parent.getName() + "\" is a placeholder." );
            issue.setRemediation( "Remove the placeholder parent organization" +
                    "\nor change the parent to a non-placeholder organization" +
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
        return "Organization has a placeholder as a parent";
    }
}
