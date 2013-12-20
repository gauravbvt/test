package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Profile panel for an actual, non-placeholder organization.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/16/12
 * Time: 3:20 PM
 */
public class OrganizationProfile extends AbstractCommandablePanel {

    /**
     * Parent link.
     */
    private ModelObjectLink parentLink;
    /**
     * Actual parent name field.
     */
    private TextField actualParentField;
    /**
     * Location name field.
     */
    private TextField locationField;
    /**
     * Mission field.
     */
    private TextArea missionField;

//    private WebMarkupContainer mediaNotDeployedContainer;
    private WebMarkupContainer locationContainer;


    public OrganizationProfile( String id, IModel<? extends Identifiable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        addMissionField();
        addParentLink();
        addParentField();
        addLocationField();
        addLocationLink();
        addContactInfoPanel();
//        addMediaNotDeployedPanel();
        adjustFields();
    }

    private void addMissionField() {
        missionField = new TextArea<String>( "mission",
                new PropertyModel<String>( this, "mission" ) );
        missionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getOrganization(), "mission" ) );
            }
        } );
        addInputHint( missionField, "A brief mission statement" );
        add( missionField );
    }


    private void addParentLink() {
        parentLink = new ModelObjectLink(
                "org-link",
                new PropertyModel<Organization>( getOrganization(), "parent" ),
                new Model<String>( "Parent" ) );
        addOrReplace( parentLink );
    }

    private void addParentField() {
        // If organization is actual, parent must be actual
        actualParentField = new AutoCompleteTextField<String>(
                "actualParent",
                new PropertyModel<String>( this, "parentOrganization" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                final List<String> parentChoices = findCandidateParentsForActual();
                List<String> candidates = new ArrayList<String>();
                for ( String choice : parentChoices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        actualParentField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addParentLink();
                target.add( parentLink );
                target.add( actualParentField );
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "parent" ) );
            }
        } );
        actualParentField.setVisible( getOrganization().isActual() );
        addInputHint( actualParentField, "The name of an actual organization" );
        add( actualParentField );
        // If organization is type, parent can be either actual or type.
        EntityReferencePanel<Organization> parentField = new EntityReferencePanel<Organization>(
                "parent",
                new Model<Organization>( getOrganization() ),
                findCandidateParentsForType(),
                "parent",
                Organization.class );
        add( parentField );
        parentField.setVisible( getOrganization().isType() );
    }

    private void addLocationField() {
        locationContainer = new WebMarkupContainer( "locationContainer" );
        locationContainer.setOutputMarkupId( true );
        locationContainer.setVisible( getOrganization().isActual() );
        add( locationContainer );
        final List<String> locationChoices = getQueryService().findAllEntityNames(
                Place.class,
                ModelEntity.Kind.Actual );
        locationField = new AutoCompleteTextField<String>(
                "location",
                new PropertyModel<String>( this, "locationName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : locationChoices ) {
                    if ( getOrganization().isType() ) {
                        if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                    } else if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        locationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "location" ) );
            }
        } );
        addInputHint( locationField, "The name of an actual place" );
        locationContainer.add( locationField );
    }

    private void addLocationLink() {
        ModelObjectLink localeLink =  new ModelObjectLink( "loc-link",
                new PropertyModel<Organization>( getOrganization(), "location" ),
                new Model<String>( "Location" ) );
        locationContainer.addOrReplace( localeLink );
    }

    private void addContactInfoPanel() {
        WebMarkupContainer contactContainer = new WebMarkupContainer( "contactContainer" );
        contactContainer.setVisible( getOrganization().isActual() );
        add( contactContainer );
        Organization organization = getOrganization();
        contactContainer.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( organization ),
                false,
                true ) );
    }

/*
    private void addMediaNotDeployedPanel() {
        mediaNotDeployedContainer = new WebMarkupContainer( "mediaNotDeployedContainer" );
        mediaNotDeployedContainer.setVisible( getOrganization().isActual() );
        mediaNotDeployedContainer.setOutputMarkupId( true );
        addOrReplace( mediaNotDeployedContainer );
        ListView<String> mediaNotDeployedList = new ListView<String>(
                "mediaNotDeployed",
                getMediaNotDeployedNames() ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                addNotDeployedMediumCell( item );
                addNotDeployedDeleteMediumCell( item );
            }
        };
        mediaNotDeployedContainer.add( mediaNotDeployedList );
    }

    private void addNotDeployedMediumCell( ListItem<String> item ) {
        String name = item.getModelObject();
        TransmissionMedium medium = name.isEmpty()
                ? TransmissionMedium.getUNKNOWN()
                : getQueryService().safeFindOrCreateType( TransmissionMedium.class, name );
        Component mediumLink = medium.isUnknown()
                ? new Label( "notDeployedMediumLink", "" )
                : new ModelObjectLink(
                "notDeployedMediumLink",
                new Model<ModelEntity>( medium ),
                new Model<String>( medium.getName() ) );
        mediumLink.setVisible( !medium.isUnknown() );
        item.add( mediumLink );
        DropDownChoice<String> mediumChoice = new DropDownChoice<String>(
                "mediumChoice",
                new PropertyModel<String>( this, "newNotDeployedMediumName" ),
                getMediaChoices()
        );
        mediumChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addMediaNotDeployedPanel();
                target.add( mediaNotDeployedContainer );
                update( target, new Change( Change.Type.Updated, getOrganization(), "mediaNotDeployed" ) );
            }
        } );
        mediumChoice.setVisible( medium.isUnknown() );
        item.add( mediumChoice );
    }

    private void addNotDeployedDeleteMediumCell( ListItem<String> item ) {
        String name = item.getModelObject();
        final TransmissionMedium medium = getQueryService().safeFindOrCreateType(
                TransmissionMedium.class,
                name,
                null );
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "deleteNotDeployedMedium",
                "Remove medium from list?" ) {
            public void onClick( AjaxRequestTarget target ) {
                doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "mediaNotDeployed",
                        medium,
                        UpdateObject.Action.Remove ) );
                addMediaNotDeployedPanel();
                target.add( mediaNotDeployedContainer );
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getOrganization(),
                                "mediaNotDeployed"
                        ) );
            }
        };
        makeVisible( deleteLink, isLockedByUser( getOrganization() ) && !name.isEmpty() );
        item.add( deleteLink );
    }

    public String getNewNotDeployedMediumName() {
        return null;
    }

    public void setNewNotDeployedMediumName( String name ) {
        if ( name != null && !name.isEmpty() ) {
            TransmissionMedium medium = getQueryService().safeFindOrCreateType(
                    TransmissionMedium.class,
                    name,
                    null );
            doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                    "mediaNotDeployed",
                    medium,
                    UpdateObject.Action.Add ) );
        }
    }


    private List<String> getMediaNotDeployedNames() {
        List<String> media = new ArrayList<String>();
        for ( TransmissionMedium medium : getOrganization().getMediaNotDeployed() ) {
            media.add( medium.getName() );
        }
        Collections.sort( media );
        media.add( "" );
        return media;
    }

    private List<String> getMediaChoices() {
        List<String> choices = new ArrayList<String>();
        final Place planLocale = getPlanLocale();
        for ( final TransmissionMedium medium : getQueryService().list( TransmissionMedium.class ) ) {
            if ( !medium.isUnknown() ) {
                boolean subsumed = CollectionUtils.exists(
                        getOrganization().getMediaNotDeployed(),
                        new Predicate() {
                            @Override
                            public boolean evaluate( Object object ) {
                                return medium.narrowsOrEquals( (TransmissionMedium) object, planLocale );
                            }
                        }
                );
                if ( !subsumed ) {
                    choices.add( medium.getName() );
                }
            }
        }
        Collections.sort( choices );
        choices.add( "New medium" );
        return choices;
    }

*/
private void adjustFields() {
        missionField.setEnabled( isLockedByUser( getOrganization() ) );
        actualParentField.setEnabled( isLockedByUser( getOrganization() ) );
        locationField.setEnabled( isLockedByUser( getOrganization() ) );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParentsForActual() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        if ( organization.isActual() ) {
            List<Organization> ancestors = organization.ancestors();
            List<Organization> allOrganizations = new ArrayList<Organization>(
                    getQueryService().listActualEntities( Organization.class ) );
            allOrganizations.remove( Organization.UNKNOWN );
            allOrganizations.remove( organization );
            Collection<Organization> candidates = CollectionUtils.subtract( allOrganizations, ancestors );
            for ( Organization candidate : candidates ) {
                if ( !candidate.ancestors().contains( organization )
                        && ( !candidate.isPlaceHolder() || organization.isPlaceHolder() ) ) // a non-placeholder can not have a placeholder parent
                    candidateNames.add( candidate.getName() );
            }
            if ( organization.getParent() != null )
                candidateNames.add( organization.getParent().getName() );
            Collections.sort( candidateNames );
        }
        return candidateNames;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParentsForType() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        if ( getOrganization().isType() ) {
            List<Organization> allOrganizations =
                    new ArrayList<Organization>(
                            getQueryService().listActualEntities( Organization.class ) );
            allOrganizations.remove( Organization.UNKNOWN );
            allOrganizations.addAll( getQueryService().listTypeEntities( Organization.class ) );
            allOrganizations.removeAll( organization.getAllTypes() );
            allOrganizations.remove( organization );
            candidateNames = (List<String>) CollectionUtils.collect(
                    allOrganizations,
                    TransformerUtils.invokerTransformer( "getName" ) );
            Collections.sort( candidateNames );
        }
        return candidateNames;
    }

    /**
     * Set organization's parent from name if not null ir empty.
     *
     * @param name a String
     */
    public void setParentOrganization( String name ) {
        Organization oldOrg = getOrganization().getParent();
        String oldName = oldOrg == null ? "" : oldOrg.getName();
        Organization newOrg = null;
        if ( name == null || name.trim().isEmpty() )
            newOrg = null;
        else {
            if ( oldOrg == null || !isSame( name, oldName ) ) {
                newOrg = doSafeFindOrCreateActual( Organization.class, name );
                if ( newOrg.ancestors().contains( getOrganization() ) ) {
                    newOrg = oldOrg;
                    getCommander().cleanup( Organization.class, name );
                }
            }
        }
        doCommand( new UpdatePlanObject( getUser().getUsername(), getOrganization(), "parent", newOrg ) );
        getCommander().cleanup( Organization.class, oldName );
    }

    /**
     * Get organization's parent's name.
     *
     * @return a String
     */
    public String getParentOrganization() {
        Organization parent = ( getOrganization() ).getParent();
        return parent == null ? "" : parent.getName();
    }

    /**
     * Set organization's location from name, if not null or empty.
     *
     * @param name a String
     */
    public void setLocationName( String name ) {
        Organization org = getOrganization();
        Place oldPlace = org.getLocation();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = doSafeFindOrCreateActual( Place.class, name );
        }
        doCommand( new UpdatePlanObject( getUser().getUsername(), org, "location", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Get organization's location's name.
     *
     * @return a String
     */
    public String getLocationName() {
        Place location = ( getOrganization() ).getLocation();
        return location == null ? "" : location.getName();
    }

    /**
     * Get edited organization.
     *
     * @return an organization
     */
    public Organization getOrganization() {
        return (Organization) getModel().getObject();
    }

    /**
     * Get the model object's mission
     *
     * @return a string
     */
    public String getMission() {
        return getOrganization().getMission();
    }

    /**
     * Set the organization's mission.
     *
     * @param val a string
     */
    public void setMission( String val ) {
        if ( val != null )
            doCommand(
                    new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                            "mission",
                            val,
                            UpdateObject.Action.Set ) );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() ) {
            String property = change.getProperty();
            if ( property.equals( "parent" ) ) {
                addParentLink();
                target.add( parentLink );
                super.updateWith(
                        target,
                        new Change( Change.Type.Updated, getOrganization(), "types" ),
                        updated );
            }
            if ( property.equals( "location" ) ) {
                addLocationLink();
                target.add( locationContainer );
            }
        }
        super.updateWith( target, change, updated );
    }


}
