package com.mindalliance.channels.pages.components.entities.structure;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Hierarchical;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.HierarchyDiagramPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 2:29:36 PM
 */
public class HierarchyPanel extends AbstractResizableDiagramPanel implements Guidable {

    /**
     * Entity network diagram panel
     */
    private HierarchyDiagramPanel hierarchyDiagramPanel;
    /**
     * Model of a hierarchical object.
     */
    private IModel<? extends Hierarchical> hierarchicalModel;

    public HierarchyPanel(
            String id,
            IModel<? extends Hierarchical> hierarchicalModel,
            Set<Long> expansions,
            String prefixDomIdentifier
    ) {
        super( id, expansions, prefixDomIdentifier );
        this.hierarchicalModel = hierarchicalModel;
        init();
    }

    @Override
    public String getHelpSectionId() {
        Hierarchical hierarchical = hierarchicalModel.getObject();
        if ( hierarchical instanceof  ModelEntity ) {
            ModelEntity entity = ( ModelEntity )hierarchical;
            if ( entity.isType() ) {
                return "searching";
            } else {
                return "profiling";
            }
        }
        return null;
    }

    @Override
    public String getHelpTopicId() {
        Hierarchical hierarchical = hierarchicalModel.getObject();
        if ( hierarchical instanceof  ModelEntity ) {
            ModelEntity entity = ( ModelEntity )hierarchical;
            if ( entity.isType() ) {
                return "taxonomies";
            } else {
                if ( entity instanceof Actor ) {
                    return "agent-hierarchy";
                } else if ( entity instanceof Organization ) {
                    return "organization-chart";
                }
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            hierarchyDiagramPanel = new HierarchyDiagramPanel(
                    "diagram",
                    hierarchicalModel,
                    null,
                    getDomIdentifier() );
        } else {
            hierarchyDiagramPanel = new HierarchyDiagramPanel(
                    "diagram",
                    hierarchicalModel,
                    getDiagramSize(),
                    getDomIdentifier() );
        }
        hierarchyDiagramPanel.setOutputMarkupId( true );
        addOrReplace( hierarchyDiagramPanel );
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractDiagramPanel getDiagramPanel() {
        return hierarchyDiagramPanel;
    }

    @Override
    public void changed( Change change ) {
        if ( change.isSelected() && change.isForInstanceOf( ModelEntity.class ) ) {
            change.setType( Change.Type.Expanded );
        }
        super.changed( change );
    }

}
