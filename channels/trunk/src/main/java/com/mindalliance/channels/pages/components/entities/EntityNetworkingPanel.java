package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.analysis.graph.EntityRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.FilterableEntityFlowsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.Set;

/**
 * Organization network panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 3:33:41 PM
 */
public class EntityNetworkingPanel<T extends ModelEntity> extends AbstractUpdatablePanel {

    private IModel<T> entityModel;
    /**
     * CSS identifier for dom element showing scrollable diagram.
     */
    private String prefixDomIdentifier;
    private T selectedEntity;
    private EntityRelationship<T> selectedEntityRel;
    private EntityNetworkPanel<T> entityNetworkPanel;
    private FilterableEntityFlowsPanel<T> entityFlowPanel;

    public EntityNetworkingPanel(
            String id,
            IModel<T> model,
            Set<Long> expansions,
            String prefixDomIdentifier ) {
        super( id, model, expansions );
        entityModel = model;
        this.prefixDomIdentifier = prefixDomIdentifier;
        init();
    }

    private void init() {
        selectedEntity = entityModel.getObject();
        addEntityNetworkPanel();
        addEntityFlowsPanel();
    }

    private void addEntityNetworkPanel() {
        entityNetworkPanel = new EntityNetworkPanel<T>(
                "entity-network",
                entityModel,
                selectedEntityRel,
                getExpansions(),
                prefixDomIdentifier
        );
        entityNetworkPanel.setOutputMarkupId( true );
        addOrReplace( entityNetworkPanel );
    }

    private void addEntityFlowsPanel() {
        entityFlowPanel = new FilterableEntityFlowsPanel<T>(
                "entity-flows",
                getEntityDomain(),
                getExpansions(),
                selectedEntity,
                selectedEntityRel
        );
        entityFlowPanel.setOutputMarkupId( true );
        addOrReplace( entityFlowPanel );
    }

    @SuppressWarnings( "unchecked" )
    private List<T> getEntityDomain() {
        return (List<T>) getQueryService().listActualEntities( getEntity().getClass() );
    }

    private T getEntity() {
        return entityModel.getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            if ( change.getSubject() instanceof ModelEntity ) {
                change.setType( Change.Type.Expanded );
                super.changed( change );
            } else if ( change.getSubject() instanceof EntityRelationship ) {
                selectedEntityRel = (EntityRelationship<T>) change.getSubject();
                selectedEntity = null;
            } else {
                selectedEntityRel = null;
                selectedEntity = null;
            }
        } else {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() ) {
            if ( change.getSubject() instanceof ModelEntity ) {
                super.updateWith( target, change, updated );
            } else {
                addEntityNetworkPanel();
                addEntityFlowsPanel();
                target.addComponent( entityNetworkPanel );
                target.addComponent( entityFlowPanel );
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }

}
