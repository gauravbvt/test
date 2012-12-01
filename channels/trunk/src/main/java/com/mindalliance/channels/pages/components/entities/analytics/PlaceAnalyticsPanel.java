package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 1:04 PM
 */
public class PlaceAnalyticsPanel
        extends AbstractUpdatablePanel
        implements NameRangeable, Filterable {

    /**
     * Maximum number of places within to show in table at once.
     */
    private static final int MAX_ROWS = 5;

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

    /**
     * Geomap link to places within this one.
     */
    private GeomapLinkPanel withinPlacesMapLink;


    public PlaceAnalyticsPanel( String id, IModel<ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        filters = new ArrayList<Identifiable>();
        nameRange = new NameRange();
        addPlacesWithin();
    }

    private void refreshPlacesWithin( AjaxRequestTarget target ) {
        addPlacesWithin();
        target.add( withinPlacesMapLink );
        target.add( nameRangePanel );
        target.add( placesWithinTable );
    }

    private void addPlacesWithin() {
        WebMarkupContainer placesWithinContainer = new WebMarkupContainer( "placesWithinContainer" );
        placesWithinContainer.setVisible( getPlace().isActual() );
        placesWithinContainer.setOutputMarkupId( true );
        addOrReplace( placesWithinContainer );
        Label placesWithinLabel = new Label(
                "placesWithinTitle",
                "Places within \"" + getPlace().getName() + "\""
        );
        placesWithinLabel.setOutputMarkupId( true );
        placesWithinContainer.addOrReplace( placesWithinLabel );
        // Geomaplink
        List<? extends GeoLocatable> geoLocatables = getPlacesWithin();
        withinPlacesMapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>(
                        "Places "
                                + "within "
                                + getPlace().getName() ),
                geoLocatables,
                new Model<String>( "Show places listed" ) );
        withinPlacesMapLink.setOutputMarkupId( true );
        placesWithinContainer.addOrReplace( withinPlacesMapLink );
        // Name ranges
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All names"
        );
        nameRangePanel.setOutputMarkupId( true );
        placesWithinContainer.addOrReplace( nameRangePanel );
        // Table
        placesWithinTable = new PlacesWithinTable(
                "placesWithin",
                new PropertyModel<List<Place>>( this, "placesWithin" ),
                MAX_ROWS
        );
        placesWithinTable.setOutputMarkupId( true );
        placesWithinContainer.addOrReplace( placesWithinTable );
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
        addPlacesWithin();
        target.add( placesWithinTable );
    }

    /**
     * Find all names to be indexed.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        List<Place> places = findAllPlacesWithin( getQueryService(), getPlace() );
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
        List<Place> placesWithin = getPlace().isActual()
                ? findAllPlacesWithin( ( getQueryService() ), getPlace() )
                : (List<Place>) getQueryService().findAllNarrowingOrEqualTo( getPlace() );
        return (List<Place>) CollectionUtils.select(
                placesWithin,
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

    /**
     * Find all places directly or indirectly within a given place.
     *
     * @param queryService the query service
     * @param place        a place
     * @return a list of places
     */
    public static List<Place> findAllPlacesWithin( QueryService queryService, Place place ) {
        List<Place> places = queryService.listActualEntities( Place.class );
        List<Place> result = new ArrayList<Place>( places.size() );

        Place locale = queryService.getPlanLocale();

        for ( Place p : places )
            if ( !p.equals( place ) && p.matchesOrIsInside( place, locale ) )
                result.add( p );

        return result;
    }

    private Place getPlace() {
        return (Place)getModel().getObject();
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
                    PlaceAnalyticsPanel.this ) );
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

}
