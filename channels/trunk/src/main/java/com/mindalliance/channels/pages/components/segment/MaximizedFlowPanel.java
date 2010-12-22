package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Maximized flow map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 4, 2010
 * Time: 9:29:46 PM
 */
public class MaximizedFlowPanel extends AbstractUpdatablePanel {
    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;
    /**
     * Part model.
     */
    private IModel<Part> partModel;
    /**
     * Whether to show goals.
     */
    private boolean showingGoals = false;
     /**
     * Whether to show connectors.
     */
    private boolean showingConnectors = false;
    private boolean hidingNoop;
    /**
     * Flow diagram panel.
     */
    private FlowMapDiagramPanel flowMapDiagramPanel;
    /**
     * Width, height dimension contraints on the flow diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] flowDiagramDim = new double[2];
    /**
     * Whether the flow map was resized to fit.
     */
    private boolean resizedToFit = false;

    public MaximizedFlowPanel(
            String id,
            IModel<Part> partModel,
            boolean showingGoals,
            boolean showingConnectors,
            boolean hidingNoop ) {
        super( id );
        this.partModel = partModel;
        this.showingGoals = showingGoals;
        this.showingConnectors = showingConnectors;
        this.hidingNoop = hidingNoop;
        init();
    }

    private void init() {
        addFlowViewControls();
        addFlowDiagram();
    }

    private void addFlowViewControls() {
        // show hide connectors
        WebMarkupContainer showConnectors = new WebMarkupContainer( "showConnectors" );
        showConnectors.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                showingConnectors = !showingConnectors;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( showConnectors );
        // show hide goals
        WebMarkupContainer showGoals = new WebMarkupContainer( "showGoals" );
        showGoals.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                showingGoals = !showingGoals;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( showGoals );

        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = "#maximized-graph";
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                if ( !resizedToFit ) {
                    flowDiagramDim[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    flowDiagramDim[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    flowDiagramDim = new double[2];
                }
                resizedToFit = !resizedToFit;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( reduceToFit );
        // De-maximize
        WebMarkupContainer minimize = new WebMarkupContainer( "minimized" );
        minimize.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                String props = showingGoals ? "showGoals" : "";
                props += showingConnectors ? " showConnectors" : "";
                props += hidingNoop ? " hideNoop" : "";
                update( target, new Change(
                        Change.Type.Minimized,
                        getSegment(),
                        props ) );
            }
        } );
        add( minimize );
        // Show/hide non-operational
        WebMarkupContainer hideNoop = new WebMarkupContainer( "hideNoop" );
        hideNoop.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                hidingNoop = !hidingNoop;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( hideNoop );
        // Legend
        WebMarkupContainer legend = new WebMarkupContainer( "legend" );
        legend.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Explained, getSegment(), "legend" ) );
            }
        } );
        add( legend );

    }

    private void addFlowDiagram() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( "#maximized-graph", null, dim, true, true );

        flowMapDiagramPanel =
                new FlowMapDiagramPanel(
                        "flow-map",
                        new PropertyModel<Segment>( this, "segment" ),
                        partModel,
                        settings,
                        showingGoals,
                        showingConnectors,
                        hidingNoop );
        flowMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( flowMapDiagramPanel );
    }

    /**
     * The part currently selected.
     *
     * @return a part
     */
    public Part getPart() {
        return partModel.getObject();
    }

    /**
     * The segment displayed.
     *
     * @return a segment
     */
    public Segment getSegment() {
        return getPart().getSegment();
    }


}
