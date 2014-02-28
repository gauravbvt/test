package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Collaboration plan is based on an obsolete version of the template.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/13
 * Time: 1:40 PM
 */
public class ObsoleteTemplateVersion extends AbstractIssueDetector {

    public ObsoleteTemplateVersion() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof PlanCommunity;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        PlanCommunity planCommunity = (PlanCommunity)identifiable;
        List<Issue> issues = new ArrayList<Issue>(  );
        int currentVersion = planCommunity.getModelVersion();
        final int latestProdVersion = getModelManager().findProductionModel( planCommunity.getModelUri() ).getVersion();
        int diff = latestProdVersion - currentVersion;
        if ( diff > 0 ) {
            Issue issue = makeIssue( communityService, Issue.VALIDITY, planCommunity );
            issue.setDescription( "The community is based on version " + currentVersion + " of the collaboration model. "
                    + "The latest version is " + latestProdVersion );
            issue.setRemediation( "Upgrade to the latest version of the collaboration model." );
            issue.setSeverity( diff > 1 ? Level.High : Level.Medium );
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
        return "Community based on obsolete version of the collaboration model";
    }
}
