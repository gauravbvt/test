package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramAjaxBehavior;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.Set;

/**
 * Resizable diagram panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2010
 * Time: 4:44:20 PM
 */
abstract public class AbstractResizableDiagramPanel extends AbstractUpdatablePanel {
    /**
     * Expected screen resolution.
     */
    static private double DPI = 96.0;
    /**
     * Width, height dimension contraints on the  diagram.
     * In inches.
     * None if any is 0.
     */
    private double[] diagramSize = new double[2];
    /**
     * DOM id of diagram container.
     */
    private static String DOM_IDENTIFIER = ".picture";
    /**
     * Prefix dom identifier (to make combination unique in page).
     */
    private String prefixDomIdentifier;
    /**
     * Whether the flow map was resized to fit.
     */
    private boolean reducedToFit = false;
    /**
     * Sizing toggle label..
     */
    private Label sizingLabel;

    protected static String WICKET_ID = "diagram";


    public AbstractResizableDiagramPanel(
            String id,
            Set<Long> expansions,
            String prefixDomIdentifier ) {
        super( id, null, expansions );
        this.prefixDomIdentifier = prefixDomIdentifier;
    }

    public AbstractResizableDiagramPanel(
            String id,
            Set<Long> expansions,
            String prefixDomIdentifier,
            double[] diagramSize ) {
        super( id, null, expansions );
        this.prefixDomIdentifier = prefixDomIdentifier;
        this.diagramSize = diagramSize;
    }


    public String getDomIdentifier() {
        return ( prefixDomIdentifier == null ? "" : prefixDomIdentifier + " " )
                + DOM_IDENTIFIER;
    }

    public double[] getDiagramSize() {
        return diagramSize;
    }

    protected void init() {
        addDiagramSizing();
        addDiagramPanel();
    }


    private void addDiagramSizing() {
        sizingLabel = new Label(
                "fit",
                new Model<String>( reducedToFit ? "Full size" : "Reduce to fit" ) );
        sizingLabel.setOutputMarkupId( true );
        sizingLabel.add( new AbstractDiagramAjaxBehavior( DOM_IDENTIFIER, reducedToFit ) {
                @Override
                protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                if ( !reducedToFit ) {
                    String swidth = requestCycle.getRequest().getQueryParameters().getParameterValue( "width" ).toString();
                    String sheight = requestCycle.getRequest().getQueryParameters().getParameterValue( "height" ).toString();
                    diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    diagramSize = new double[2];
                }
                reducedToFit = !reducedToFit;
                addDiagramPanel();
                target.add( getDiagramPanel() );
                addDiagramSizing();
                target.add( sizingLabel );
            }
        } );
        addOrReplace( sizingLabel );
    }

    /**
     * Add diagram panel.
     */
    abstract protected void addDiagramPanel();

    /**
     * Get diagram panel.
     *
     * @return an abstract diagram panel
     */
    abstract protected AbstractDiagramPanel getDiagramPanel();
}
