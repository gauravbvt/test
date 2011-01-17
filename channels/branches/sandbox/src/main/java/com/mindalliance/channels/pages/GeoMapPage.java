package com.mindalliance.channels.pages;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.geo.GeoService;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
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
public class GeoMapPage extends WebPage {

    private static final String MARKER_PARAM = "m";
    private static final String MARKER_SEP = "||";
    private static final int MAX_QUERY_SIZE = 2000;
    private static final String TITLE_PARAM = "t";

    /**
     * The query service.
     */
    @SpringBean
    private QueryService queryService;
    /**
     * The geo service
     */
    @SpringBean
    private GeoService geoService;

    private List<GeoMarker> geoMarkers;

    private String title;

    public GeoMapPage( PageParameters pageParameters ) {
        super( pageParameters );
        geoMarkers = getGeoMarkers( pageParameters );
        title = pageParameters.getString( TITLE_PARAM );
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
        add( new Label( "title", title ) );
        add( new Label( "caption", title ) );
    }

    private void addMap() {
        WebMarkupContainer nothing = new WebMarkupContainer( "nothing" );
        nothing.setVisible( geoMarkers.isEmpty() );
        add( nothing );
        if ( geoMarkers.isEmpty() ) {
            Label label = new Label( "map", "" );
            label.setVisible( false );
            add( label );
        } else {
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
            add( map );
        }
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
        return makeLink( id, titleModel, geos );
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink(
            String id,
            IModel<String> titleModel,
            List<? extends GeoLocatable> geos ) {
        PageParameters params = makeGeoMapParameters( titleModel, geos );
        return makeLink( id, params );
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink(
            String id,
            IModel<String> titleModel,
            GeoLocation geoLocation ) {
        PageParameters params = makeGeoMapParameters( titleModel, geoLocation );
        return makeLink( id, params );
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink( String id, PageParameters params ) {
        BookmarkablePageLink<GeoMapPage> geomapLink = new BookmarkablePageLink<GeoMapPage>(
                id,
                GeoMapPage.class,
                params );
        PopupSettings popupSettings = new PopupSettings( PopupSettings.LOCATION_BAR );
        popupSettings.setHeight( 450 );
        popupSettings.setWidth( 620 );
        popupSettings.setTop( 100 );
        popupSettings.setLeft( 100 );
        geomapLink.setPopupSettings( popupSettings );
        geomapLink.add( new AttributeModifier(
                "target",
                true,
                new Model<String>( "geomap" ) ) );
        return geomapLink;
    }


    private static PageParameters makeGeoMapParameters(
            IModel<String> titleModel,
            GeoLocation geoLocation ) {
        PageParameters params = new PageParameters();
        params.put( TITLE_PARAM, titleModel.getObject() );
        String value = makeMarkerParam( geoLocation.toString(), geoLocation );
        params.put( GeoMapPage.MARKER_PARAM, value );
        return params;
    }

    private static PageParameters makeGeoMapParameters(
            IModel<String> titleModel,
            List<? extends GeoLocatable> geos ) {
        Map<GeoLocation, List<GeoLocatable>> locatedGeos = new HashMap<GeoLocation, List<GeoLocatable>>();
        for ( GeoLocatable geo : new HashSet<GeoLocatable>( geos ) ) {
            GeoLocation geoLocation = geo.geoLocate();
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
            if ( querySize < MAX_QUERY_SIZE ) {
                params.add( GeoMapPage.MARKER_PARAM, value );
            }
        }
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
