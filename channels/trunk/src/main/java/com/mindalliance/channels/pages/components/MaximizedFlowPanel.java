package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
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
     * Part model.
     */
    private IModel<Part> partModel;
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

    public MaximizedFlowPanel( String id, IModel<Part> partModel) {
        super( id );
        this.partModel = partModel;
        init();
    }

    private void init() {
        addFlowSizing();
        addFlowDiagram();
    }

    private void addFlowSizing() {
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
                flowDiagramDim[0] = ( Double.parseDouble( swidth ) - 20 ) / 96.0;
                flowDiagramDim[1] = ( Double.parseDouble( sheight ) - 20 ) / 96.0;
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullSize = new WebMarkupContainer( "full" );
        fullSize.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                flowDiagramDim = new double[2];
                addFlowDiagram();
                target.addComponent( flowMapDiagramPanel );
            }
        } );
        add( fullSize );
        WebMarkupContainer minimize = new WebMarkupContainer( "minimized" );
        minimize.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Minimized, getSegment() ) );
            }
        } );
        add( minimize );
    }

    private void addFlowDiagram() {
        double[] dim = flowDiagramDim[0] <= 0.0 || flowDiagramDim[1] <= 0.0 ? null : flowDiagramDim;
        Settings settings = new Settings( "#maximized-graph", null, dim, true, true );

        flowMapDiagramPanel =
                new FlowMapDiagramPanel(
                        "flow-map",
                        new PropertyModel<Segment>( this, "segment"),
                        partModel,
                        settings );
        flowMapDiagramPanel.setOutputMarkupId( true );
        addOrReplace( flowMapDiagramPanel );
    }

    /**
     * The part currently selected.
     * @return a part
     */
    public Part getPart() {
        return partModel.getObject();
    }

    /**
     * The segment displayed.
     * @return a segment
     */
    public Segment getSegment() {
        return getPart().getSegment();
    }


}
