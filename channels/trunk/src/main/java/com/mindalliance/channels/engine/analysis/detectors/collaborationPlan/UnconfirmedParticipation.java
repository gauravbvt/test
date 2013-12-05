package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.communities.UserParticipation;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Accepted but unconfirmed user participation requests.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/3/13
 * Time: 10:26 AM
 */
public class UnconfirmedParticipation extends AbstractIssueDetector {

    public UnconfirmedParticipation() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof Agent;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends Issue> detectIssues( final CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        Agent agent = (Agent) identifiable;
        final ParticipationManager participationManager = communityService.getParticipationManager();
        for ( UserParticipation userParticipation : participationManager.getParticipationsAsAgent( agent, communityService ) ) {
            if ( userParticipation.isAccepted() && userParticipation.isSupervised( communityService ) ) {
                if ( !communityService.getUserParticipationConfirmationService()
                        .isConfirmedByAllSupervisors( userParticipation, communityService ) ) {
                    Issue issue = makeIssue( communityService, Issue.COMPLETENESS, agent );
                    issue.setDescription(
                            "The participation of "
                                    + userParticipation.getParticipantFullName( communityService )
                                    + " as \""
                                    + userParticipation.getAgent( communityService ).getName()
                                    + "\" is not confirmed by all supervisors"
                    );
                    issue.setSeverity( Level.High ); // todo - set to max failure impact of assigned tasks
                    // compose remediation
                    List<Agent> emptySupervisorAgents = (List<Agent>) CollectionUtils.select(
                            participationManager.findAllSupervisorsOf( agent, communityService ),
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    return participationManager.findAllUsersParticipatingAs( ( (Agent) object ), communityService ).isEmpty();
                                }
                            }
                    );
                    List<String> emptySupervisorAgentNames = (List<String>) CollectionUtils.collect(
                            emptySupervisorAgents,
                            new Transformer() {
                                @Override
                                public Object transform( Object input ) {
                                    return ( (Agent) input ).getName();
                                }
                            }
                    );
                    List<String> supervisingUserNames = (List<String>) CollectionUtils.collect(
                            participationManager.findAllUsersRequestedToConfirm( userParticipation, communityService ),
                            new Transformer() {
                                @Override
                                public Object transform( Object input ) {
                                    return ( (ChannelsUser) input ).getFullName();
                                }
                            }
                    );
                    StringBuilder sb = new StringBuilder();
                    if ( !supervisingUserNames.isEmpty() ) {
                        sb.append( "Remind " )
                                .append( ChannelsUtils.listToString( supervisingUserNames, " and " ) )
                                .append( " to confirm the participation of " )
                                .append( userParticipation.getParticipantFullName( communityService ) )
                                .append( " as " )
                                .append( agent.getName() );
                    }
                    if ( !emptySupervisorAgentNames.isEmpty() ) {
                        if ( !supervisingUserNames.isEmpty() )
                            sb.append( " and request " );
                        else
                            sb.append( "Request user " );
                        sb.append( "participation as supervisor " );
                        sb.append( emptySupervisorAgentNames.size() > 1 ? "agents " : "agent " );
                        sb.append( ChannelsUtils.listToString( emptySupervisorAgentNames, " and " ) );
                    }
                    if ( !supervisingUserNames.isEmpty() || !emptySupervisorAgentNames.isEmpty() )
                        sb.append( "\nor " );
                    sb.append( "cancel the unconfirmed participation by " )
                            .append( userParticipation.getParticipantFullName( communityService ) )
                            .append( " as " )
                            .append( agent.getName() )
                            .append( "." );
                    issue.setRemediation( sb.toString() );
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
        return "User participation awaits confirmation";
    }
}
