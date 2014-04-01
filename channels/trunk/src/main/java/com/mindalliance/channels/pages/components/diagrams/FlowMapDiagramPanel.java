package com.mindalliance.channels.pages.components.diagrams;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.graph.Diagram;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 11, 2009
 * Time: 11:31:48 AM
 */
public class FlowMapDiagramPanel extends AbstractDiagramPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( FlowMapDiagramPanel.class );
    /**
     * Segment to be diagrammed
     */
    private IModel<Segment> segmentModel;
    /**
     * Selected node. Null if none selected.
     */
    private IModel<Part> partModel;
    /**
     * Whether to show goals.
     */
    private boolean showingGoals;
    /**
     * Whether to show assets.
     */
    private boolean showingAssets;
    /**
     * Whether to show connectors.
     */
    private boolean showingConnectors;
    /**
     * Whether to hide conceptual tasks and flows.
     */
    private boolean hidingNoop;
    /**
     * whether to show a simplified flow map.
     */
    private boolean simplified;
    /**
     * Highlighted flow.
     */
    private Flow flow;

    public FlowMapDiagramPanel( String id,
                                IModel<Segment> segmentModel,
                                IModel<Part> partModel,
                                Settings settings ) {
        this( id, segmentModel, partModel, settings, false, false, false, false, false );
    }

    public FlowMapDiagramPanel( String id,
                                IModel<Segment> segmentModel,
                                IModel<Part> partModel,
                                Settings settings,
                                boolean showingGoals,
                                boolean showingConnectors,
                                boolean hidingNoop,
                                boolean simplified,
                                boolean showingAssets ) {
        super( id, settings );
        this.segmentModel = segmentModel;
        this.partModel = partModel;
        this.showingGoals = showingGoals;
        this.showingAssets = showingAssets;
        this.showingConnectors = showingConnectors;
        this.hidingNoop = hidingNoop;
        this.simplified = simplified;
         init();
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow( Flow flow ) {
        this.flow = flow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getContainerId() {
        return "flow-map";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Diagram makeDiagram() {
        return getDiagramFactory().newFlowMapDiagram(
                getSegment(),
                getPart(),
                getFlow(),
                getDiagramSize(),
                getOrientation(),
                showingGoals,
                showingConnectors,
                hidingNoop,
                simplified,
                showingAssets );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String makeDiagramUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append( "segment.png?segment=" );
        sb.append( getSegment().getId() );
        sb.append( "&node=" );
        if ( getPart() != null ) {
            sb.append( getPart().getId() );
        } else {
            sb.append( "NONE" );
        }
        sb.append( "&flow=" );
        if ( getFlow() != null ) {
            sb.append( getFlow().getId() );
        } else {
            sb.append( "NONE" );
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
        if ( showingGoals ) {
            sb.append( "&showingGoals=" );
            sb.append( showingGoals );
        }
        if ( showingAssets ) {
            sb.append( "&showingAssets=" );
            sb.append( showingAssets );
        }
        if ( showingConnectors ) {
            sb.append( "&showingConnectors=" );
            sb.append( showingConnectors );
        }
        if ( hidingNoop ) {
            sb.append( "&hidingNoop=" );
            sb.append( hidingNoop );
        }
        if ( simplified ) {
            sb.append( "&simplifying=" );
            sb.append( simplified );
        }

        sb.append( "&" );
        sb.append( TICKET_PARM );
        sb.append( '=' );
        sb.append( getTicket() );
        return sb.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onClick( AjaxRequestTarget target ) {
        // Do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onSelectGraph(
            String graphId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String, String> extras,
            AjaxRequestTarget target ) {
        try {
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( graphId ) );
            this.update( target, new Change( Change.Type.Selected, segment ) );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
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
            Segment segment = getQueryService().find( Segment.class, Long.valueOf( graphId ) );
            Part part = (Part) segment.getNode( Long.valueOf( vertexId ) );
            if ( part != null ) {
                String js = scroll( domIdentifier, scrollTop, scrollLeft );
                Change change = new Change( Change.Type.Selected, part );
                String props = isShowingGoals() ? "showGoals" : "";
                props += isShowingConnectors() ? " showConnectors" : "";
                props += isShowingAssets() ? " showAssets" : "";
                props += isHidingNoop() ? " hideNoop" : "";
                props += isSimplified() ? " simplify" : "";
                props += isTopBottom() ? "" : " leftRight";
                change.setProperty( props );
                change.setScript( js );
                this.update( target, change );

            } else {
                LOG.warn( "Selection not found" );
            }
        } catch ( NotFoundException e ) {
            LOG.warn( "Selection not found", e );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void onSelectEdge(
            String graphId,
            String edgeId,
            String domIdentifier,
            int scrollTop,
            int scrollLeft,
            Map<String, String> extras,
            AjaxRequestTarget target ) {
        long id = Long.valueOf( edgeId );
        try {
            Flow flow = getQueryService().find( Flow.class, id );
            String js = scroll( domIdentifier, scrollTop, scrollLeft );
            Change change = new Change( Change.Type.Selected, flow );
            String props = isShowingGoals() ? "showGoals" : "";
            props += isShowingConnectors() ? " showConnectors" : "";
            props += isShowingAssets() ? " showAssets" : "";
            props += isHidingNoop() ? " hideNoop" : "";
            props += isSimplified() ? " simplify" : "";
            props += isTopBottom() ? "" : " leftRight";
            change.setProperty( props );
            change.setScript( js );
            update( target, change );
        } catch ( NotFoundException e ) {
            LOG.warn( "Selected flow not found at id " + id );
        }
    }

    private boolean isTopBottom() {
        return getOrientation().equals( "TB" );
    }

    private Segment getSegment() {
        return segmentModel.getObject();
    }

    private Part getPart() {
        if ( partModel == null )
            return null;
        else
            return partModel.getObject();
    }

    public boolean isShowingGoals() {
        return showingGoals;
    }

    public boolean isShowingConnectors() {
        return showingConnectors;
    }

    public boolean isHidingNoop() {
        return hidingNoop;
    }

    public boolean isSimplified() {
        return simplified;
    }

    public boolean isShowingAssets() {
        return showingAssets;
    }
}

