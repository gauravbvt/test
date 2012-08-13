package com.mindalliance.channels.pages.components.plan.requirements;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.commons.lang.WordUtils;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Assignment spec resource edit panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/4/11
 * Time: 12:24 PM
 */
public class RequiredWhoPanel extends AbstractCommandablePanel {

    /**
     * The actor property.
     */
    private static final String ACTOR_PROPERTY = "actor";                   // NON-NLS

    /**
     * The role property.
     */
    private static final String ROLE_PROPERTY = "role";                     // NON-NLS

    /**
     * The organization property.
     */
    private static final String ORG_PROPERTY = "organization";              // NON-NLS

    /**
     * The jurisdiction property.
     */
    private static final String JURISDICTION_PROPERTY = "jurisdiction";     // NON-NLS
    private static String[] EntityProps = {
            ACTOR_PROPERTY, ROLE_PROPERTY, JURISDICTION_PROPERTY, ORG_PROPERTY
    };

    private final boolean isBeneficiary;
    /**
     * Entity reference fields.
     */
    private List<EntityReferencePanel<? extends ModelEntity>> entityFields =
            new ArrayList<EntityReferencePanel<? extends ModelEntity>>();

    public RequiredWhoPanel( String id, Model<Requirement> requirementModel, boolean isBeneficiary ) {
        super( id, requirementModel );
        this.isBeneficiary = isBeneficiary;
        init();
    }

    private void init() {
        entityFields = new ArrayList<EntityReferencePanel<? extends ModelEntity>>();
        addActorField();
        addRoleField();
        addOrganizationField();
        addJurisdictionField();
        addEntityLinks();
        adjustFields();
    }

    private void adjustFields() {
        boolean lockedByUser = isLockedByUser( getRequirement() );
        for ( EntityReferencePanel entityReferencePanel : entityFields )
            entityReferencePanel.enable( lockedByUser );
    }

    private void addActorField() {
        EntityReferencePanel<Actor> field = new EntityReferencePanel<Actor>(
                ACTOR_PROPERTY,
                new PropertyModel<Requirement>( this, "requirement" ),
                getAllActorNames(),
                getEntityPath( ACTOR_PROPERTY ),
                Actor.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addRoleField() {
        EntityReferencePanel<Role> field = new EntityReferencePanel<Role>(
                ROLE_PROPERTY,
                new PropertyModel<Part>( this, "requirement" ),
                getAllRoleNames(),
                getEntityPath( ROLE_PROPERTY ),
                Role.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addOrganizationField() {
        EntityReferencePanel<Organization> field = new EntityReferencePanel<Organization>(
                ORG_PROPERTY,
                new PropertyModel<Part>( this, "requirement" ),
                getAllOrganizationNames(),
                getEntityPath( ORG_PROPERTY ),
                Organization.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addJurisdictionField() {
        EntityReferencePanel<Place> field = new EntityReferencePanel<Place>(
                JURISDICTION_PROPERTY,
                new PropertyModel<Part>( this, "requirement" ),
                getAllPlaceNames(),
                getEntityPath( JURISDICTION_PROPERTY ),
                Place.class );
        addOrReplace( field );
        entityFields.add( field );
    }

    private void addEntityLinks() {
        for ( String prop : EntityProps ) {
            addEntityLink( prop, getEntityPath( prop ) );
        }
    }

    private ModelObjectLink addEntityLink( String id, String propPath ) {
        ModelObjectLink moLink = new ModelObjectLink(
                id + "-link",
                new PropertyModel<ModelEntity>( getRequirement(), propPath ),
                new Model<String>( WordUtils.capitalize( id.equals( "actor" ) ?
                        "agent" :
                        id ) ) );
        moLink.setOutputMarkupId( true );
        addOrReplace( moLink );
        return moLink;
    }

    private String getEntityPath( String property ) {
        return ( isBeneficiary ? "beneficiarySpec" : "committerSpec" ) + "." + property;
    }


    public List<String> getAllActorNames() {
        return getQueryService().findAllEntityNames( Actor.class );
    }

    public List<String> getAllRoleNames() {
        return getQueryService().findAllEntityNames( Role.class );
    }

    public List<String> getAllOrganizationNames() {
        return getQueryService().findAllEntityNames( Organization.class );
    }

    public List<String> getAllPlaceNames() {
        return getQueryService().findAllEntityNames( Place.class );
    }

    public Requirement getRequirement() {
        return (Requirement) getModel().getObject();
    }
}
