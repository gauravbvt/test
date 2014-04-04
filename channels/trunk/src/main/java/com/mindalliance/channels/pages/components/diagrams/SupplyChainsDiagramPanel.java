package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.pages.png.ProceduresPng;
import com.mindalliance.channels.pages.png.SupplyChainsPng;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 4:41 PM
 */
public class SupplyChainsDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( SupplyChainsDiagramPanel.class );

    private boolean summarizeByOrgType;
    private boolean summarizeByOrg;
    private boolean summarizeByRole;

    private MaterialAsset materialAsset;

    public SupplyChainsDiagramPanel( String id,
                                     MaterialAsset materialAsset,
                                     boolean summarizeByOrgType,
                                     boolean summarizeByOrg,
                                     boolean summarizeByRole,
                                     Settings settings ) {
        super( id, settings );
        this.materialAsset = materialAsset;
        this.summarizeByOrgType = summarizeByOrgType;
        this.summarizeByOrg = summarizeByOrg;
        this.summarizeByRole = summarizeByRole;
        init();
    }

    @Override
    protected String getContainerId() {
        return "supplyChains";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newSupplyChainsDiagram(
                materialAsset,
                summarizeByOrgType,
                summarizeByOrg,
                summarizeByRole,
                getDiagramSize(),
                getOrientation()
        );
    }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "supplyChains.png?" );
        sb.append( "&" );
        sb.append( SupplyChainsPng.ASSET_PARM );
        sb.append( "=" );
        sb.append( materialAsset == null ? SupplyChainsPng.NONE : materialAsset.getId() );
        sb.append( "&" );
        sb.append( ProceduresPng.SUMMARIZE );
        sb.append("=");
        sb.append( summarizeByOrgType && summarizeByRole
                ? SupplyChainsPng.SUMMARIZE_BY_ORG_TYPE_AND_ROLE
                : summarizeByOrgType
                ? SupplyChainsPng.SUMMARIZE_BY_ORG_TYPE
                : summarizeByOrg
                ? SupplyChainsPng.SUMMARIZE_BY_ORG
                : summarizeByRole
                ? SupplyChainsPng.SUMMARIZE_BY_ROLE
                : "NONE" );
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
        sb.append( "&" );
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
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String, String> extras,
            AjaxRequestTarget target ) {
        // Do nothing

    }

    @Override
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String, String> extras,
            AjaxRequestTarget target ) {
        try {
            QueryService queryService = getQueryService();
            Segment segment = queryService.find( Segment.class, Long.valueOf( graphId ) );
            Part part = (Part) segment.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, part );
                change.addQualifier( "segment", segment );
                change.setScript( js );
                update( target, change );
            } else {
                throw new NotFoundException();
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String, String> extras,
            AjaxRequestTarget target ) {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeSeed() {
        // Force regeneration
        return getCollaborationModel().isDevelopment() ? "&_modified=" + System.currentTimeMillis() : "";
    }

}
