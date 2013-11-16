package com.mindalliance.channels.graph.diagrams;

import com.mindalliance.channels.core.community.Agent;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.engine.analysis.GraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.CommandChainsGraphBuilder;
import com.mindalliance.channels.engine.analysis.graph.CommandRelationship;
import com.mindalliance.channels.engine.analysis.graph.Contact;
import com.mindalliance.channels.graph.AbstractDiagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.graph.GraphRenderer;
import org.jgrapht.Graph;

import java.io.OutputStream;

/**
 * User command chain diagram.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 12:48 PM
 */
public class CommandChainsDiagram extends AbstractDiagram<Contact, CommandRelationship> {

    private ChannelsUser user;
    private Agent agent;

    private final String algo;

    public CommandChainsDiagram( ChannelsUser user, double[] diagramSize, String orientation ) {
        this( user, diagramSize, orientation, "dot" );
    }

    public CommandChainsDiagram( ChannelsUser user, double[] diagramSize, String orientation, String algo ) {
        super( diagramSize, orientation );
        this.algo = algo;
        this.user = user;
    }

    public CommandChainsDiagram( Agent agent, double[] diagramSize, String orientation ) {
        this( agent, diagramSize, orientation, "dot" );
    }

    public CommandChainsDiagram( Agent agent, double[] diagramSize, String orientation, String algo ) {
        super( diagramSize, orientation );
        this.algo = algo;
        this.agent = agent;
    }


    @Override
    public void render( String ticket,
                        String outputFormat,
                        OutputStream outputStream,
                        Analyst analyst,
                        DiagramFactory diagramFactory,
                        CommunityService communityService ) throws DiagramException {
        double[] diagramSize = getDiagramSize();
        String orientation = getOrientation();
        GraphBuilder<Contact, CommandRelationship> userCommandChainsGraphBuilder =
                user != null
                ? new CommandChainsGraphBuilder( user, communityService )
                : new CommandChainsGraphBuilder( agent, communityService );
        Graph<Contact, CommandRelationship> graph = userCommandChainsGraphBuilder.buildDirectedGraph();
        GraphRenderer<Contact, CommandRelationship> graphRenderer =
                diagramFactory.getGraphRenderer().cloneSelf();
        graphRenderer.setAlgo( algo );
        graphRenderer.resetHighlight();
        CommandChainsMetaProvider metaProvider =
                user != null
                        ? new CommandChainsMetaProvider(
                        user,
                        outputFormat,
                        diagramFactory.getImageDirectory(),
                        diagramFactory.getUserIconDirectory(),
                        analyst,
                        communityService )
                        : new CommandChainsMetaProvider(
                        agent,
                        outputFormat,
                        diagramFactory.getImageDirectory(),
                        diagramFactory.getUserIconDirectory(),
                        analyst,
                        communityService );
        if ( diagramSize != null )
            metaProvider.setGraphSize( diagramSize );
        if ( orientation != null )
            metaProvider.setGraphOrientation( orientation );
        CommandChainsDOTExporter dotExporter = new CommandChainsDOTExporter( metaProvider );
        graphRenderer.render( communityService, graph, dotExporter, outputFormat, ticket, outputStream );
    }
}
