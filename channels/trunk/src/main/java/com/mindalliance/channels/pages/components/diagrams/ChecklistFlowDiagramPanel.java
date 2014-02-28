package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.CommunicationStep;
import com.mindalliance.channels.core.model.checklist.Step;
import com.mindalliance.channels.core.model.checklist.SubTaskStep;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.pages.ModelPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
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
    private final boolean interactive;

    public ChecklistFlowDiagramPanel( String id, PropertyModel<Part> partModel, Settings settings, boolean interactive ) {
        super( id, settings );
        this.partModel = partModel;
        this.interactive = interactive;
        init();
    }

    @Override
    protected String getContainerId() {
        return "checklist-flow";
    }

    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newChecklistFlowDiagram( getPart(), getDiagramSize(), getOrientation(), interactive );
    }

    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "checklist.png?" )
                .append( ModelPage.SEGMENT_PARM )
                .append( "=" )
                .append( getPart().getSegment().getId() )
                .append( "&" )
                .append( ModelPage.PART_PARM )
                .append( "=" )
                .append( getPart().getId() );
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
        if ( interactive ) {
            sb.append( "&interactive= true");
        }
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
        int index = Integer.parseInt( vertexId );
        if ( index < 1000 ) {
            List<Step> steps = getPart().getEffectiveChecklist().listEffectiveSteps();
            if ( index < steps.size() && index >= 0 ) {
                Step step = steps.get( index );
                if ( step.isSubTaskStep() ) {
                    SubTaskStep subTaskStep = (SubTaskStep) step;
                    Part subTask = subTaskStep.getSubTask();
                    Change change = new Change( Change.Type.AspectViewed, subTask );
                    change.setProperty( "checklist-flow" );
                    update( target, change );
                } else if ( step.isCommunicationStep() ) {
                    CommunicationStep commStep = (CommunicationStep) step;
                    Flow sharing = commStep.getSharing();
                    Change change = new Change( Change.Type.Selected, sharing );
                    update( target, change );
                }
            }
        }
    }

    @Override
    protected void onSelectEdge( String graphId, String edgeId, String domIdentifier, int scrollTop, int scrollLeft, Map<String, String> extras, AjaxRequestTarget target ) {
        // Do nothing
    }

    private Part getPart() {
        return partModel.getObject();
    }
}
