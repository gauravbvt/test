package com.mindalliance.channels.engine.analysis.detectors;

import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Requirement not satisfied by organization.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/10/11
 * Time: 6:19 PM
 */
public class RequirementNotSatisfiedByOrganization extends AbstractIssueDetector {

    public RequirementNotSatisfiedByOrganization() {
    }

    @Override
    public List<? extends Issue> detectIssues( QueryService queryService, ModelObject modelObject ) {
        List<Issue> issues = new ArrayList<Issue>();
        Organization org = (Organization) modelObject;
        if ( org.isActual() && !org.isUnknown() ) {
            for ( Requirement req : queryService.list( Requirement.class ) ) {
                // as committer
                verifyRequirementSatisfaction( org, req, false, issues, queryService );
                // as beneficiary
                verifyRequirementSatisfaction( org, req, true, issues, queryService );
            }
        }
        return issues;
    }

    private void verifyRequirementSatisfaction(
            Organization org,
            Requirement req,
            boolean asBeneficiary,
            List<Issue> issues,
            QueryService queryService ) {
        Place planLocale = queryService.getPlanLocale();
        if ( req.appliesTo( org, asBeneficiary, planLocale ) ) {
            Requirement.Satisfaction satisfaction = req.satisfaction( org, asBeneficiary, queryService, getAnalyst() );
            if ( satisfaction == Requirement.Satisfaction.Impossible ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, org );
                issue.setDescription( req.dissatisfactionSummary( org, asBeneficiary, queryService, getAnalyst() )
                        + " \""
                        + req.getName()
                        + "\"." );
                issue.setRemediation( "Add flows to the plan to make satisfaction possible" +
                        "\nor change the definition of the requirement." );
                issue.setSeverity( Level.High );
                issues.add( issue );
            } else if ( satisfaction == Requirement.Satisfaction.Negative ) {
                Issue issue = makeIssue( queryService, Issue.COMPLETENESS, org );
                issue.setDescription( "Requirement \""
                        + req.getName()
                        + "\" is not satisfied: "
                        + req.dissatisfactionSummary( org, asBeneficiary, queryService, getAnalyst() )
                        + "." );
                issue.setRemediation( "Modify the plan to achieve the minimum number of " +
                        "required commitments for agents in the organization" +
                        "\nor change the definition of the requirement." );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            } else if ( satisfaction == Requirement.Satisfaction.Weak ) {
                Issue issue = makeIssue(  queryService, Issue.ROBUSTNESS, org );
                issue.setDescription( "Requirement \""
                        + req.getName()
                        + "\" is only weakly satisfied by "
                        + org.getName()
                        + "." );
                issue.setRemediation( "Modify the plan to achieve the preferred number of " +
                        "required commitments for agents in the organization" +
                        "\nor change the definition of the requirement."  );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
    }

    @Override
    public boolean appliesTo( ModelObject modelObject ) {
        return modelObject instanceof Organization;
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Organization does not satisfy requirement";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
