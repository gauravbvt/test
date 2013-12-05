package com.mindalliance.channels.engine.analysis.detectors.collaborationPlan;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.community.protocols.CommunityAssignment;
import com.mindalliance.channels.core.community.protocols.CommunityAssignments;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.EventPhase;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.engine.analysis.AbstractIssueDetector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A user participates too much.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/4/13
 * Time: 3:35 PM
 */
public class OverParticipationByUser extends AbstractIssueDetector {

    public OverParticipationByUser() {
    }

    @Override
    public boolean appliesTo( Identifiable identifiable ) {
        return identifiable instanceof ChannelsUser;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<? extends Issue> detectIssues( final CommunityService communityService, Identifiable identifiable ) {
        List<Issue> issues = new ArrayList<Issue>();
        ChannelsUser user = (ChannelsUser) identifiable;
        final ParticipationManager participationManager = communityService.getParticipationManager();
        List<Agent> agents = participationManager.listAgentsUserParticipatesAs( user, communityService );
        if ( agents.size() > 1 ) {
            checkForTooMuchSupervision( user, agents, issues, communityService );
            checkForOverlappingAssignments( user, agents, issues, communityService );
        }
        return issues;
    }

    private void checkForTooMuchSupervision( ChannelsUser user,
                                             List<Agent> agents, List<Issue> issues,
                                             CommunityService communityService ) {
        final ParticipationManager participationManager = communityService.getParticipationManager();
        Map<Agent, Integer> supervisedCount = new HashMap<Agent, Integer>();
        // multiple (primary) supervisory responsibilities?
        for ( Agent agent : agents ) {
            if ( !agent.isLinked() ) {
                int count = 0;
                for ( Agent supervisedAgent : participationManager.findAllSupervisedBy( agent, communityService ) ) {
                    count += participationManager
                            .findAllUsersParticipatingAs( supervisedAgent, communityService )
                            .size();
                }
                if ( count > 0 )
                    supervisedCount.put( agent, count );
            }
        }
        if ( supervisedCount.keySet().size() > 1 ) {
            Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, user );
            StringBuilder sb = new StringBuilder();
            sb.append( user.getFullName() );
            boolean first = true;
            for ( Agent supervisor : supervisedCount.keySet() ) {
                if ( !first ) {
                    sb.append( ", and" );
                }
                int count = supervisedCount.get( supervisor );
                sb.append( " supervises " )
                        .append( count )
                        .append( count > 1 ? " individuals" : " individual" )
                        .append( " as " )
                        .append( supervisor.getName() );
                first = false;
            }
            issue.setDescription( sb.toString() );
            issue.setSeverity( Level.Medium );
            sb = new StringBuilder();
            sb.append( "Terminate the participation of " )
                    .append( user.getFullName() )
                    .append( " as " );
            first = true;
            for ( Agent supervisor : supervisedCount.keySet() ) {
                if ( !first )
                    sb.append( "\nor as " );
                sb.append( supervisor.getName() );
                first = false;
            }
            sb.append( "." );
            issue.setRemediation( sb.toString() );
            issues.add( issue );
        }
    }

    // Assignments from different (primary) agents in same event phase?
    @SuppressWarnings( "unchecked" )
    private void checkForOverlappingAssignments( ChannelsUser user,
                                                 List<Agent> agents,
                                                 List<Issue> issues,
                                                 CommunityService communityService ) {
        Map<EventPhase, Set<Agent>> agentInvolvementsByEventPhase = new HashMap<EventPhase, Set<Agent>>();
        CommunityAssignments communityAssignments = communityService.getAllAssignments();
        for ( Agent agent : agents ) {
            for ( CommunityAssignment communityAssignment : communityAssignments.with( agent ) ) {
                if ( communityAssignment.getAssignment().getEmployment().isPrimary() ) {
                    EventPhase eventPhase = communityAssignment.getEventPhase();
                    addInvolvement( agent, eventPhase, agentInvolvementsByEventPhase );
                }
            }
        }
        // find all event phases where user is involved  as > 1 agent
        for ( EventPhase eventPhase : agentInvolvementsByEventPhase.keySet() ) {
            Set<Agent> agentsInvolved = agentInvolvementsByEventPhase.get( eventPhase );
            if ( agentsInvolved.size() > 1 ) {
                List<String> agentNames = (List<String>) CollectionUtils.collect(
                        agentsInvolved,
                        new Transformer() {
                            @Override
                            public Object transform( Object input ) {
                                return ( (Agent) input ).getName();
                            }
                        }
                );
                Issue issue = makeIssue( communityService, Issue.ROBUSTNESS, user );
                StringBuilder sb = new StringBuilder();
                sb.append( user.getFullName() )
                        .append( " is involved in planning scenario \"" )
                        .append( eventPhase.toString() )
                        .append( "\" in more than one position, namely " )
                        .append( ChannelsUtils.listToString( agentNames, " and " ) );
                issue.setDescription( sb.toString() );
                sb = new StringBuilder();
                sb.append( "Terminate " )
                        .append( user.getFullName() )
                        .append( "'s participation as " );
                boolean first = true;
                for ( Agent agent : agentsInvolved ) {
                    if ( !first )
                        sb.append( "\nor as " );
                    sb.append( agent.getName() );
                    first = false;
                }
                sb.append( "." );
                issue.setRemediation( sb.toString() );
                issue.setSeverity( Level.Medium );
                issues.add( issue );
            }
        }
    }

    private void addInvolvement( Agent agent,
                                 EventPhase eventPhase,
                                 Map<EventPhase, Set<Agent>> agentInvolvementsByEventPhase ) {
        Set<Agent> involvedAgents = agentInvolvementsByEventPhase.get( eventPhase );
        if ( involvedAgents == null ) {
            involvedAgents = new HashSet<Agent>();
            agentInvolvementsByEventPhase.put( eventPhase, involvedAgents );
        }
        involvedAgents.add( agent );
    }

    @Override
    public String getTestedProperty() {
        return null;
    }

    @Override
    protected String getKindLabel() {
        return "User participates too much";
    }

    @Override
    public boolean canBeWaived() {
        return true;
    }
}
