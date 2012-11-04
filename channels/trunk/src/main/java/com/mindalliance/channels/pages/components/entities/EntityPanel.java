package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.InfoFormat;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.entities.menus.EntityActionsMenuPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A panel showing an entity (actor, organization, role or place)
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2009
 * Time: 12:37:10 PM
 */
public class EntityPanel extends AbstractFloatingMultiAspectPanel {

    /**
     * Network aspect.
     */
    public static final String NETWORK = "network";
    /**
     * Issues aspect.
     */
    public static final String ISSUES = "issues";

    /**
     * Actionable aspects.
     */
    private static final String[] ACTIONABLE_ASPECTS = {DETAILS};

    private static final String[] ASPECTS = { DETAILS, NETWORK, ISSUES };

    /**
     * DOM identifier prefix for resizeble diagrams.
     */
    private static final String PREFIX_DOM_IDENTIFIER = ".entity";

    public EntityPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        this( id, model, expansions, DETAILS );
    }

    public EntityPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }

    protected Identifiable getTabChangeDefaultSubject() {
        return getEntity();
    }


    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return "About "
                + getAboutString()
                + getEntity().getKindLabel().toLowerCase()
                + ": " + getObject().getName();
    }

    private String getAboutString() {
        ModelEntity entity = getEntity();
        if ( ModelEntity.canBeActualOrType( entity.getClass() ) ) {
            return  getEntity().isActual() ? " actual " : " type of ";
        } else {
            return "";
        }
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        super.doClose( target );
        getCommander().cleanup( getEntity().getClass(), getEntity().getName() );
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
    protected MenuPanel makeActionMenu( String menuId ) {
        return new EntityActionsMenuPanel(
                menuId,
                new PropertyModel<ModelEntity>( this, "object" ) );
    }

    /**
     * {@inheritDoc}
     */
    protected Component makeAspectPanel( String aspect, Change change ) {
        if ( aspect.equals( DETAILS ) ) {
            return getEntityDetailsPanel();
        } else if ( aspect.equals( NETWORK ) ) {
            return getEntityNetworkPanel();
        }  else if ( aspect.equals( ISSUES ) ) {
            return getEntityIssuesPanel();
        } else {
            // Should never happen
            throw new RuntimeException( "Unknown aspect " + aspect );
        }
    }

    private Component getEntityDetailsPanel() {
        if ( getObject() instanceof Organization ) {
            return new OrganizationDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Actor ) {
            return new ActorDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Role ) {
            return new RoleDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Event ) {
            return new EventDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Place ) {
            return new PlaceDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof Phase ) {
            return new PhaseDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof TransmissionMedium ) {
            return new MediumDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof InfoProduct ) {
            return new InfoProductDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else if ( getObject() instanceof InfoFormat ) {
            return new InfoFormatDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        } else {
            return new EntityDetailsPanel(
                    "aspect",
                    new PropertyModel<ModelEntity>( this, "object" ),
                    getExpansions() );
        }
    }

    private Component getEntityNetworkPanel() {
        if ( getObject() instanceof Actor ) {
            return new EntityNetworkingPanel<Actor>(
                    "aspect",
                    new PropertyModel<Actor>( this, "object" ),
                    getExpansions(),
                    PREFIX_DOM_IDENTIFIER );
        } else if ( getObject() instanceof Role ) {
            return new EntityNetworkingPanel<Role>(
                    "aspect",
                    new PropertyModel<Role>( this, "object" ),
                    getExpansions(),
                    PREFIX_DOM_IDENTIFIER );
        } else if ( getObject() instanceof Organization ) {
            return new EntityNetworkingPanel<Organization>(
                    "aspect",
                    new PropertyModel<Organization>( this, "object" ),
                    getExpansions(),
                    PREFIX_DOM_IDENTIFIER );
        } else {
            return new Label( "aspect", "Under construction" );
        }
    }


    private Component getEntityIssuesPanel() {
        return new EntityIssuesPanel(
                "aspect",
                new PropertyModel<ModelEntity>( this, "object" ) );
    }

    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected List<String> getActionableAspects() {
        return Arrays.asList( ACTIONABLE_ASPECTS );
    }

    @Override
    protected String getMapTitle() {
        return "Location of " + getEntity().getKindLabel() + " " + getEntity().getName();
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>(  );
        if ( getEntity() instanceof GeoLocatable ) {
            geoLocatables.add( (GeoLocatable)getEntity() );
        }
        return geoLocatables;
    }

    @Override
    protected PathIcon getIssuesPathIcon( String id ) {
        return null;
    }

    /**
     * Get entity being edited.
     *
     * @return a model object
     */
    protected ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        getCommander().requestLockOn( getUser().getUsername(), change.getSubject( getQueryService() ) );
        super.refresh( target, change, aspect );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isExists() && change.isForInstanceOf( Issue.class ) ) {
            setAspectShown(target, DETAILS);
        }
        super.updateWith( target, change, updated );
    }
}
