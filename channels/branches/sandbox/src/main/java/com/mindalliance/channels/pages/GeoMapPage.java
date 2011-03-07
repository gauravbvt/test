package com.mindalliance.channels.pages;

import com.mindalliance.channels.geo.GeoService;
import com.mindalliance.channels.model.GeoLocatable;
import com.mindalliance.channels.model.GeoLocation;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.collections.ArrayListStack;
import wicket.contrib.gmap.GMap2;
import wicket.contrib.gmap.api.GControl;
import wicket.contrib.gmap.api.GLatLng;
import wicket.contrib.gmap.api.GMarker;
import wicket.contrib.gmap.api.GMarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Page showing geo map.
 * Note: can't use an Ajax-generated panel with GMap2
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 18, 2009
 * Time: 12:47:45 PM
 */
public class GeoMapPage extends AbstractChannelsWebPage {

    private static final String MARKER_PARAM = "m";

    private static final String MARKER_SEP = "||";

    private static final int MAX_QUERY_SIZE = 2000;

    private static final String TITLE_PARAM = "t";

    /** The geo service */
    @SpringBean
    private GeoService geoService;

    private List<GeoMarker> geoMarkers;

    public GeoMapPage( PageParameters pageParameters ) {
        super( pageParameters );

        geoMarkers = getGeoMarkers( pageParameters );
        String title = pageParameters.getString( TITLE_PARAM );

        boolean hideMap = !geoService.isConfigured() || geoMarkers.isEmpty();
        add(
            new Label( "title", title ),
            new Label( "caption", title ),
            new WebMarkupContainer( "nothing" ).setVisible( hideMap ),

            hideMap ? new Label( "map", "" ).setVisible( false )
                    : createGmap() );
    }

    private static List<GeoMarker> getGeoMarkers( PageParameters params ) {
        List<GeoMarker> markers = new ArrayList<GeoMarker>();
        String[] values = params.getStringArray( MARKER_PARAM );
        if ( values != null )
            for ( String value : values )
                    markers.add( new GeoMarker( value ) );

        return markers;
    }

    private GMap2 createGmap() {
        GMap2 map = new GMap2( "map", getGoogleMapsAPIkey() );
        map.addControl( GControl.GMapTypeControl );
        map.addControl( GControl.GLargeMapControl );

        List<GLatLng> gLatLngs = new ArrayListStack<GLatLng>();
        for ( GeoMarker geoMarker : geoMarkers ) {
            GLatLng gLatLng = new GLatLng( geoMarker.getLatitude(), geoMarker.getLongitude() );
            map.addOverlay( new GMarker( gLatLng, new GMarkerOptions( geoMarker.getLabel() ) ) );
            gLatLngs.add( gLatLng );
        }

        map.fitMarkers( gLatLngs, false );
        map.setDoubleClickZoomEnabled( true );
        map.setScrollWheelZoomEnabled( true );
        return map;
    }

    private String getGoogleMapsAPIkey() {
        return geoService.getGoogleMapsAPIKey();
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink(
        String id,
        IModel<String> titleModel,
        GeoLocatable geo,
        QueryService queryService ) {
        List<GeoLocatable> geos = new ArrayList<GeoLocatable>();
        geos.addAll( geo.getImpliedGeoLocatables( queryService ) );
        return makeLink( id, titleModel, geos, queryService );
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink(
        String id,
        IModel<String> titleModel,
        List<? extends GeoLocatable> geos,
        QueryService queryService) {
        PageParameters params = makeGeoMapParameters( titleModel, geos );
        BookmarkablePageLink<GeoMapPage> link = makeLink( id, params );
        addPlanParameters( link, queryService.getPlan() );
        return link;
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink(
        String id,
        IModel<String> titleModel,
        GeoLocation geoLocation,
        QueryService queryService ) {
        PageParameters params = makeGeoMapParameters(
                titleModel,
                geoLocation );
        BookmarkablePageLink<GeoMapPage> link = makeLink( id, params );
        addPlanParameters( link, queryService.getPlan() );
        return link;
    }

    private static BookmarkablePageLink<GeoMapPage> makeLink( String id, PageParameters params ) {
        PopupSettings popupSettings = new PopupSettings( PopupSettings.LOCATION_BAR );
        popupSettings.setHeight( 450 );
        popupSettings.setWidth( 620 );
        popupSettings.setTop( 100 );
        popupSettings.setLeft( 100 );
        BookmarkablePageLink<GeoMapPage> geomapLink = new BookmarkablePageLink<GeoMapPage>(
            id, GeoMapPage.class, params );
        geomapLink.add(
            new AttributeModifier(
                "target", true, new Model<String>( "geomap" ) ) );
        return geomapLink;
    }

    private static PageParameters makeGeoMapParameters(
        IModel<String> titleModel, GeoLocation geoLocation ) {
        PageParameters params = new PageParameters();
        params.put( TITLE_PARAM, titleModel.getObject() );
        String value = makeMarkerParam( geoLocation.toString(), geoLocation );
        params.put( MARKER_PARAM, value );
        return params;
    }

    private static PageParameters makeGeoMapParameters(
        IModel<String> titleModel, List<? extends GeoLocatable> geos ) {

        Map<GeoLocation, List<GeoLocatable>> locatedGeos = new HashMap<GeoLocation, List<GeoLocatable>>();
        for ( GeoLocatable geo : new HashSet<GeoLocatable>( geos ) ) {
            Place place = geo.getPlaceBasis();
            if ( place != null ) {
                GeoLocation geoLocation = place.getLocationBasis();
                if ( geoLocation != null ) {
                    List<GeoLocatable> locs = locatedGeos.get( geoLocation );
                    if ( locs == null ) {
                        locs = new ArrayList<GeoLocatable>();
                        locatedGeos.put( geoLocation, locs );
                    }
                    locs.add( geo );
                }
            }
        }

        PageParameters params = new PageParameters();
        params.put( TITLE_PARAM, titleModel.getObject() );
        int querySize = 0;
        Iterator<GeoLocation> iter = locatedGeos.keySet().iterator();
        while ( iter.hasNext() && querySize < MAX_QUERY_SIZE ) {
            GeoLocation geoLocation = iter.next();
            Iterator<GeoLocatable> locs = locatedGeos.get( geoLocation ).iterator();
            StringBuilder sb = new StringBuilder();
            Set<String> labels = new HashSet<String>();
            while ( locs.hasNext() ) {
                GeoLocatable geo = locs.next();
                String label = geo.getGeoMarkerLabel( Channels.instance().getQueryService() );
                if ( !labels.contains( label ) ) {
                    if ( !sb.toString().isEmpty() && !sb.toString().endsWith( " - " ) ) {
                        sb.append( " - " );
                    }
                    labels.add( label );
                    sb.append( labels.size() );
                    sb.append( ". " );
                    sb.append( label );
                }
            }
            String label = sb.toString();
            String value = makeMarkerParam( label, geoLocation );
            querySize += GeoMapPage.MARKER_PARAM.length() + value.length() + 1;
            if ( querySize < MAX_QUERY_SIZE )
                params.add( MARKER_PARAM, value );
        }
        return params;
    }

    private static String makeMarkerParam( String label, GeoLocation geoLocation ) {
        StringBuilder sb = new StringBuilder();
        sb.append( label );
        sb.append( MARKER_SEP );
        sb.append( geoLocation.getLatitude() );
        sb.append( MARKER_SEP );
        sb.append( geoLocation.getLongitude() );
        return sb.toString();
    }

    private static class GeoMarker implements Serializable {

        private final String label;

        private final double latitude;

        private final double longitude;

        private GeoMarker( String param ) {
            String[] vals = StringUtils.split( param, MARKER_SEP );
            assert vals.length == 3;
            label = vals[ 0 ];
            latitude = Double.valueOf( vals[ 1 ] );
            longitude = Double.valueOf( vals[ 2 ] );
        }

        public String getLabel() {
            return label;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
