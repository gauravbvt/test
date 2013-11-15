package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/13/13
 * Time: 2:18 PM
 */
public class UserCommandChainsDiagramPanel extends AbstractDiagramPanel {

    private final IModel<? extends ChannelsUser> userModel;
    private final String algo;

    public UserCommandChainsDiagramPanel( String id,
                                          IModel<? extends ChannelsUser> userModel,
                                          double[] diagramSize,
                                          String domIdentifier ) {
        this( id, userModel, diagramSize, domIdentifier, "dot" );

    }
        public UserCommandChainsDiagramPanel( String id,
                                              IModel<? extends ChannelsUser> userModel,
                                              double[] diagramSize,
                                              String domIdentifier,
                                              String algo ) {
        super( id, new Settings( domIdentifier, null, diagramSize, true, true ) );
        this.userModel = userModel;
        this.algo = algo;
        init();
    }

    @Override
    protected String getContainerId() {
        return "command-chains-diagram";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newUserCommandChainsDiagram(
                userModel.getObject(),
                getDiagramSize(),
                getOrientation(),
                algo );
    }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "command_chains.png?user=" );
        sb.append( userModel.getObject().getUsername() );
        sb.append( "&algo=");
        sb.append( algo );
        double[] diagramSize = getDiagramSize();
        if ( diagramSize != null ) {
            sb.append( "&size=" );
            sb.append( diagramSize[0] );
            sb.append( "," );
            sb.append( diagramSize[1] );
        }
        String orientation = getOrientation();
        if ( orientation != null ) {
            sb.append( "&orientation=" );
            sb.append( orientation );
        }
        sb.append( "&");
        sb.append( TICKET_PARM );
        sb.append( '=' );
        sb.append( getTicket() );
        return sb.toString();
    }

    @Override
    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    @Override
    protected void onSelectGraph( String graphId, String domIdentifier, int scrollTop, int scrollLeft, Map<String, String> extras, AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    @Override
    protected void onSelectVertex( String graphId, String vertexId, String domIdentifier, int scrollTop, int scrollLeft, Map<String, String> extras, AjaxRequestTarget target ) {
        // Do nothing -- never called
    }

    @Override
    protected void onSelectEdge( String graphId, String edgeId, String domIdentifier, int scrollTop, int scrollLeft, Map<String, String> extras, AjaxRequestTarget target ) {
        // Do nothing -- never called
    }
}
