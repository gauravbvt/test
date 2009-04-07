package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.analysis.network.EntityRelationship;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.diagrams.EntityNetworkDiagramPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
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
public class EntityNetworkPanel<T extends ModelObject> extends AbstractUpdatablePanel {

    /**
     * Default page size for external flows panel.
     */
    private static final int PAGE_SIZE = 10;

    private IModel<T> entityModel;
    private T selectedEntity;
    private EntityRelationship<T> selectedEntityRel;


    public EntityNetworkPanel(
            String id,
            IModel<T> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
        this.entityModel = model;
        init();
    }

    private void init() {
        addEntityNetworkDiagramPanel();
        addFlowsTitleLabel();
        addFlowsPanel();
    }

    private void addEntityNetworkDiagramPanel() {
        EntityNetworkDiagramPanel<T> entityRelDiagramPanel = new EntityNetworkDiagramPanel<T>(
                "diagram",
                entityModel,
                selectedEntity,
                selectedEntityRel
        );
        addOrReplace( entityRelDiagramPanel );
    }

    private void addFlowsTitleLabel() {
        Label flowsTitleLabel = new Label( "flows-title", new PropertyModel<String>( this, "flowsTitle" ) );
        flowsTitleLabel.setOutputMarkupId( true );
        add( flowsTitleLabel );
    }

    private void addFlowsPanel() {
        NetworkFlowsPanel flowsPanel = new NetworkFlowsPanel(
                "flows",
                new PropertyModel<ArrayList<Flow>>( this, "flows" ),
                PAGE_SIZE,
                getExpansions()
        );
        flowsPanel.setOutputMarkupId( true );
        addOrReplace( flowsPanel );
    }

    /**
     * Get flows title.
     *
     * @return a string
     */
    public String getFlowsTitle() {
        if ( selectedEntity != null ) {
            return "Flows between \""
                    + getEntity().getName()
                    + "\" and "
                    + selectedEntity.getName();
        } else if ( selectedEntityRel != null ) {
            T fromEntity = selectedEntityRel.getFromEntity( getDqo() );
            T toEntity = selectedEntityRel.getToEntity( getDqo() );
            if ( fromEntity == null || toEntity == null ) {
                return "*** You need to refresh ***";
            } else {
                return "Flows from \""
                        + fromEntity.getName()
                        + "\" connecting to \""
                        + toEntity.getName()
                        + "\"";
            }
        } else {
            return "All flows invoving \""
                    + getEntity().getName()
                    + "\"";
        }
    }

    /**
     * Get flows.
     *
     * @return a list of flows
     */
    public List<Flow> getFlows() {
        if ( selectedEntityRel != null ) {
            return selectedEntityRel.getFlows();
        } else if ( selectedEntity != null ) {
            List<Flow> flows = new ArrayList<Flow>();
            EntityRelationship<T> entityRel = getDqo().findEntityRelationship( getEntity(), selectedEntity );
            if ( entityRel != null ) flows.addAll( entityRel.getFlows() );
            return flows;
        } else {
            List<Flow> flows = new ArrayList<Flow>();
            for ( T other : getEntities() ) {
                if ( getEntity() != other ) {
                    EntityRelationship<T> entityRel = getDqo().findEntityRelationship( getEntity(), other );
                    if ( entityRel != null ) flows.addAll( entityRel.getFlows() );
                }
            }
            return flows;
        }
    }

    public T getEntity() {
        return entityModel.getObject();
    }

    private List<T> getEntities() {
        return (List<T>) getDqo().list( getEntity().getClass() );
    }

    public void refresh( AjaxRequestTarget target ) {
        addEntityNetworkDiagramPanel();
        addFlowsPanel();
        target.addComponent( this );
    }

    /**
     * {@inheritDoc}
     */
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            Identifiable changed = change.getSubject();
            if ( changed instanceof Project ) {
                selectedEntity = null;
                selectedEntityRel = null;
            } else if ( changed instanceof Part ) {
                selectedEntity = (T) extractEntityFromPart( (Part) changed );
                selectedEntityRel = null;
            } else if ( changed instanceof EntityRelationship ) {
                selectedEntity = null;
                selectedEntityRel = (EntityRelationship<T>) changed;
            }
            // Don't percolate change on selection of project, entity or entity relationship.
            else {
                super.changed( change );
            }
        } else {
            super.changed( change );
        }
    }

    private ModelObject extractEntityFromPart( Part part ) {
        ModelObject entity = getEntity();
        if ( entity instanceof Actor ) {
            return part.getActor();
        } else if ( entity instanceof Role ) {
            return part.getRole();
        }
        if ( entity instanceof Organization ) {
            return part.getOrganization();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isSelected() ) {
            refresh( target );
            // Don't percolate update on selection unless a part was selected.
            if ( change.getSubject() instanceof Flow ) {
                super.updateWith( target, change );
            }
        } else {
            super.updateWith( target, change );
        }
    }


}
