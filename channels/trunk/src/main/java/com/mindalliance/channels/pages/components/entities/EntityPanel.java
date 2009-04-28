package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.LockManager;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.menus.EntityActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.EntityShowMenuPanel;
import com.mindalliance.channels.pages.Project;
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
    /**
     * Details aspect.
     */
    public static final String DETAILS = "details";
    /**
     * Flows aspect.
     */
    public static final String FLOWS = "flows";
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
    /**
     * Banner.
     */
    private WebMarkupContainer banner;
    /**
     * Entity name plus aspect.
     */
    private Label entityNameLabel;
    /**
     * Entity actions menu.
     */
    private Component entityActionsMenu;
    /**
     * Entity aspect panel (or label).
     */
    private Component entityAspect;
    /**
     * Name of aspect shown.
     */
    private String aspectShown = DETAILS;


    public EntityPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        this( id, model, expansions, DETAILS );
    }

    public EntityPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions );
        aspectShown = aspect;
        init();
    }


    public String getAspectShown() {
        return aspectShown;
    }

    private void init() {
        banner = new WebMarkupContainer( "banner" );
        banner.setOutputMarkupId( true );
        add( banner );
        entityNameLabel = new Label( "header-title", new PropertyModel<String>( this, "entityName" ) );
        entityNameLabel.setOutputMarkupId( true );
        banner.add( entityNameLabel );
        AjaxFallbackLink closeLink = new AjaxFallbackLink( "close" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, getEntity() );
                update( target, change );
            }
        };
        banner.add( closeLink );
        EntityShowMenuPanel entityShowMenu = new EntityShowMenuPanel(
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
        annotateEntityName();
    }

    private void annotateEntityName() {
        Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        String summary = analyst.getIssuesSummary(
                getEntity(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
        boolean hasIssues = analyst.hasIssues( getEntity(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !summary.isEmpty() ) {
            entityNameLabel.add( new AttributeModifier(
                    "class", true, new Model<String>( "error" ) ) ); // NON-NLS
            entityNameLabel.add( new AttributeModifier(
                    "title", true, new Model<String>( summary ) ) );  // NON-NLS
        } else {
            if ( hasIssues ) {
                // All waived issues
                entityNameLabel.add(
                        new AttributeModifier( "class", true, new Model<String>( "waived" ) ) );
                entityNameLabel.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            } else {
                entityNameLabel.add( new AttributeModifier(
                        "class", true, new Model<String>( "no-error" ) ) ); // NON-NLS
                entityNameLabel.add( new AttributeModifier(
                        "title", true, new Model<String>( "No known issue" ) ) );  // NON-NLS
            }
        }
    }


    private void showEntityAspect() {
        if ( aspectShown.equals( DETAILS ) ) {
            entityAspect = getEntityDetailsPanel();
        } else if ( aspectShown.equals( NETWORK ) ) {
            entityAspect = getEntityNetworkPanel();
        } else if ( aspectShown.equals( MAP ) ) {
            entityAspect = getEntityMapPanel();
        } else if ( aspectShown.equals( FLOWS ) ) {
            entityAspect = getEntityFlowsPanel();
        } else if ( aspectShown.equals( ISSUES ) ) {
            entityAspect = getEntityIssuesPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspectShown );
        }
        entityAspect.setOutputMarkupId( true );
        addOrReplace( entityAspect );
    }

    private Component getEntityDetailsPanel() {
        if ( getEntity() instanceof Organization ) {
            return new OrganizationDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "entity" ),
                    getExpansions() );
        } else if ( getEntity() instanceof Actor ) {
            return new ActorDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "entity" ),
                    getExpansions() );
        } else {
            return new EntityDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelObject>( this, "entity" ),
                    getExpansions() );
        }
    }

    private Component getEntityNetworkPanel() {
        if ( getEntity() instanceof Actor ) {
            return new EntityNetworkPanel<Actor>(
                    "aspect",
                    new PropertyModel<Actor>( this, "entity" ),
                    getExpansions(),
                    null);
        } else if ( getEntity() instanceof Role ) {
            return new EntityNetworkPanel<Role>(
                    "aspect",
                    new PropertyModel<Role>( this, "entity" ),
                    getExpansions(),
                    null );
        } else if ( getEntity() instanceof Organization ) {
            return new EntityNetworkPanel<Organization>(
                    "aspect",
                    new PropertyModel<Organization>( this, "entity" ),
                    getExpansions(),
                    null );
        } else {
            return new Label( "aspect", "Network is under construction" );
        }
    }

    private Component getEntityMapPanel() {
        return new Label( "aspect", "Map is under construction" );
    }

    private Component getEntityFlowsPanel() {
        return new EntityFlowsPanel( "aspect",
                new PropertyModel<ModelObject>( this, "entity" ),
                getExpansions() );
    }

    private Component getEntityIssuesPanel() {
        return new EntityIssuesPanel(
                "aspect",
                new PropertyModel<ModelObject>( this, "entity" ),
                getExpansions() );
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

    /**
     * Get entity name plus aspect.
     *
     * @return a string
     */
    public String getEntityName() {
        return getEntity().getName() + " " + getAspectShown();
    }

    /**
     * Get entity's class.
     *
     * @return a class
     */
    public String getEntityClass() {
        return getEntity().getClass().getSimpleName().toLowerCase();
    }

    /**
     * Get the entity that's viewed.
     *
     * @return a model object
     */
    public ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    /**
     * Change aspect shown.
     *
     * @param target an ajax request target
     * @param aspect the name of the aspect
     */
    public void setAspectShown( AjaxRequestTarget target, String aspect ) {
        aspectShown = aspect;
        showEntityAspect();
        target.addComponent( entityNameLabel );
        target.addComponent( entityAspect );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        target.addComponent( entityActionsMenu );
        /*if ( change.getType() == Change.Type.Updated ) {
            target.addComponent( banner );
        }*/
        if ( change.getSubject() instanceof UserIssue ) {
            setAspectShown( target, "issues" );
        }
        adjustComponents();
        target.addComponent( banner );
        super.updateWith( target, change );
    }
}
