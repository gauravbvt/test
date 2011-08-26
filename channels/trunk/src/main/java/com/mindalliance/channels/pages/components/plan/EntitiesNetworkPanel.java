package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.EntitiesNetworkDiagramPanel;

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
    private Class<T> entityClass;
    private Segment segment;
    /**
     * Selected entity relationship.
     */
    private EntityRelationship<T> selectedEntityRel;

    public EntitiesNetworkPanel(
            String id,
            Class<T> entityClass,
            Segment segment,
            EntityRelationship<T> selectedEntityRel,
            Set<Long> expansions,
            String prefixDomIdentifier) {
        super( id, expansions, prefixDomIdentifier );
        this.entityClass = entityClass;
        this.segment = segment;
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
                    entityClass,
                    segment,
                    selectedEntityRel,
                    null,
                    getDomIdentifier()
            );
        } else {
            entitiesNetworkDiagramPanel = new EntitiesNetworkDiagramPanel<T>(
                    "diagram",
                    entityClass,
                    segment,
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
