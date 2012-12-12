package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.diagrams.FlowMapDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.Settings;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.Set;

/**
 * Abstract commandable panel containing a flow map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/18/11
 * Time: 8:47 PM
 */
public abstract class AbstractFlowMapContainingPanel extends AbstractCommandablePanel {


    /**
      * Expected screen resolution.
      */
     static private double DPI = 96.0;

    /**
     * Whether the flow map was resized to fit.
     */
    private boolean resizedToFit = false;
    /**
     * Whether to show goals in flow map.
     */
    private boolean showingGoals = true;
    /**
     * Whether to show conceptual tasks and flows in flow map.
     */
    private boolean hidingNoop = false;
    /**
     * Whether to show connectors in flow map.
     */
    private boolean showingConnectors = false;

    /**
     * Width, height dimension contraints on the flow diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] flowDiagramDim = new double[2];


    /**
     * Flowmap viewing controls container.
     */
    private WebMarkupContainer controlsContainer;
    private IModel<Segment> segmentModel;
    private final IModel<Part> partModel;

    /**
     * Flow diagram panel.
     */
    protected FlowMapDiagramPanel flowMapDiagramPanel;



    public AbstractFlowMapContainingPanel(
            String id,
            IModel<Segment> segmentModel,
            IModel<Part> partModel,
            Set<Long> expansions ) {
        super( id, segmentModel, expansions );
         this.segmentModel = segmentModel;
        this.partModel = partModel;
    }

    public boolean isResizedToFit() {
        return resizedToFit;
    }

    public void setResizedToFit( boolean resizedToFit ) {
        this.resizedToFit = resizedToFit;
    }

    public boolean isShowingGoals() {
        return showingGoals;
    }

    public void setShowingGoals( boolean showingGoals ) {
        this.showingGoals = showingGoals;
    }

    public boolean isHidingNoop() {
        return hidingNoop;
    }

    public void setHidingNoop( boolean hidingNoop ) {
        this.hidingNoop = hidingNoop;
    }

    public boolean isShowingConnectors() {
        return showingConnectors;
    }

    public void setShowingConnectors( boolean showingConnectors ) {
        this.showingConnectors = showingConnectors;
    }

    public WebMarkupContainer getControlsContainer() {
        return controlsContainer;
    }

    public Segment getSegment() {
        return segmentModel.getObject();
    }

    protected void addFlowDiagram() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( getFlowMapDomId(), null, dim, true, true );

        flowMapDiagramPanel =
                new FlowMapDiagramPanel( "flow-map",
                        segmentModel,
                        partModel,
                        settings,
                        showingGoals,
                        showingConnectors,
                        hidingNoop );
        flowMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( flowMapDiagramPanel );
    }


    protected void addFlowMapViewingControls() {
        controlsContainer = new WebMarkupContainer( "controls" );
        controlsContainer.setOutputMarkupId( true );
        addOrReplace( controlsContainer );
        addReduceToFitControl();
        addShowGoalsControl();
        addShowConnectorsControl();
        addHideConceptualControl();
        addShowLegendControl();
    }

    private void addShowLegendControl() {
        WebMarkupContainer legend = new WebMarkupContainer( "legend" );
        legend.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Explained, getSegment(), "legend" ) );
            }
        } );
        controlsContainer.add( legend );
    }


    private void addHideConceptualControl() {
        WebMarkupContainer hideNoop = new WebMarkupContainer( "hideNoop" );
        hideNoop.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                hidingNoop = !hidingNoop;
                addFlowDiagram();
                target.add( flowMapDiagramPanel );
                addFlowMapViewingControls();
                target.add( controlsContainer );
            }
        } );
        controlsContainer.add( hideNoop );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "hide_noop_icon" );
        icon.add( new AttributeModifier(
                "src",
                new Model<String>( hidingNoop
                        ? "images/hide_noop_on.png"
                        : "images/hide_noop.png" ) ) );
        icon.add( new AttributeModifier(
                "title",
                new Model<String>( hidingNoop
                        ? "Show all"
                        : "Hide not realizable" ) ) );
        hideNoop.add( icon );
    }

    private void addShowConnectorsControl() {
        WebMarkupContainer showConnectors = new WebMarkupContainer( "showConnectors" );
        showConnectors.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                showingConnectors = !showingConnectors;
                addFlowDiagram();
                target.add( flowMapDiagramPanel );
                addFlowMapViewingControls();
                target.add( controlsContainer );
            }
        } );
        controlsContainer.add( showConnectors );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "show_connectors_icon" );
        icon.add( new AttributeModifier(
                "src",
                new Model<String>( showingConnectors
                        ? "images/show_connectors_on.png"
                        : "images/show_connectors.png" ) ) );
        icon.add( new AttributeModifier(
                "title",
                new Model<String>( showingConnectors
                        ? "Hide  info needs and capabilities"
                        : "Show info needs and capabilities" ) ) );
        showConnectors.add( icon );
    }

    private void addShowGoalsControl() {
        WebMarkupContainer showGoals = new WebMarkupContainer( "showGoals" );
        showGoals.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                showingGoals = !showingGoals;
                addFlowDiagram();
                target.add( flowMapDiagramPanel );
                addFlowMapViewingControls();
                target.add( controlsContainer );
            }
        } );
        controlsContainer.add( showGoals );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "show_goals_icon" );
        icon.add( new AttributeModifier(
                "src",
                new Model<String>( showingGoals
                        ? "images/show_goals_on.png"
                        : "images/show_goals.png" ) ) );
        icon.add( new AttributeModifier(
                "title",
                new Model<String>( showingGoals
                        ? "Hide goals"
                        : "Show goals" ) ) );
        showGoals.add( icon );
    }

     private void addReduceToFitControl() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            @Override
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String domIdentifier = getFlowMapDomId();
                String script = "wicketAjaxGet('"
                        + getCallbackUrl(  )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            @Override
            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getQueryParameters().getParameterValue( "width" ).toString();
                String sheight = requestCycle.getRequest().getQueryParameters().getParameterValue( "height" ).toString();
                if ( !resizedToFit ) {
                    flowDiagramDim[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    flowDiagramDim[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    flowDiagramDim = new double[2];
                }
                resizedToFit = !resizedToFit;
                addFlowDiagram();
                target.add( flowMapDiagramPanel );
                addFlowMapViewingControls();
                target.add( controlsContainer );
            }
        } );
        controlsContainer.add( reduceToFit );
        // icon
        WebMarkupContainer icon = new WebMarkupContainer( "fit_icon" );
        icon.add( new AttributeModifier(
                "src",
                new Model<String>( resizedToFit
                        ? "images/fit_on.png"
                        : "images/fit.png" ) ) );
        icon.add( new AttributeModifier(
                "title",
                new Model<String>( resizedToFit
                        ? "Show flow map at normal size"
                        : "Reduce flow map to fit" ) ) );
        reduceToFit.add( icon );
    }

    protected abstract String getFlowMapDomId();


}
