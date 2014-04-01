package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.Set;

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
            boolean hidingNoop,
            boolean simplified,
            boolean topBottom,
            boolean showingAssets,
            Set<Long> expansions ) {
        super( id, segmentModel, partModel, expansions );
        setShowingGoals( showingGoals );
        setShowingAssets( showingAssets );
        setShowingConnectors( showingConnectors );
        setHidingNoop( hidingNoop );
        setSimplified( simplified );
        setTopBottom( topBottom );
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
        addSimplifyControl();
    }

    private void addMinimizeControl() {
        // De-maximize
        WebMarkupContainer minimize = new WebMarkupContainer( "minimized" );
        minimize.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String props = isShowingGoals() ? "showGoals" : "";
                props += isShowingConnectors() ? " showConnectors" : "";
                props += isShowingAssets() ? " showAssets" : "";
                props += isHidingNoop() ? " hideNoop" : "";
                props += isSimplified() ? " simplify" : "";
                props += isTopBottom() ? "" : " leftRight";
                update( target, new Change(
                        Change.Type.Minimized,
                        getSegment(),
                        props ) );
            }
        } );
        addTipTitle( minimize, "Restore size" );
        getControlsContainer().add( minimize );
    }


}
