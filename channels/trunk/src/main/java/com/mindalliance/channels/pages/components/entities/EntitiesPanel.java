package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.engine.command.Change;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.FilterableEntityFlowsPanel;
import com.mindalliance.channels.pages.components.plan.EntitiesNetworkPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;
import java.util.Set;

/**
 * Entities panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 7, 2010
 * Time: 2:07:25 PM
 */
public class EntitiesPanel<T extends ModelEntity> extends AbstractUpdatablePanel {

    private Class<T> entityClass;
    private Segment segment;
    private String prefixDomIdentifier;
    private EntityRelationship<T> selectedEntityRel;
    private EntitiesNetworkPanel<T> entitiesNetworkPanel;
    private FilterableEntityFlowsPanel<T> entityFlowPanel;

    public EntitiesPanel(
            String id,
            Class<T> entityClass,
            Segment segment,
            Set<Long> expansions,
            String prefixDomIdentifier ) {
        super( id, null, expansions );
        this.entityClass = entityClass;
        this.segment = segment;
        this.prefixDomIdentifier = prefixDomIdentifier;
        init();
    }

    private void init() {
        addEntitiesNetworkPanel();
        addEntityFlowsPanel();
    }

    private void addEntitiesNetworkPanel() {
        entitiesNetworkPanel = new EntitiesNetworkPanel<T>(
                "entities-network",
                entityClass,
                segment,
                selectedEntityRel,
                getExpansions(),
                prefixDomIdentifier
        );
        entitiesNetworkPanel.setOutputMarkupId( true );
        addOrReplace( entitiesNetworkPanel );
    }

    private void addEntityFlowsPanel() {
        entityFlowPanel = new FilterableEntityFlowsPanel<T>(
                "entity-flows",
                entityClass,
                segment,
                getExpansions(),
                null,
                selectedEntityRel
        );
        entityFlowPanel.setOutputMarkupId( true );
        addOrReplace( entityFlowPanel );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            if ( change.isForInstanceOf( ModelEntity.class )
                    || change.isForInstanceOf( SegmentObject.class )
                    || change.isForInstanceOf( Segment.class ) ) {
                super.changed( change );
            } else if ( change.isForInstanceOf( EntityRelationship.class ) ) {
                selectedEntityRel = (EntityRelationship<T>) change.getSubject( getQueryService() );
            } else {
                selectedEntityRel = null;
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
            if ( change.isForInstanceOf( ModelEntity.class )
                    || change.isForInstanceOf( SegmentObject.class )
                    || change.isForInstanceOf( Segment.class ) ) {
                super.updateWith( target, change, updated );
            } else {
                addEntitiesNetworkPanel();
                addEntityFlowsPanel();
                target.addComponent( entitiesNetworkPanel );
                target.addComponent( entityFlowPanel );
            }
        } else {
            super.updateWith( target, change, updated );
        }
    }

}
