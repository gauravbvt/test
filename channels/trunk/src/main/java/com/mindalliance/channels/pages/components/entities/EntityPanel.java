package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.menus.EntityActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.EntityShowMenuPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * A panel showing an entity (actor, organization, role or place)
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2009
 * Time: 12:37:10 PM
 */
public class EntityPanel extends AbstractCommandablePanel {

    private WebMarkupContainer banner;
    private EntityShowMenuPanel entityShowMenu;
    private Component entityActionsMenu;
    private Component entityAspect;
    private String aspectShown = "details";


    public String getAspectShown() {
        return aspectShown;
    }

    public EntityPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        banner = new WebMarkupContainer( "banner" );
        banner.setOutputMarkupId( true );
        add( banner );
        banner.add( new Label( "header-title", new PropertyModel<String>( this, "entityName" ) ) );
        AjaxFallbackLink closeLink = new AjaxFallbackLink( "close" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, getEntity() );
                update( target, change );
            }
        };
        banner.add( closeLink );
        entityShowMenu = new EntityShowMenuPanel(
                "entityShowMenu",
                new PropertyModel<ModelObject>( this, "entity" ) );
        entityShowMenu.setEntityPanel( this );
        banner.add( entityShowMenu );
        addEntityActionMenu();
        showEntityAspect();
        adjustComponents();
    }

    private void adjustComponents() {
        banner.add( new AttributeModifier(
                "class",
                true,
                new PropertyModel<String>( this, "entityClass" ) ) );
    }

    private void showEntityAspect() {
        if ( aspectShown.equals( "details" ) ) {
            entityAspect = getEntityDetailsPanel();
        } else if ( aspectShown.equals( "network" ) ) {
            entityAspect = getEntityNetworkPanel();
        } else if ( aspectShown.equals( "map" ) ) {
            entityAspect = getEntityMapPanel();
        } else {
            entityAspect = getEntityIssuesPanel();
        }
        entityAspect.setOutputMarkupId( true );
        addOrReplace( entityAspect );
    }

    private Component getEntityDetailsPanel() {
        if (getEntity() instanceof Organization ) {
            return new OrganizationDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>(this, "entity"),
                    getExpansions());
        }
        else if (getEntity() instanceof Actor ) {
            return new ActorDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>(this, "entity"),
                    getExpansions());
        }

        else {
            return new EntityDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>(this, "entity"),
                    getExpansions());
        }
    }

    private Component getEntityNetworkPanel() {
        return new Label( "aspect", "Network is under construction" );
    }

    private Component getEntityMapPanel() {
        return new Label( "aspect", "Map is under construction" );
    }

    private Component getEntityIssuesPanel() {
        return new EntityIssuesPanel(
                "aspect",
                new PropertyModel<ModelObject>( this, "entity"),
                getExpansions());
    }

    private void addEntityActionMenu() {
        LockManager lockManager = getLockManager();
        if ( lockManager.isLockedByUser( getEntity() ) ) {
            entityActionsMenu = new EntityActionsMenuPanel(
                    "entityActionsMenu",
                    new PropertyModel<ModelObject>( this, "entity" ) );
        } else {
            String otherUser = lockManager.getLockOwner( getEntity().getId() );
            entityActionsMenu = new Label(
                    "entityActionsMenu", new Model<String>( "Edited by " + otherUser ) );
            entityActionsMenu.add(
                    new AttributeModifier( "class", true, new Model<String>( "locked" ) ) );
        }
        entityActionsMenu.setOutputMarkupId( true );
        banner.addOrReplace( entityActionsMenu );
    }


    public String getEntityName() {
        return getEntity().getName();
    }

    public String getEntityClass() {
        return getEntity().getClass().getSimpleName().toLowerCase();
    }

    public ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    public void setAspectShown( AjaxRequestTarget target, String aspect ) {
        if ( !aspectShown.equals( aspect ) ) {
            this.aspectShown = aspect;
            showEntityAspect();
            target.addComponent( entityAspect );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        target.addComponent( entityActionsMenu );
        if ( change.getType() == Change.Type.Updated ) {
            target.addComponent( banner );
        }
        super.updateWith( target, change );
    }
}
