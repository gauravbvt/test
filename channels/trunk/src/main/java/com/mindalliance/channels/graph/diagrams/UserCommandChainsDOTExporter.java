package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.ParticipationManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.engine.analysis.graph.CommandRelationship;
import com.mindalliance.channels.engine.analysis.graph.Contact;
import com.mindalliance.channels.graph.AbstractDOTExporter;
import com.mindalliance.channels.graph.DOTAttribute;
import com.mindalliance.channels.graph.MetaProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 12:58 PM
 */
public class UserCommandChainsDOTExporter extends AbstractDOTExporter<Contact, CommandRelationship> {

    public UserCommandChainsDOTExporter( MetaProvider<Contact, CommandRelationship> metaProvider ) {
        super( metaProvider );
    }

/*
    @Override
    protected void exportVertices( CommunityService communityService, PrintWriter out, Graph<Contact, CommandRelationship> g ) {
        DirectedGraph<Contact, CommandRelationship> digraph =
                (DirectedGraph<Contact, CommandRelationship>) g;
        List<Contact> roots = findRoots( digraph );
        printoutRankedVertices( communityService, out, roots );
        List<Contact> ranked = roots;
        List<Contact> placed = new ArrayList<Contact>( ranked );
        while ( !ranked.isEmpty() ) {
            ranked = findAllChildrenOf( ranked, digraph, placed, communityService );
            placed.addAll(  ranked );
            printoutRankedVertices( communityService, out, ranked );
        }
    }

    @SuppressWarnings( "unchecked" )
    private List<Contact> findRoots(
            final DirectedGraph<Contact, CommandRelationship> digraph ) {
        return (List<Contact>) CollectionUtils.select(
                digraph.vertexSet(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return digraph.inDegreeOf( (Contact) object ) == 0;
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    private List<Contact> findAllChildrenOf(
            final List<Contact> ranked,
            DirectedGraph<Contact, CommandRelationship> digraph,
            final List<Contact> placed,
            final CommunityService communityService ) {
        return (List<Contact>) CollectionUtils.select(
                digraph.vertexSet(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !placed.contains( (Contact)object )
                                && !CollectionUtils.intersection( ranked, getSuperiors( (Contact) object, communityService ) ).isEmpty();
                    }
                } );
    }

    private List<Contact> getSuperiors( Contact contact, CommunityService communityService ) {
        Set<Contact> superiorContacts = new HashSet<Contact>(  );
        List<Agent> superiors = communityService.getParticipationManager()
                .findAllSupervisorsOf( contact.getAgent(), communityService );
        for ( Agent superior : superiors ) {
            superiorContacts.addAll( findContacts( superior, communityService ) );
        }
        return new ArrayList<Contact>( superiorContacts );
    }

    private List<Contact> findContacts( Agent agent, CommunityService communityService ) {
        ParticipationManager participationManager = communityService.getParticipationManager();
        List<Contact> contacts = new ArrayList<Contact>();
        List<ChannelsUser> participants = participationManager.findAllUsersParticipatingAs( agent, communityService );
        if ( contacts.isEmpty() ) {
            contacts.add( new Contact( agent ) );
        } else {
            for ( ChannelsUser participant : participants ) {
                contacts.add( new Contact( agent, participant ) );
            }
        }
        return contacts;
    }


    private void printoutRankedVertices( CommunityService communityService, PrintWriter out, List<Contact> vertices ) {
        if ( !vertices.isEmpty() ) {
            MetaProvider<Contact, CommandRelationship> metaProvider = getMetaProvider();
            out.print( "{ rank=same; " );
            // Vertices
            for ( Contact v : vertices ) {
                List<DOTAttribute> attributes = DOTAttribute.emptyList();
                if ( metaProvider.getVertexLabelProvider() != null ) {
                    String label = metaProvider.getVertexLabelProvider().getVertexName( v );
                    attributes.add( new DOTAttribute( "label", label ) );
                }
                if ( metaProvider.getDOTAttributeProvider() != null ) {
                    attributes.addAll( metaProvider.getDOTAttributeProvider().getVertexAttributes( communityService,
                            v,
                            getHighlightedVertices().contains( v ) ) );
                }
                if ( metaProvider.getURLProvider() != null ) {
                    String url = metaProvider.getURLProvider().getVertexURL( v );
                    if ( url != null ) attributes.add( new DOTAttribute( "URL", url ) );
                }
                out.print( INDENT + getVertexID( v ) );
                out.print( "[" );
                if ( !attributes.isEmpty() ) {
                    out.print( asElementAttributes( attributes ) );
                }
                out.println( "];" );
            }
            out.print( "}" );
        }
    }
*/
}
