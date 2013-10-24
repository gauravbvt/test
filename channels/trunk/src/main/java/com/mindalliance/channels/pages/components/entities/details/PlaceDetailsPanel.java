package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.engine.geo.GeoService;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.entities.PlaceReferencePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Place details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 16, 2009
 * Time: 10:53:47 AM
 */
public class PlaceDetailsPanel extends EntityDetailsPanel implements Guidable{

    @SpringBean
    private GeoService geoService;
    /**
     * Place detail fields container.
     */
    private WebMarkupContainer moDetailsDiv;
    /**
     * Geolocation list container.
     */
    private WebMarkupContainer geoLocationsContainer;
    /**
     * Postal code field.
     */
    private TextField<String> postalCodeField;
    /**
     * Geoname field.
     */
    private TextField<String> geonameField;
    /**
     * Within field.
     */
    private TextField<String> withinField;
    private TextField<String> streetAddressField;
    private WebMarkupContainer streetContainer;
    private WebMarkupContainer codeContainer;
    private WebMarkupContainer geonameContainer;
    private WebMarkupContainer withinContainer;

    public PlaceDetailsPanel(
            String id,
            IModel<? extends ModelEntity> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-place";
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addPlaceholder();
        addWithinField();
        addStreetAddressField();
        addPostalCodeField();
        addGeonameField();
        geoLocationsContainer = new WebMarkupContainer( "geoLocationsContainer" );
        geoLocationsContainer.setOutputMarkupId( true );
        moDetailsDiv.add( geoLocationsContainer );
        addGeoLocationList();
        addWithinConstraint();
        addContainsConstraint();
        adjustFields();
    }

    private void addPlaceholder() {
        WebMarkupContainer placeholderContainer = new WebMarkupContainer( "placeholderContainer" );
        placeholderContainer.setVisible( getPlace().isActual() );
        moDetailsDiv.add( placeholderContainer );
        AjaxCheckBox placeholderCheckbox = new AjaxCheckBox(
                "placeholder",
                new PropertyModel<Boolean>( this, "placeholder")
        ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.add( codeContainer, streetContainer, geonameContainer, withinContainer );
            }
        };
        placeholderContainer.add( placeholderCheckbox );
    }

    private void adjustFields() {
        makeVisible( streetContainer, !isPlaceholder() );
        makeVisible( codeContainer, !isPlaceholder() );
        postalCodeField.setEnabled( isLockedByUser( getPlace() ) );
        geonameField.setEnabled( isLockedByUser( getPlace() ) );
        withinField.setEnabled( isLockedByUser( getPlace() ) );
    }

    public boolean isPlaceholder() {
        return getPlace().isActual() && getPlace().isPlaceholder();
    }

    public void setPlaceholder( boolean val ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getPlace(), "placeholder", val ) );
    }

    private void addWithinField() {
        withinContainer = new WebMarkupContainer( "withinContainer" );
        withinContainer.setOutputMarkupId( true );
        moDetailsDiv.add( withinContainer );
        withinContainer.add(
                new ModelObjectLink( "within-link",
                        new PropertyModel<Place>( getPlace(), "within" ),
                        new Model<String>( "Is within" ) ) );
        final List<String> choices = findWithinCandidates();
        withinField = new AutoCompleteTextField<String>(
                "within",
                new PropertyModel<String>( this, "withinName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( input, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        withinField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssues( withinField, getPlace(), "within" );
                target.add( withinField );
                update( target, new Change( Change.Type.Updated, getPlace(), "within" ) );
            }
        } );
        addIssues( withinField, getPlace(), "within" );
        withinField.setOutputMarkupId( true );
        addInputHint( withinField, ModelEntity.getNameHint( getEntity().getClass(), getEntity().getKind() ) );
        withinContainer.add( withinField );
        withinContainer.setVisible( getPlace().isActual() );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findWithinCandidates() {
        final Place place = getPlace();
        if ( place.isActual() ) {
            final Place locale = getPlanLocale();
            List<Place> allCandidatePlaces = (List<Place>) CollectionUtils.select(
                    getQueryService().listActualEntities( Place.class ),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return !( (Place) object ).matchesOrIsInside( place, locale );
                        }
                    }
            );
            return (List<String>) CollectionUtils.collect(
                    allCandidatePlaces,
                    new Transformer() {
                        public Object transform( Object input ) {
                            return ( (Place) input ).getName();
                        }
                    } );
        } else {
            return new ArrayList<String>();
        }
    }


    private void addGeoLocationList() {
        // Alternates
        ListView<GeoLocation> geoLocationsList = new ListView<GeoLocation>(
                "geoLocations",
                new PropertyModel<List<GeoLocation>>( getPlace(), "geoLocations" )
        ) {
            @Override
            protected void populateItem( ListItem<GeoLocation> item ) {
                item.add( new GeoLocationPanel( "geoLocation", item.getModelObject() ) );
            }
        };
        geoLocationsList.setOutputMarkupId( true );
        geoLocationsContainer.addOrReplace( geoLocationsList );
    }

    private void addStreetAddressField() {
        streetContainer = new WebMarkupContainer( "streetContainer" );
        streetContainer.setOutputMarkupId( true );
        streetContainer.setVisible( getPlace().isActual() );
        moDetailsDiv.add( streetContainer );
        streetAddressField = new TextField<String>(
                "streetAddress",
                new PropertyModel<String>( this, "streetAddress" ) );
        streetAddressField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssues( streetAddressField, getPlace(), "streetAddress" );
                update( target, new Change( Change.Type.Updated, getPlace(), "streetAddress" ) );
            }
        } );
        addIssues( streetAddressField, getPlace(), "streetAddress" );
        streetAddressField.setEnabled( isLockedByUser( getPlace() ) );
        addInputHint( streetAddressField, "A street address" );
        streetContainer.add( streetAddressField );
    }

    private void addPostalCodeField() {
        codeContainer = new WebMarkupContainer( "codeContainer" );
        codeContainer.setOutputMarkupId( true );
        codeContainer.setVisible( getPlace().isActual() );
        moDetailsDiv.add( codeContainer );
        postalCodeField = new TextField<String>(
                "postalCode",
                new PropertyModel<String>( this, "postalCode" ) );
        postalCodeField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssues( geonameField, getPlace(), "geoname" );
                target.add( geonameField );
                addIssues( postalCodeField, getPlace(), "postalCode" );
                target.add( postalCodeField );
                addIssues( withinField, getPlace(), "within" );
                target.add( withinField );
                update( target, new Change( Change.Type.Updated, getPlace(), "postalCode" ) );
            }
        } );
        addIssues( postalCodeField, getPlace(), "postalCode" );
        postalCodeField.setEnabled( isLockedByUser( getPlace() ) );
        addInputHint( postalCodeField, "A postal code" );
        codeContainer.add( postalCodeField );
    }

    private void addGeonameField() {
        geonameContainer = new WebMarkupContainer( "geonameContainer" );
        geonameContainer.setVisible( getPlace().isActual() );
        geonameContainer.setOutputMarkupId( true );
        moDetailsDiv.add( geonameContainer );
        final List<String> choices = getQueryService().findAllGeonames();
        geonameField = new AutoCompleteTextField<String>(
                "geoname",
                new PropertyModel<String>( this, "geoname" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( input, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        geonameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addGeoLocationList();
                target.add( geoLocationsContainer );
                addIssues( geonameField, getPlace(), "geoname" );
                target.add( geonameField );
                addIssues( postalCodeField, getPlace(), "postalCode" );
                target.add( postalCodeField );
                addIssues( withinField, getPlace(), "within" );
                target.add( withinField );
                update( target, new Change( Change.Type.Updated, getPlace(), "geoname" ) );
            }
        } );
        addIssues( geonameField, getPlace(), "geoname" );
        geonameField.setEnabled( isLockedByUser( getPlace() ) );
        addInputHint( geonameField, "A geo-name" );
        geonameContainer.add( geonameField );
    }

    private void addWithinConstraint() {
        WebMarkupContainer containedInContainer = new WebMarkupContainer( "containedInConstraint" );
        containedInContainer.setVisible( getPlace().isType() );
        moDetailsDiv.add( containedInContainer );
        PlaceReferencePanel mustBeContainedInPanel = new PlaceReferencePanel(
                "mustBeContainedIn",
                new Model<Place>( getPlace() ),
                "mustBeContainedIn" );
        mustBeContainedInPanel.enable( isLockedByUser( getPlace() ) );
        containedInContainer.add( mustBeContainedInPanel );
    }

    private void addContainsConstraint() {
        WebMarkupContainer containsContainer = new WebMarkupContainer( "containsConstraint" );
        containsContainer.setVisible( getPlace().isType() );
        moDetailsDiv.add( containsContainer );
        PlaceReferencePanel mustContainsPanel = new PlaceReferencePanel(
                "mustContain",
                new Model<Place>( getPlace() ),
                "mustContain" );
        mustContainsPanel.enable( isLockedByUser( getPlace() ) );
        containsContainer.add( mustContainsPanel );
    }

    private Place getPlace() {
        return (Place) getEntity();
    }

    /**
     * Return the place's street address.
     *
     * @return a string
     */
    public String getStreetAddress() {
        String address = getPlace().getStreetAddress();
        return address == null ? "" : address;
    }

    /**
     * Set place's street address.
     *
     * @param val a string
     */
    public void setStreetAddress( String val ) {
        updatePlace( "streetAddress", val );
    }

    private void updatePlace( String property, Object value ) {
        Place place = getPlace();
        doCommand( new UpdatePlanObject( getUser().getUsername(), place, property, value ) );
        geoService.validate( place );

    }

    /**
     * Return the place's postal code.
     *
     * @return a string
     */
    public String getPostalCode() {
        String code = getPlace().getPostalCode();
        return code == null ? "" : code;
    }

    /**
     * Set place's postal code.
     *
     * @param val a string
     */
    public void setPostalCode( String val ) {
        updatePlace( "postalCode", val );
    }

    /**
     * Return the place's geoname.
     *
     * @return a string
     */
    public String getGeoname() {
        String geoname = getPlace().getGeoname();
        return geoname == null ? "" : geoname;
    }

    /**
     * Set place's geoname.
     *
     * @param val a string
     */
    public void setGeoname( String val ) {
        updatePlace( "geoname", val );
    }

    /**
     * Return the name of the place containing this place.
     *
     * @return a place
     */
    public String getWithinName() {
        Place within = getPlace().getWithin();
        return within == null ? "" : within.getName();
    }

    /**
     * Set the place containing this place given its name.
     *
     * @param name a string
     */
    public void setWithinName( String name ) {
        Place oldWithin = getPlace().getWithin();
        String oldName = oldWithin == null ? "" : oldWithin.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldWithin == null || !isSame( name, oldName ) )
                newPlace = ( getPlace().isActual()
                        ? doSafeFindOrCreateActual( Place.class, name )
                        : doSafeFindOrCreateType( Place.class, name ) );
        }
        updatePlace( "within", newPlace );
        getCommander().cleanup( Place.class, oldName );
    }

     private class GeoLocationPanel extends AbstractUpdatablePanel {
        /**
         * A geolocation.
         */
        private GeoLocation geoLocation;

        public GeoLocationPanel( String id, final GeoLocation geoLocation ) {
            super( id );
            this.geoLocation = geoLocation;
            Label alternateLabel = new Label( "name", geoLocation.toString() );
            add( alternateLabel );
            CheckBox selectionCheckBox = new CheckBox(
                    "selection",
                    new PropertyModel<Boolean>( this, "selected" ) );
            selectionCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    addIssues( postalCodeField, getPlace(), "postalCode" );
                    target.add( postalCodeField );
                    target.add( geoLocationsContainer );
                    update( target, new Change( Change.Type.Updated, getPlace(), "geoLocation" ) );
                }
            } );
            selectionCheckBox.setEnabled( isLockedByUser( getPlace() ) );
            add( selectionCheckBox );
            BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                    "mapLink",
                    new Model<String>( geoLocation.toString() ),
                    geoLocation, getQueryService().getPlan() );
            add( geomapLink );
        }

        /**
         * Select is geoLocation selected?
         *
         * @return a boolean
         */
        public boolean isSelected() {
            GeoLocation geoLoc = getPlace().getLocationBasis();
            return geoLoc != null && geoLoc.getGeonameId() == geoLocation.getGeonameId();
        }

        /**
         * Set place's geoLocation.
         *
         * @param val a boolean
         */
        public void setSelected( boolean val ) {
            if ( val )
                updatePlace( "geoLocation", geoLocation );
            else if ( isSelected() )
                updatePlace( "geoLocation", null );
        }
    }



}
