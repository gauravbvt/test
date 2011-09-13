package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.engine.analysis.graph.EntityRelationship;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.SegmentObject;
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

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
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
                (Class<T>) getEntity().getClass(),
                null,
                getExpansions(),
                selectedEntity,
                selectedEntityRel
        );
        entityFlowPanel.setOutputMarkupId( true );
        addOrReplace( entityFlowPanel );
    }

/*    public List<EntityRelationship<T>> getEntityRelationships() {
        boolean cartesianProduct = false;
        List<EntityRelationship<T>> entityRels = new ArrayList<EntityRelationship<T>>();
        if ( selectedEntityRel != null ) {
            entityRels.add( selectedEntityRel );
        } else {
            List<T> entityDomain = getEntityDomain();
            List<T> entities = new ArrayList<T>();
            if ( selectedEntity != null ) {
                entities.add( selectedEntity );
            } else {
                entities.addAll( entityDomain );
                cartesianProduct = true;
            }
            for ( T entity : entities ) {
                for ( T other : entityDomain ) {
                    if ( entity != other ) {
                        EntityRelationship<T> sendRel =
                                getQueryService().findEntityRelationship( entity, other );
                        if ( sendRel != null ) entityRels.add( sendRel );
                        if ( !cartesianProduct ) {
                            EntityRelationship<T> receiveRel =
                                    getQueryService().findEntityRelationship( other, entity );
                            if ( receiveRel != null ) entityRels.add( receiveRel );
                        }
                    }
                }
            }
        }
        return entityRels;
    }*/

    /*   @SuppressWarnings( "unchecked" )
        public List<T> getEntityDomain() {
            return (List<T>) getQueryService().listActualEntities( getEntity().getClass() );
        }
    */

    private T getEntity() {
        return entityModel.getObject();
    }



    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            if ( change.isForInstanceOf( SegmentObject.class ) ) {
                // change.setType( Change.Type.Expanded );
                super.changed( change );
            } else if ( change.isForInstanceOf( ModelEntity.class ) ) {
                if ( change.isForProperty( "network" )
                        && change.getId() != getEntity().getId() ) {
                    change.setType( Change.Type.Expanded );
                    super.changed( change );
                } else {
                    super.changed( change );
                }
            } else if ( change.isForInstanceOf( EntityRelationship.class ) ) {
                selectedEntityRel = (EntityRelationship<T>) change.getSubject( getQueryService() );
                // selectedEntity = null;
            } else {
                selectedEntityRel = null;
                // selectedEntity = null;
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
                    || change.isForInstanceOf( SegmentObject.class ) ) {
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
