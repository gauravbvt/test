package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.HierarchyDiagramPanel;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 2:29:36 PM
 */
public class OrgChartPanel extends AbstractUpdatablePanel {

    /** Width, height dimension contraints on the plan map diagram.
      * In inches.
      * None if any is 0.
      */
     private double[] diagramSize = new double[2];
     /**
      * Entity network diagram panel
      */
     private HierarchyDiagramPanel hierarchyDiagramPanel;
    /**
     * Model of a hierarchical object.
     */
    private IModel<Hierarchical> hierarchicalModel;
    /**
     * DOM id of diagram container.
     */
    private String domIdentifier;

    public OrgChartPanel(
            String id,
            IModel<Hierarchical> hierarchicalModel,
            Set<Long> expansions,
            String domIdentifier) {
        super( id, hierarchicalModel, expansions );
        this.hierarchicalModel = hierarchicalModel;
        this.domIdentifier = domIdentifier;
        init();
    }

    private void init() {
       addDiagramSizing();
       addHierarchyDiagramPanel();
    }
    
    private void addDiagramSizing() {
        WebMarkupContainer reduceToFit = new WebMarkupContainer( "fit" );
        reduceToFit.add( new AbstractDefaultAjaxBehavior() {
            protected void onComponentTag( ComponentTag tag ) {
                super.onComponentTag( tag );
                String script = "wicketAjaxGet('"
                        + getCallbackUrl( true )
                        + "&width='+$('" + domIdentifier + "').width()+'"
                        + "&height='+$('" + domIdentifier + "').height()";
                String onclick = ( "{" + generateCallbackScript( script ) + " return false;}" )
                        .replaceAll( "&amp;", "&" );
                tag.put( "onclick", onclick );
            }

            protected void respond( AjaxRequestTarget target ) {
                RequestCycle requestCycle = RequestCycle.get();
                String swidth = requestCycle.getRequest().getParameter( "width" );
                String sheight = requestCycle.getRequest().getParameter( "height" );
                diagramSize[0] = ( Double.parseDouble( swidth ) - 20 ) / 96.0;
                diagramSize[1] = ( Double.parseDouble( sheight ) - 20 ) / 96.0;
                addHierarchyDiagramPanel();
                target.addComponent( hierarchyDiagramPanel );
            }
        } );
        add( reduceToFit );
        WebMarkupContainer fullSize = new WebMarkupContainer( "full" );
        fullSize.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                diagramSize = new double[2];
                addHierarchyDiagramPanel();
                target.addComponent( hierarchyDiagramPanel );
            }
        } );
        add( fullSize );
    }



    private void addHierarchyDiagramPanel() {
        if ( diagramSize[0] <= 0.0 || diagramSize[1] <= 0.0 ) {
            hierarchyDiagramPanel = new HierarchyDiagramPanel(
                    "diagram",
                    hierarchicalModel,
                    null,
                    domIdentifier
            );
        } else {
            hierarchyDiagramPanel = new HierarchyDiagramPanel(
                    "diagram",
                    hierarchicalModel,
                    diagramSize,
                    domIdentifier
            );
        }
        hierarchyDiagramPanel.setOutputMarkupId( true );
        addOrReplace( hierarchyDiagramPanel );
    }

}
