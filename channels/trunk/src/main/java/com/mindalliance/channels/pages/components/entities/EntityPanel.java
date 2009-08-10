package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.components.AbstractMultiAspectPanel;
import com.mindalliance.channels.pages.components.entities.menus.EntityActionsMenuPanel;
import com.mindalliance.channels.pages.components.entities.menus.EntityShowMenuPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
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
public class EntityPanel extends AbstractMultiAspectPanel {
    /**
     * Network aspect.
     */
    public static final String NETWORK = "network";
    /**
     * Map aspect.
     */
    public static final String MAP = "map";
    /**
     * Issues aspect.
     */
    public static final String ISSUES = "issues";

    public EntityPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        this( id, model, expansions, DETAILS );
    }

    public EntityPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }


    /**
     * {@inheritDoc}
     */
    protected String getDefaultAspect() {
        return DETAILS;
    }

    /**
     * {@inheritDoc}
     */
    protected String getCssClass() {
        return "entity";
    }

    /**
     * {@inheritDoc}
     */
    protected MenuPanel makeShowMenu( String menuId ) {
        EntityShowMenuPanel showMenu = new EntityShowMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "object" ) );
        showMenu.setEntityPanel( this );
        return showMenu;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean objectNeedsLocking() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected MenuPanel makeActionMenu( String menuId ) {
        return new EntityActionsMenuPanel(
                menuId,
                new PropertyModel<ModelObject>( this, "object" ) );
    }


    /**
     * {@inheritDoc}
     */
    protected Component makeAspectPanel( String aspect ) {
        if ( aspect.equals( DETAILS ) ) {
            return getEntityDetailsPanel();
        } else if ( aspect.equals( NETWORK ) ) {
            return getEntityNetworkPanel();
        } else if ( aspect.equals( MAP ) ) {
            return getEntityMapPanel();
        } else if ( aspect.equals( ISSUES ) ) {
            return getEntityIssuesPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected int getMaxTitleNameLength() {
        return 30;
    }

    private Component getEntityDetailsPanel() {
        if ( getObject() instanceof Organization ) {
            return new OrganizationDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Actor ) {
            return new ActorDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Role ) {
            return new RoleDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Event ) {
            return new EventDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Place ) {
            return new PlaceDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "object" ),
                    getExpansions() );
        } else {
            return new EntityDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "object" ),
                    getExpansions() );
        }
    }

    private Component getEntityNetworkPanel() {
        if ( getObject() instanceof Actor ) {
            return new EntityNetworkPanel<Actor>(
                    "aspect",
                    new PropertyModel<Actor>( this, "object" ),
                    getExpansions(),
                    ".entity .picture" );
        } else if ( getObject() instanceof Role ) {
            return new EntityNetworkPanel<Role>(
                    "aspect",
                    new PropertyModel<Role>( this, "object" ),
                    getExpansions(),
                    ".entity .picture" );
        } else if ( getObject() instanceof Organization ) {
            return new EntityNetworkPanel<Organization>(
                    "aspect",
                    new PropertyModel<Organization>( this, "object" ),
                    getExpansions(),
                    ".entity .picture" );
        } else {
            return new Label( "aspect", "Under construction" );
        }
    }

    private Component getEntityMapPanel() {
        return new Label( "aspect", "Under construction" );
    }

    private Component getEntityIssuesPanel() {
        return new EntityIssuesPanel(
                "aspect",
                new PropertyModel<ModelObject>( this, "object" ) );
    }


    /**
     * {@inheritDoc}
     */
    protected String getObjectClassName() {
        ModelObject entity = getEntity();
        if ( entity instanceof Actor && ( (Actor) entity ).isSystem() )
            return "system";
        else
            return getObject().getTypeName();
    }

    /**
     * Get entity being edited.
     *
     * @return a model object
     */
    protected ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }


    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        /*if ( change.getSubject() instanceof UserIssue ) {
            setAspectShown( target, "issues" );
        } else */if ( change.isUpdated() && change.getProperty().equals( "geoLocation" ) ) {
            addShowMenu();
            target.addComponent( getShowMenu() );
        }
        super.updateWith( target, change );
    }
}
