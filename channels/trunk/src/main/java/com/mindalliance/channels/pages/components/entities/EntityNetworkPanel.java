package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.components.AbstractResizableDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.AbstractDiagramPanel;
import com.mindalliance.channels.pages.components.diagrams.EntityNetworkDiagramPanel;
import org.apache.wicket.model.IModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2010
 * Time: 4:43:10 PM
 */
public class EntityNetworkPanel<T extends ModelEntity> extends AbstractResizableDiagramPanel {
    /**
     * Entities network diagram panel.
     */
    private EntityNetworkDiagramPanel entityNetworkDiagramPanel;
    /**
     * Organizatons to show networked.
     */
    private IModel<T> entityModel;
    /**
     * Selected entity relationship.
     */
    private EntityRelationship<T> selectedEntityRel;

    public EntityNetworkPanel(
            String id,
            IModel<T> entityModel,
            EntityRelationship<T> selectedEntityRel,
            Set<Long> expansions,
            String prefixDomIdentifier) {
        super( id, expansions, prefixDomIdentifier );
        this.entityModel = entityModel;
        this.selectedEntityRel = selectedEntityRel;
        init();
    }

    /**
     * {@inheritDoc}
     */
    protected void addDiagramPanel() {
        if ( getDiagramSize()[0] <= 0.0 || getDiagramSize()[1] <= 0.0 ) {
            entityNetworkDiagramPanel = new EntityNetworkDiagramPanel<T>(
                    "diagram",
                    entityModel,
                    selectedEntityRel,
                    null,
                    getDomIdentifier()
            );
        } else {
            entityNetworkDiagramPanel = new EntityNetworkDiagramPanel<T>(
                    "diagram",
                    entityModel,
                    null,
                    getDiagramSize(),
                    getDomIdentifier()
            );
        }
        entityNetworkDiagramPanel.setOutputMarkupId( true );
        addOrReplace( entityNetworkDiagramPanel );
    }

    /**
     * {@inheritDoc}
     */
    protected AbstractDiagramPanel getDiagramPanel() {
        return entityNetworkDiagramPanel;
    }
}
