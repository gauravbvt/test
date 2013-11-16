package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Command relationship graph build.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 10:53 AM
 */
public class CommandChainsGraphBuilder implements GraphBuilder<Contact, CommandRelationship> {

    private ChannelsUser user;
    private Agent selectedAgent;
    private final CommunityService communityService;
    private ParticipationManager participationManager;

    public CommandChainsGraphBuilder( ChannelsUser user, CommunityService communityService ) {
        this.user = user;
        this.communityService = communityService;
        participationManager = communityService.getParticipationManager();
    }

    public CommandChainsGraphBuilder( Agent selectedAgent, CommunityService communityService ) {
        this.selectedAgent = selectedAgent;
        this.communityService = communityService;
        participationManager = communityService.getParticipationManager();
    }

    @Override
    public DirectedGraph<Contact, CommandRelationship> buildDirectedGraph() {
        DirectedGraph<Contact, CommandRelationship> digraph =
                new DirectedMultigraph<Contact, CommandRelationship>(
                        new EdgeFactory<Contact, CommandRelationship>() {
                            public CommandRelationship createEdge(
                                    Contact agent,
                                    Contact otherAgent ) {
                                return new CommandRelationship( agent, otherAgent );
                            }

                        } );
        List<Agent> agents;
        if ( user != null ) {
            agents = participationManager.listAgentsUserParticipatesAs( user, communityService );
        } else {
            agents = new ArrayList<Agent>();
            agents.add( selectedAgent );
        }
        for ( Agent agent : agents ) {
            Contact contact = user == null ? new Contact( agent) : new Contact( agent, user );
            digraph.addVertex( contact );
            populateGraphUp( digraph, contact );
            populateGraphDown( digraph, contact );
        }
        return digraph;
    }

    private void populateGraphUp( DirectedGraph<Contact, CommandRelationship> digraph, Contact contact ) {
        // add all supervisors of contact
        for ( Agent superior : participationManager.findAllSupervisorsOf( contact.getAgent(), communityService ) ) {
            for ( Contact superiorContact : findContacts( superior ) ) {
                boolean added = digraph.addVertex( superiorContact );
                digraph.addEdge( superiorContact, contact, new CommandRelationship( superiorContact, contact ) );
                if ( added )
                    populateGraphUp( digraph, superiorContact );
            }
        }
    }

    private void populateGraphDown( DirectedGraph<Contact, CommandRelationship> digraph, Contact contact ) {
        // add all supervised of contact
        for ( Agent supervised : participationManager.findAllSupervisedBy( contact.getAgent(), communityService ) ) {
            for ( Contact supervisedContact : findContacts( supervised ) ) {
                boolean added = digraph.addVertex( supervisedContact );
                digraph.addEdge( contact, supervisedContact, new CommandRelationship( contact, supervisedContact ) );
                if ( added )
                    populateGraphDown( digraph, supervisedContact );
            }
        }
    }

    private List<Contact> findContacts( Agent agent ) {
        List<Contact> contacts = new ArrayList<Contact>();
        if ( agent.getActor().isAnonymousParticipation() ) {
            contacts.add( new Contact( agent ) );
        } else {
            List<ChannelsUser> participants = participationManager.findAllUsersParticipatingAs( agent, communityService );
            if ( participants.isEmpty() ) {
                contacts.add( new Contact( agent ) );
            } else {
                for ( ChannelsUser participant : participants ) {
                    contacts.add( new Contact( agent, participant ) );
                }
            }
        }
        return contacts;
    }

}
