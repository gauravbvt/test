package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.util.SemMatch;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
public class PlaceDetailsPanel extends EntityDetailsPanel {
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

    public PlaceDetailsPanel(
            String id,
            IModel<? extends ModelObject> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addStreetAddressField();
        addPostalCodeField();
        addGeonameField();
        geoLocationsContainer = new WebMarkupContainer( "geoLocationsContainer" );
        geoLocationsContainer.setOutputMarkupId( true );
        moDetailsDiv.add( geoLocationsContainer );
        addGeoLocationList();
    }

    private void addGeoLocationList() {
        // Alternates
        ListView<GeoLocation> geoLocationsList = new ListView<GeoLocation>(
                "geoLocations",
                new PropertyModel<List<GeoLocation>>( getPlace(), "geoLocations" )
        ) {
            protected void populateItem( ListItem<GeoLocation> item ) {
                item.add( new GeoLocationPanel( "geoLocation", item.getModelObject() ) );
            }
        };
        geoLocationsContainer.add( geoLocationsList );
    }

    private void addStreetAddressField() {
        TextField<String> streetAddressField = new TextField<String>(
                "streetAddress",
                new PropertyModel<String>( this, "streetAddress" ) );
        streetAddressField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlace(), "streetAddress" ) );
            }
        } );
        moDetailsDiv.add( streetAddressField );
    }

    private void addPostalCodeField() {
        postalCodeField = new TextField<String>(
                "postalCode",
                new PropertyModel<String>( this, "postalCode" ) );
        postalCodeField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssues( geonameField, getPlace(), "geoname" );
                target.addComponent( geonameField );
                addIssues( postalCodeField, getPlace(), "postalCode" );
                target.addComponent( postalCodeField );
                update( target, new Change( Change.Type.Updated, getPlace(), "postalCode" ) );
            }
        } );
        addIssues( postalCodeField, getPlace(), "postalCode" );        
        moDetailsDiv.add( postalCodeField );
    }

    private void addGeonameField() {
        final List<String> choices = getQueryService().findAllGeonames();
        geonameField = new AutoCompleteTextField<String>(
                "geoname",
                new PropertyModel<String>( this, "geoname" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        geonameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssues( geonameField, getPlace(), "geoname" );
                target.addComponent( geonameField );
                addIssues( postalCodeField, getPlace(), "postalCode" );
                target.addComponent( postalCodeField );
                update( target, new Change( Change.Type.Updated, getPlace(), "geoname" ) );
            }
        } );
        addIssues( geonameField, getPlace(), "geoname" );
        moDetailsDiv.add( geonameField );
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
        doCommand( new UpdatePlanObject( getPlace(), "streetAddress", val ) );
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
        doCommand( new UpdatePlanObject( getPlace(), "postalCode", val ) );
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
        doCommand( new UpdatePlanObject( getPlace(), "geoname", val ) );
    }

    private class GeoLocationPanel extends AbstractUpdatablePanel {

        private GeoLocation geoLocation;

        public GeoLocationPanel( String id, final GeoLocation geoLocation ) {
            super( id );
            this.geoLocation = geoLocation;
            Label alternateLabel = new Label( "name", geoLocation.toString() );
            add( alternateLabel );
            CheckBox selectionCheckBox = new CheckBox(
                    "selection",
                    new PropertyModel<Boolean>( this, "selected" ) );
            selectionCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                protected void onUpdate( AjaxRequestTarget target ) {
                    addIssues( postalCodeField, getPlace(), "postalCode" );
                    target.addComponent( postalCodeField );
                    target.addComponent( geoLocationsContainer );
                    update( target, new Change( Change.Type.Updated, getPlace(), "geoLocation" ) );
                }
            } );
            add( selectionCheckBox );
            BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink( "mapLink", geoLocation );
            add( geomapLink );
        }

        /**
         * Select is geoLocation selected?
         *
         * @return a boolean
         */
        public boolean isSelected() {
            GeoLocation geoLoc = getPlace().getGeoLocation();
            return ( geoLoc != null && geoLoc.equals( geoLocation ) );
        }

        /**
         * Set place's geoLocation.
         *
         * @param val a boolean
         */
        public void setSelected( boolean val ) {
            if ( val ) {
                doCommand( new UpdatePlanObject( getPlace(), "geoLocation", geoLocation ) );
            } else {
                if ( isSelected() ) {
                    doCommand( new UpdatePlanObject( getPlace(), "geoLocation", null ) );
                }
            }
        }
    }

}
