package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.png.ProceduresPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/9/11
 * Time: 10:30 AM
 */
public class ProcedureMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ProcedureMapDiagramPanel.class );


    private Segment segment;
    private boolean summarizeByOrgType;
    private boolean summarizeByOrg;
    private boolean summarizeByRole;
    private ModelEntity focusEntity;

    public ProcedureMapDiagramPanel(
            String id,
            Segment segment,
            boolean summarizeByOrgType,
            boolean summarizeByOrg,
            boolean summarizeByRole,
            ModelEntity focusEntity,
            Settings settings ) {
        super( id, settings );
        this.segment = segment;
        this.summarizeByOrgType = summarizeByOrgType;
        this.summarizeByOrg = summarizeByOrg;
        this.summarizeByRole = summarizeByRole;
        this.focusEntity = focusEntity;
        init();
    }

    @Override
    protected String getContainerId() {
        return "mappedProcedures";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newProcedureMapDiagram(
                segment,
                summarizeByOrgType,
                summarizeByOrg,
                summarizeByRole,
                focusEntity,
                getDiagramSize(),
                getOrientation()
        );
    }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "/procedures.png?" );
        sb.append( "&");
        sb.append( PlanPage.SEGMENT_PARM );
        sb.append("=");
        sb.append( segment == null ? "NONE" : segment.getId() );
        sb.append( "&" );
        sb.append( ProceduresPage.SUMMARIZE );
        sb.append("=");
        sb.append( summarizeByOrgType
                ? ProceduresPage.SUMMARIZE_BY_ORG_TYPE
                : summarizeByOrg
                ? ProceduresPage.SUMMARIZE_BY_ORG
                : summarizeByRole
                ? ProceduresPage.SUMMARIZE_BY_ROLE
                : "NONE" );
        if ( focusEntity != null ) {
            sb.append( "&" ); sb.append( ProceduresPage.FOCUS_CLASS ); sb.append( "=");
            sb.append( focusEntity.getClass().getSimpleName() );
            sb.append( "&" ); sb.append( ProceduresPage.FOCUS_ID ); sb.append( "=");
            sb.append( focusEntity.getId() );
        }
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
        return sb.toString();
    }

    @Override
    protected void onClick( AjaxRequestTarget target ) {
        // DO nothing
    }

    @Override
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        try {
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, segment ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
            this.update( target, new Change( Change.Type.Selected, getPlan() ) );
        }
    }

    @Override
    protected void onSelectVertex(
            String graphId,
            String vertexId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        try {
            QueryService queryService = getQueryService();
            Segment segment = queryService.find( Segment.class, Long.valueOf( graphId ) );

            // todo - deconstruct vertex id
            Part part = (Part) segment.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, part );
                change.addQualifier( "segment", segment );
                change.addQualifier( "focus", focusEntity );
                processExtras( extras, change );
                change.setScript( js );
                this.update( target, change );

            } else {
                throw new NotFoundException();
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    private void processExtras( Map<String, String> extras, Change change ) throws NotFoundException {
        QueryService queryService = getQueryService();
        if ( extras.containsKey( "_actor" ) ) {
            long id = Long.valueOf( extras.get("_actor") );
            if ( id != 0 )
                change.addQualifier(
                        "actor",
                        queryService.find(Actor.class, id ) );
        }
        if ( extras.containsKey( "_role" ) ) {
            long id = Long.valueOf( extras.get("_role") );
            if ( id != 0 )
                change.addQualifier(
                        "role",
                        queryService.find(Role.class, id ) );
        }
        if ( extras.containsKey( "_org" ) ) {
            long id = Long.valueOf( extras.get("_org") );
            if ( id != 0 )
                change.addQualifier(
                        "organization",
                        queryService.find(Organization.class, id ) );
        }
    }

    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String,String> extras,
            AjaxRequestTarget target ) {
        Long id = Long.valueOf( edgeId );
        try {
            Flow flow = getQueryService().find( Flow.class, Long.valueOf( edgeId ) );
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Change change = new Change( Change.Type.Selected, flow );
            change.addQualifier( "segment", segment );
            change.addQualifier( "focus", focusEntity );
            processExtras( extras, change );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selected flow not found at id " + id );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeSeed() {
        // Force regeneration
        return "&_modified=" + System.currentTimeMillis();
    }

}
