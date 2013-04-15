package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.pages.PlanPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.PropertyModel;

import java.util.Map;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/13/13
 * Time: 7:54 PM
 */
public class ChecklistFlowDiagramPanel extends AbstractDiagramPanel {

    private final PropertyModel<Part> partModel;

    public ChecklistFlowDiagramPanel( String id, PropertyModel<Part> partModel, Settings settings ) {
        super( id, settings );
        this.partModel = partModel;
        init();
    }

    @Override
    protected String getContainerId() {
        return "checklist-flow";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newChecklistFlowDiagram( getPart(), getDiagramSize(), getOrientation() );
    }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "checklist.png?" )
                .append( PlanPage.SEGMENT_PARM )
                .append( "=" )
                .append( getPart().getSegment().getId())
                .append("&")
                .append( PlanPage.PART_PARM )
                .append("=")
                .append(getPart().getId());
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
        // Do nothing
    }

    @Override
    protected void onSelectVertex( String graphId, String vertexId, String domIdentifier, int scrollTop, int scrollLeft, Map<String, String> extras, AjaxRequestTarget target ) {
        // Do nothing
    }

    @Override
    protected void onSelectEdge( String graphId, String edgeId, String domIdentifier, int scrollTop, int scrollLeft, Map<String, String> extras, AjaxRequestTarget target ) {
        // Do nothing
    }

    private Part getPart() {
        return partModel.getObject();
    }
}
