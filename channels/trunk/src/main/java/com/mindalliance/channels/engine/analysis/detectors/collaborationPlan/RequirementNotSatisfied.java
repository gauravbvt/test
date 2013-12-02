package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationAnalyst;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Requirement not satisfied between two participation organizations to which it applies.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/2/13
 * Time: 1:26 PM
 */
public class RequirementNotSatisfied extends AbstractIssueDetector {

    public RequirementNotSatisfied() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Requirement;
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable identifiable ) {
        Requirement requirement = (Requirement) identifiable;
        requirement.initialize( communityService );
        ParticipationAnalyst participationAnalyst = communityService.getParticipationAnalyst();
        List<Issue> issues = new ArrayList<Issue>();
        Place locale = communityService.getPlanCommunity().getLocale( communityService );
        for ( RequirementRelationship reqRel :
                participationAnalyst.findRequirementRelationships( requirement, communityService ) ) {
            for ( Requirement req : reqRel.getRequirements() ) {
                Requirement appliedReq = req.transientCopy();
                appliedReq.setSituationIfAppropriate( null, null, locale );
                appliedReq.initialize( communityService );
                Requirement.Satisfaction satisfaction = appliedReq.measureSatisfaction( null, null, communityService );
                if ( satisfaction == Requirement.Satisfaction.Impossible ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, requirement );
                    issue.setDescription( "The requirement \""
                            + requirement.getLabel()
                            + "\" could not possibly be satisfied based on the template by "
                            + appliedReq.getCommitterSpec().getAgency().getName()
                            + " toward "
                            +  appliedReq.getBeneficiarySpec().getAgency().getName()
                    );
                    issue.setSeverity( Level.High );
                    issue.setRemediation( "Change the definition of the requirement so that it does not apply\n"
                            + "or remove the requirement.");
                    issues.add( issue );
                } else if ( satisfaction == Requirement.Satisfaction.Negative ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, requirement );
                    issue.setDescription( "The requirement \""
                            + requirement.getLabel()
                            + "\" is not satisfied by "
                            + appliedReq.getCommitterSpec().getAgency().getName()
                            + " toward "
                            +  appliedReq.getBeneficiarySpec().getAgency().getName()
                    );
                    issue.setSeverity( Level.High );
                    issue.setRemediation( "Change the definition of the requirement so that it does not apply\n"
                            + "or change the participation in either or both organizations\n"
                            + "or remove the requirement.");
                    issues.add( issue );
                }
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
        return "Requirement not satisfied";
    }

}
