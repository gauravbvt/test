package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.util.Matcher;
import com.mindalliance.channels.util.NameRange;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
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

import java.util.ArrayList;
import java.util.Arrays;
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
public class PlaceDetailsPanel extends EntityDetailsPanel implements NameRangeable, Filterable {
    /**
     * Maximum number of places within to show in table at once.
     */
    private static final int MAX_ROWS = 5;

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
    /**
     * Geomap link to places within this one.
     */
    private GeomapLinkPanel withinPlacesMapLink;
    /**
     * Selected name range.
     */
    private NameRange nameRange;
    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * /**
     * Role employment table.
     */
    private PlacesWithinTable placesWithinTable;
    /**
     * Model objects filtered on
     */
    private List<Identifiable> filters;

    public PlaceDetailsPanel(
            String id,
            IModel<? extends ModelObject> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        filters = new ArrayList<Identifiable>();
        nameRange = new NameRange();
        this.moDetailsDiv = moDetailsDiv;
        addWithinField();
        addStreetAddressField();
        addPostalCodeField();
        addGeonameField();
        geoLocationsContainer = new WebMarkupContainer( "geoLocationsContainer" );
        geoLocationsContainer.setOutputMarkupId( true );
        moDetailsDiv.add( geoLocationsContainer );
        addGeoLocationList();
        addPlacesWithinGeomapLink();
        addNameRangePanel();
        addPlacesWithinTable();
        addPlaceReferencesTable();
        adjustFields();
    }

    private void adjustFields() {
        postalCodeField.setEnabled( isLockedByUser( getPlace() ) );
        geonameField.setEnabled( isLockedByUser( getPlace() ) );
        withinField.setEnabled( isLockedByUser( getPlace() ) );
    }

    private void addWithinField() {
        moDetailsDiv.add(
                new ModelObjectLink( "within-link",
                        new PropertyModel<Place>( getPlace(), "within" ),
                        new Model<String>( "Is within" ) ) );
        final List<String> choices = findWithinCandidates();
        withinField = new AutoCompleteTextField<String>(
                "within",
                new PropertyModel<String>( this, "withinName" ) ) {
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
                target.addComponent( withinField );
                refreshPlacesWithin( target );
                update( target, new Change( Change.Type.Updated, getPlace(), "within" ) );
            }
        } );
        addIssues( withinField, getPlace(), "within" );
        withinField.setEnabled( isLockedByUser( getPlace() ) );
        withinField.setOutputMarkupId( true );
        moDetailsDiv.add( withinField );
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findWithinCandidates() {
        final Place place = getPlace();
        List<Place> allCandidatePlaces = (List<Place>) CollectionUtils.select(
                getQueryService().list( Place.class ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return !( (Place) object ).isSameAsOrIn( place );
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
    }

    private void refreshPlacesWithin( AjaxRequestTarget target ) {
        addPlacesWithinGeomapLink();
        addNameRangePanel();
        addPlacesWithinTable();
        target.addComponent( withinPlacesMapLink );
        target.addComponent( nameRangePanel );
        target.addComponent( placesWithinTable );
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
        geoLocationsContainer.add( geoLocationsList );
    }

    private void addStreetAddressField() {
        final TextField<String> streetAddressField = new TextField<String>(
                "streetAddress",
                new PropertyModel<String>( this, "streetAddress" ) );
        streetAddressField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                refreshPlacesWithin( target );
                addIssues( streetAddressField, getPlace(), "streetAddress" );
                update( target, new Change( Change.Type.Updated, getPlace(), "streetAddress" ) );
            }
        } );
        addIssues( streetAddressField, getPlace(), "streetAddress" );
        streetAddressField.setEnabled( isLockedByUser( getPlace() ) );
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
                addIssues( withinField, getPlace(), "within" );
                target.addComponent( withinField );
                refreshPlacesWithin( target );
                update( target, new Change( Change.Type.Updated, getPlace(), "postalCode" ) );
            }
        } );
        addIssues( postalCodeField, getPlace(), "postalCode" );
        postalCodeField.setEnabled( isLockedByUser( getPlace() ) );
        moDetailsDiv.add( postalCodeField );
    }

    private void addGeonameField() {
        final List<String> choices = getQueryService().findAllGeonames();
        geonameField = new AutoCompleteTextField<String>(
                "geoname",
                new PropertyModel<String>( this, "geoname" ) ) {
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
                addIssues( geonameField, getPlace(), "geoname" );
                target.addComponent( geonameField );
                addIssues( postalCodeField, getPlace(), "postalCode" );
                target.addComponent( postalCodeField );
                addIssues( withinField, getPlace(), "within" );
                target.addComponent( withinField );
                refreshPlacesWithin( target );
                update( target, new Change( Change.Type.Updated, getPlace(), "geoname" ) );
            }
        } );
        addIssues( geonameField, getPlace(), "geoname" );
        geonameField.setEnabled( isLockedByUser( getPlace() ) );
        moDetailsDiv.add( geonameField );
    }

    private void addPlacesWithinGeomapLink() {
        List<? extends GeoLocatable> geoLocatables = getPlacesWithin();
        withinPlacesMapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>( "Places within " + getPlace().getName() ),
                geoLocatables,
                new Model<String>( "Show listed places within this one" ) );
        withinPlacesMapLink.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( withinPlacesMapLink );
    }

    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All names"
        );
        nameRangePanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( nameRangePanel );
    }

    private void addPlacesWithinTable() {
        placesWithinTable = new PlacesWithinTable(
                "placesWithin",
                new PropertyModel<List<Place>>( this, "placesWithin" ),
                MAX_ROWS
        );
        placesWithinTable.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( placesWithinTable );
    }

    private void addPlaceReferencesTable() {
        PlaceReferencesTable placeReferencesTable = new PlaceReferencesTable(
                "placeReferences",
                new PropertyModel<List<ModelObject>>( this, "placeReferences" ),
                MAX_ROWS
        );
        moDetailsDiv.add( placeReferencesTable );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        refreshPlacesWithin( target );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    /**
     * {@inheritDoc}
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addPlacesWithinTable();
        target.addComponent( placesWithinTable );
    }

    /**
     * Find all names to be indexed.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        List<Place> places = getQueryService().findAllPlacesWithin( getPlace() );
        return (List<String>) CollectionUtils.collect(
                places,
                new Transformer() {
                    public Object transform( Object input ) {
                        return ( (Place) input ).getName();
                    }
                } );
    }

    /**
     * Find all employments in the plan that are not filtered out and are within selected name
     * range.
     *
     * @return a list of employments.
     */
    @SuppressWarnings( "unchecked" )
    public List<Place> getPlacesWithin() {
        return (List<Place>) CollectionUtils.select(
                getQueryService().findAllPlacesWithin( getPlace() ),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return !isFilteredOut( (Place) object ) && isInNameRange( (Place) object );
                    }
                }
        );

    }

    private boolean isFilteredOut( Place place ) {
        boolean filteredOut = false;
        for ( Identifiable filter : filters ) {
            filteredOut = filteredOut
                        || filter instanceof Place
                           && ( place.getWithin() == null || !place.getWithin().equals( filter ) );
        }
        return filteredOut;
    }

    private boolean isInNameRange( Place place ) {
        return nameRange.contains( place.getName() );
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
        doCommand( new UpdatePlanObject( place, property, value ) );
        place.validate( Channels.instance().getGeoService() );

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
                newPlace = getQueryService().findOrCreate( Place.class, name );
        }
        updatePlace( "within", newPlace );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Find all model objects that reference this place.
     *
     * @return a list of model objects
     */
    public List<ModelObject> getPlaceReferences() {
        return getQueryService().findAllReferencesTo( getPlace() );
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
            selectionCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
                @Override
                protected void onUpdate( AjaxRequestTarget target ) {
                    addIssues( postalCodeField, getPlace(), "postalCode" );
                    target.addComponent( postalCodeField );
                    target.addComponent( geoLocationsContainer );
                    refreshPlacesWithin( target );
                    update( target, new Change( Change.Type.Updated, getPlace(), "geoLocation" ) );
                }
            } );
            selectionCheckBox.setEnabled( isLockedByUser( getPlace() ) );
            add( selectionCheckBox );
            BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                    "mapLink",
                    new Model<String>( geoLocation.toString() ),
                    geoLocation );
            add( geomapLink );
        }

        /**
         * Select is geoLocation selected?
         *
         * @return a boolean
         */
        public boolean isSelected() {
            GeoLocation geoLoc = getPlace().geoLocate();
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

    /**
     * Places within table panel.
     */
    public class PlacesWithinTable extends AbstractTablePanel<Place> {

        /**
         * Employment model.
         */
        private IModel<List<Place>> placesModel;

        public PlacesWithinTable(
                String id,
                IModel<List<Place>> placesModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.placesModel = placesModel;
            init();
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeLinkColumn(
                    "Name",
                    "",
                    "name",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "Is within",
                    "within",
                    "within.name",
                    EMPTY,
                    PlaceDetailsPanel.this ) );
            columns.add( makeColumn(
                    "Address",
                    "fullAddress",
                    "fullAddress",
                    EMPTY ) );
            columns.add( makeGeomapLinkColumn(
                    "",
                    "name",
                    Arrays.asList( "" ),
                    new Model<String>( "Show place in map" ) ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "places",
                    columns,
                    new SortableBeanProvider<Place>(
                            placesModel.getObject(),
                            "name" ),
                    getPageSize() ) );
        }
    }

    /**
     * Event reference table.
     */
    public class PlaceReferencesTable extends AbstractTablePanel<ModelObject> {
        /**
         * Event reference model.
         */
        private IModel<List<ModelObject>> referencesModel;

        public PlaceReferencesTable(
                String id,
                IModel<List<ModelObject>> referencesModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.referencesModel = referencesModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn(
                    "Kind",
                    "modelObjectType",
                    "modelObjectType.name",
                    EMPTY ) );
            columns.add( makeLinkColumn(
                    "Name",
                    "",
                    "name",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Description",
                    "description",
                    "description",
                    EMPTY ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "references",
                    columns,
                    new SortableBeanProvider<ModelObject>(
                            referencesModel.getObject(),
                            "name" ),
                    getPageSize() ) );

        }
    }

}
