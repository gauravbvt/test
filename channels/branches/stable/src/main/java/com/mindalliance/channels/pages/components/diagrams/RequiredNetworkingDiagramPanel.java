package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Required networking diagram panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 10:06 AM
 */
public class RequiredNetworkingDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RequiredNetworkingDiagramPanel.class );
    private final Model<Phase.Timing> timingModel;
    private final Model<Event> eventModel;
    private final Organization selectedOrganization;
    private final RequirementRelationship selectedRequirementRel;

    public RequiredNetworkingDiagramPanel(
            String id,
            Model<Phase.Timing> timingModel,
            Model<Event> eventModel,
            Organization selectedOrganization,
            RequirementRelationship selectedRequirementRel,
            double[] diagramSize,
            String domIdentifier ) {
        this( id,
                timingModel,
                eventModel,
                selectedOrganization,
                selectedRequirementRel,
                diagramSize,
                null,
                true,
                domIdentifier );
    }

    public RequiredNetworkingDiagramPanel(
            String id,
            Model<Phase.Timing> timingModel,
            Model<Event> eventModel,
            Organization selectedOrganization,
            RequirementRelationship selectedRequirementRel,
            double[] diagramSize,
            String orientation,
            boolean withImageMap,
            String domIdentifier ) {
        super( id, new Settings( domIdentifier, orientation, diagramSize, true, withImageMap ) );
        this.timingModel = timingModel;
        this.eventModel = eventModel;
        this.selectedOrganization = selectedOrganization;
        this.selectedRequirementRel = selectedRequirementRel;
        init();
    }

    @Override
    protected String getContainerId() {
        return "required-network";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newRequiredNetworkingDiagram(
                getTiming(),
                getEvent(),
                selectedOrganization,
                selectedRequirementRel,
                getDiagramSize(),
                getOrientation() );
     }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "required.png?organization=" );
        sb.append( selectedOrganization == null ? "NONE" : selectedOrganization.getId() );
        sb.append("&connection=");
        sb.append( selectedRequirementRel == null ? "NONE" : selectedRequirementRel.getId() );
        if ( getTiming() != null ) {
            sb.append( "&timing=" );
            sb.append( getTiming().name() );
        }
        if ( getEvent() != null ) {
            sb.append( "&event=" );
            sb.append( getEvent().getId() );
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
        sb.append( "&");
        sb.append( TICKET_PARM );
        sb.append( '=' );
        sb.append( getTicket() );
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    protected String makeSeed() {
        // Force regeneration
        return getPlan().isDevelopment() ? "&_modified=" + System.currentTimeMillis() : "";
    }

    @Override
    protected void onClick( AjaxRequestTarget target ) {
        update( target, new Change( Change.Type.Selected, getPlan() ) );
    }

    @Override
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String, String> extras,
            AjaxRequestTarget target ) {
        // Never called
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
             Organization org = getQueryService().find( Organization.class, Long.valueOf( vertexId ) );
             if ( !org.equals( selectedOrganization ) ) {
                 String js = scroll( domIdentifier, scrollTop, scrollLeft );
                 Change change = new Change( Change.Type.Selected, org, "organization" );
                 change.setScript( js );
                 update( target, change );
             }
         } catch ( NotFoundException e ) {
             LOG.warn( "Not found", e );
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
        RequirementRelationship requirementRelationship = new RequirementRelationship();
        requirementRelationship.setId( Long.valueOf( edgeId ), null, getQueryService(), getAnalyst() );
        String js = scroll( domIdentifier, scrollTop, scrollLeft );
        Change change = new Change( Change.Type.Selected, requirementRelationship );
        change.setScript( js );
        update( target, change );
    }

    private Phase.Timing getTiming() {
        return timingModel.getObject();
    }

    private Event getEvent() {
        return eventModel.getObject();
    }
}
