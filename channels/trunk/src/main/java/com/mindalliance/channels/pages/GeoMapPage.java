package com.mindalliance.channels.pages;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.GeoLocatable;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.GeoLocation;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
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
public class GeoMapPage extends WebPage {

    private static final String MARKER_PARAM = "m";
    private static final String MARKER_SEP = "||";
    private static final int MAX_QUERY_SIZE = 2000;

    /**
     * The query service.
     */
    @SpringBean
    private QueryService queryService;


    private List<GeoMarker> geoMarkers;

    public GeoMapPage( PageParameters pageParameters ) {
        super( pageParameters );
        geoMarkers = getGeoMarkers( pageParameters );
        init();
    }

    private List<GeoMarker> getGeoMarkers( PageParameters params ) {
        List<GeoMarker> markers = new ArrayList<GeoMarker>();
        String[] values = params.getStringArray( MARKER_PARAM );
        if ( values != null ) {
            for ( String value : values ) {
                markers.add( new GeoMarker( value ) );
            }
        }
        return markers;
    }

    private void init() {
        addPageTitle();
        addMap();
    }

    private void addPageTitle() {
        String title = getTitle();
        add( new Label( "title", title ) );
    }

    private void addMap() {
        if ( geoMarkers.isEmpty() ) {
            add( new Label( "map", "Nothing to map" ) );
        } else {
            GMap2 map = new GMap2( "map", getGoogleMapsAPIkey() );
            map.addControl( GControl.GMapTypeControl );
            map.addControl( GControl.GLargeMapControl );
            for ( GeoMarker geoMarker : geoMarkers ) {
                GLatLng gLatLng = new GLatLng( geoMarker.getLatitude(), geoMarker.getLongitude() );
                map.addOverlay( new GMarker( gLatLng, new GMarkerOptions( geoMarker.getLabel() ) ) );
                // center on last marker
                map.setCenter( gLatLng );
            }
            add( map );
        }
    }


    private String getTitle() {
        StringBuilder sb = new StringBuilder();
        for ( GeoMarker geoMarker : geoMarkers ) {
            sb.append( geoMarker.getLabel() );
            sb.append( " " );
        }
        return sb.toString();
    }

    public String getGoogleMapsAPIkey
            () {
        return Channels.instance().getGoogleMapsAPIKey();
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink( String id, GeoLocatable geo ) {
        List<GeoLocatable> geos = new ArrayList<GeoLocatable>();
        geos.add( geo );
        return makeLink( id, geos );
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink( String id, List<? extends GeoLocatable> geos ) {
        PageParameters params = makeGeoMapParameters( geos );
        return makeLink( id, params );
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink( String id, PageParameters params ) {
        BookmarkablePageLink<GeoMapPage> geomapLink = new BookmarkablePageLink<GeoMapPage>(
                id,
                GeoMapPage.class,
                params );
        PopupSettings popupSettings = new PopupSettings( PopupSettings.LOCATION_BAR );
        popupSettings.setHeight( 420 );
        popupSettings.setWidth( 620 );
        popupSettings.setTop( 100 );
        popupSettings.setLeft( 100 );
        geomapLink.setPopupSettings( popupSettings );
        geomapLink.add( new AttributeModifier( "target", true, new Model<String>( "geomap" ) ) );
        return geomapLink;
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink( String id, GeoLocation geoLocation ) {
        PageParameters params = makeGeoMapParameters( geoLocation );
        return makeLink( id, params );
    }

    private static PageParameters makeGeoMapParameters( GeoLocation geoLocation ) {
        PageParameters params = new PageParameters();
        String value = makeMarkerParam( geoLocation.toString(), geoLocation );
        params.put( GeoMapPage.MARKER_PARAM, value );
        return params;
    }

    private static String makeMarkerParam( String label, GeoLocation geoLocation ) {
        StringBuilder sb = new StringBuilder();
        sb.append( label );
        sb.append( GeoMapPage.MARKER_SEP );
        sb.append( geoLocation.getLatitude() );
        sb.append( GeoMapPage.MARKER_SEP );
        sb.append( geoLocation.getLongitude() );
        return sb.toString();
    }

    private static PageParameters makeGeoMapParameters( List<? extends GeoLocatable> geos ) {
        Map<GeoLocation, List<GeoLocatable>> locatedGeos = new HashMap<GeoLocation, List<GeoLocatable>>();
        for ( GeoLocatable geo : new HashSet<GeoLocatable>( geos ) ) {
            GeoLocation geoLocation = geo.getGeoLocation();
            if ( geoLocation != null ) {
                List<GeoLocatable> locs = locatedGeos.get( geoLocation );
                if ( locs == null ) {
                    locs = new ArrayList<GeoLocatable>();
                    locatedGeos.put( geoLocation, locs );
                }
                locs.add( geo );
            }
        }
        PageParameters params = new PageParameters();
        int querySize = 0;
        Iterator<GeoLocation> iter = locatedGeos.keySet().iterator();
        while ( iter.hasNext() && querySize < MAX_QUERY_SIZE ) {
            GeoLocation geoLocation = iter.next();
            Iterator<GeoLocatable> locs = locatedGeos.get( geoLocation ).iterator();
            StringBuilder sb = new StringBuilder();
            Set<String> labels = new HashSet<String>();
            while ( locs.hasNext() ) {
                GeoLocatable geo = locs.next();
                String label = geo.getGeoMarkerLabel();
                if ( !labels.contains( label ) ) {
                    if ( !sb.toString().isEmpty() && !sb.toString().endsWith( " - " ) ) {
                        sb.append( " - " );
                    }
                    labels.add( label );
                    sb.append( labels.size() );
                    sb.append( ". ");
                    sb.append( label );
                }
            }
            String label = sb.toString();
            String value = makeMarkerParam( label, geoLocation );
            querySize += GeoMapPage.MARKER_PARAM.length() + value.length() + 1;
            if ( querySize < MAX_QUERY_SIZE ) {
                params.add( GeoMapPage.MARKER_PARAM, value );
            }
        }
        return params;
    }


    private class GeoMarker implements Serializable {

        private String label;
        private double latitude;
        private double longitude;

        public GeoMarker( String param ) {
            String[] vals = StringUtils.split( param, MARKER_SEP );
            assert vals.length == 3;
            label = vals[0];
            latitude = Double.valueOf( vals[1] );
            longitude = Double.valueOf( vals[2] );
        }

        public GeoMarker( String label, double latitude, double longitude ) {
            this.label = label;
            this.latitude = latitude;
            this.longitude = longitude;
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
