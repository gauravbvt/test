package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.EntitiesNetworkDiagramPanel;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Set;

/**
 * Entities network panel.
 * Shows relationships between a set of entities of the same type.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2010
 * Time: 4:26:14 PM
 */
public class EntitiesNetworkPanel<T extends ModelEntity> extends AbstractResizableDiagramPanel {


    /**
     * Entities network diagram panel.
     */
    private EntitiesNetworkDiagramPanel entitiesNetworkDiagramPanel;
    /**
     * Organizatons to show networked.
     */
    private IModel<List<T>> entitiesModel;
    /**
     * Selected entity relationship.
     */
    private EntityRelationship<T> selectedEntityRel;

    public EntitiesNetworkPanel(
            String id,
            IModel<List<T>> entitiesModel,
            EntityRelationship<T> selectedEntityRel,
            Set<Long> expansions,
            String prefixDomIdentifier) {
        super( id, expansions, prefixDomIdentifier );
        this.entitiesModel = entitiesModel;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }

    /**
     * {@inheritDoc}
     */
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            entitiesNetworkDiagramPanel = new EntitiesNetworkDiagramPanel<T>(
                    "diagram",
                    entitiesModel,
                    selectedEntityRel,
                    null,
                    getDomIdentifier()
            );
        } else {
            entitiesNetworkDiagramPanel = new EntitiesNetworkDiagramPanel<T>(
                    "diagram",
                    entitiesModel,
                    selectedEntityRel,
                    getDiagramSize(),
                    getDomIdentifier()
            );
        }
        entitiesNetworkDiagramPanel.setOutputMarkupId( true );
        addOrReplace( entitiesNetworkDiagramPanel );
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractDiagramPanel getDiagramPanel() {
        return entitiesNetworkDiagramPanel;
    }
}
