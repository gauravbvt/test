package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;

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
    private boolean resizedToFit = false;


    public AbstractResizableDiagramPanel(
            String id,
            Set<Long> expansions,
            String prefixDomIdentifier ) {
        super( id, null, expansions );
        this.prefixDomIdentifier = prefixDomIdentifier;
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
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + getDomIdentifier() + "').width()+'"
                        + "&height='+$('" + getDomIdentifier() + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                if ( !resizedToFit ) {
                    diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / DPI;
                    diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / DPI;
                } else {
                    diagramSize = new double[2];
                }
                resizedToFit = !resizedToFit;
                addDiagramPanel();
                target.addComponent( getDiagramPanel() );
            }
        } );
        add( reduceToFit );
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
