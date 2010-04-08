package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.model.Hierarchical;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.HierarchyDiagramPanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 2:29:36 PM
 */
public class OrgChartPanel extends AbstractResizableDiagramPanel {

     /**
      * Entity network diagram panel
      */
     private HierarchyDiagramPanel hierarchyDiagramPanel;
    /**
     * Model of a hierarchical object.
     */
    private IModel<Hierarchical> hierarchicalModel;

    public OrgChartPanel(
            String id,
            IModel<Hierarchical> hierarchicalModel,
            Set<Long> expansions,
            String prefixDomIdentifier
    ) {
        super( id,  expansions, prefixDomIdentifier );
        this.hierarchicalModel = hierarchicalModel;
        init();
    }
    /** {@inheritDoc} */
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            hierarchyDiagramPanel = new HierarchyDiagramPanel(
                    "diagram",
                    hierarchicalModel,
                    null,
                    getDomIdentifier()
            );
        } else {
            hierarchyDiagramPanel = new HierarchyDiagramPanel(
                    "diagram",
                    hierarchicalModel,
                    getDiagramSize(),
                    getDomIdentifier()
            );
        }
        hierarchyDiagramPanel.setOutputMarkupId( true );
        addOrReplace( hierarchyDiagramPanel );
    }

    /** {@inheritDoc} */
    protected AbstractDiagramPanel getDiagramPanel() {
        return hierarchyDiagramPanel;
    }

}
