package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * Maximized flow map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 4, 2010
 * Time: 9:29:46 PM
 */
public class MaximizedFlowPanel extends AbstractFlowMapContainingPanel {

    public MaximizedFlowPanel(
            String id,
            IModel<Segment> segmentModel,
            IModel<Part> partModel,
            boolean showingGoals,
            boolean showingConnectors,
            boolean hidingNoop ) {
        super( id, segmentModel, partModel, null );
        setShowingGoals( showingGoals );
        setShowingConnectors( showingConnectors );
        setHidingNoop( hidingNoop );
        init();
    }

    private void init() {
        addFlowMapViewingControls();
        addFlowDiagram();
    }

    @Override
    protected String getFlowMapDomId() {
        return "#maximized-graph";
    }

    protected void addFlowMapViewingControls() {
        super.addFlowMapViewingControls();
        addMinimizeControl();
    }

    private void addMinimizeControl() {
        // De-maximize
        WebMarkupContainer minimize = new WebMarkupContainer( "minimized" );
        minimize.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String props = isShowingGoals() ? "showGoals" : "";
                props += isShowingConnectors() ? " showConnectors" : "";
                props += isHidingNoop() ? " hideNoop" : "";
                update( target, new Change(
                        Change.Type.Minimized,
                        getSegment(),
                        props ) );
            }
        } );
        getControlsContainer().add( minimize );
    }


}
