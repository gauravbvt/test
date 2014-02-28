package com.mindalliance.channels.engine.analysis.detectors.collaborationTemplate;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Command crossing organizational line and not from supervisor.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/30/13
 * Time: 1:52 PM
 */
public class CommandWithoutAuthority extends AbstractIssueDetector {

    public CommandWithoutAuthority() {
    }

    @Override
    public boolean appliesTo( Identifiable modelObject ) {
        return modelObject instanceof Flow && ( (Flow) modelObject ).isSharing();
    }

    @Override
    public List<? extends Issue> detectIssues( CommunityService communityService, Identifiable modelObject ) {
        QueryService queryService = communityService.getModelService();
        List<Issue> issues = new ArrayList<Issue>();
        Flow flow = (Flow) modelObject;
        if ( flow.getIntent() != null && flow.getIntent() == Flow.Intent.Command ) {
            boolean fromSupervisor = fromSupervisor( flow );
            if ( !fromSupervisor ) {
                if ( isAcrossOrganizationalLine( flow ) ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                    issue.setDescription( "The command \""
                            + flow.getName()
                            + "\" sent not by a supervisor"
                            + " and it crosses organizational lines." );
                    issue.setSeverity( queryService.computePartPriority( ( (Part) flow.getTarget() ) ) );
                    issue.setRemediation( "Make the intent of the flow something other than a command" +
                            "\n or restrict the flow to be within the same organization" +
                            "\n or restrict the flow to be \"to supervised\"." );
                    issues.add( issue );
                } else if ( couldCrossOrganizationalLine( flow ) ) {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                    issue.setDescription( "The command \""
                            + flow.getName()
                            + "\" sent not by a supervisor"
                            + " and it could cross organizational lines." );
                    issue.setSeverity( queryService.computePartPriority( ( (Part) flow.getTarget() ) ) );
                    issue.setRemediation( "Make the intent of the flow something other than a command" +
                            "\n or restrict the flow to be within the same organization" +
                            "\n or restrict the flow to be \"to supervised\"." );
                    issues.add( issue );
                } else {
                    Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, flow );
                    issue.setDescription( "The command \""
                            + flow.getName()
                            + "\" sent not by a supervisor." );
                    issue.setSeverity( queryService.computePartPriority( ( (Part) flow.getTarget() ) ) );
                    issue.setRemediation( "Make the intent of the flow something other than a command" +
                            "\n or restrict the flow to be from a supervisor (i.e. to supervised)." );
                    issues.add( issue );
                }
            }
        }
        return issues;
    }

    private boolean isAcrossOrganizationalLine( Flow sharing ) {
        List<Flow.Restriction> restrictions = sharing.getRestrictions();
            if ( restrictions.contains( Flow.Restriction.DifferentOrganizations ) ) {
                return true;
            } else if ( restrictions.contains( Flow.Restriction.SameOrganization ) ) {
                return false;
            }
        Organization sourceOrg = ( (Part) sharing.getSource() ).getOrganization();
        Organization targetOrg = ( (Part) sharing.getTarget() ).getOrganization();
        return ( sourceOrg != null && targetOrg != null && !sourceOrg.equals( targetOrg ) );
    }

    private boolean couldCrossOrganizationalLine( Flow sharing ) {
        List<Flow.Restriction> restrictions = sharing.getRestrictions();
        if ( restrictions.contains( Flow.Restriction.SameOrganization  ) ) {
            return false;
        }
        Organization sourceOrg = ( (Part) sharing.getSource() ).getOrganization();
        Organization targetOrg = ( (Part) sharing.getTarget() ).getOrganization();
        return sourceOrg == null
                || targetOrg == null
                || sourceOrg.isType()
                || sourceOrg.isPlaceHolder()
                || targetOrg.isType()
                || targetOrg.isPlaceHolder()
                || !ModelObject.areIdentical( sourceOrg, targetOrg );

    }

    private boolean fromSupervisor( Flow sharing ) {
        return sharing.getRestrictions().contains( Flow.Restriction.Supervised );
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "Command flow not from supervisor";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
