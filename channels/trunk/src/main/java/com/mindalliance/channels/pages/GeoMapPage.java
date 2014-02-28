package com.mindalliance.channels.pages;

import com.google.code.jqwicket.ui.gmap.GMapMarker;
import com.google.code.jqwicket.ui.gmap.GMapOptions;
import com.google.code.jqwicket.ui.gmap.GMapWebMarkupContainer;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.GeoLocation;
import com.mindalliance.channels.core.model.Job;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.geo.GeoService;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Page showing geo map. Note: can't use an Ajax-generated panel with GMap2 Copyright (C) 2008 Mind-Alliance Systems.
 * All Rights Reserved. Proprietary and Confidential. User: jf Date: Jun 18, 2009 Time: 12:47:45 PM
 */
public class GeoMapPage extends AbstractChannelsWebPage {

    private static final String MARKER_PARAM = "m";

    private static final String MARKER_SEP = "||";

    private static final String LABEL_SEP = "---";

    private static final int MAX_QUERY_SIZE = 2000;

    private static final String TITLE_PARAM = "t";

    /**
     * The geo service.
     */
    @SpringBean
    private GeoService geoService;

    private List<GeoMarker> geoMarkers;

    public GeoMapPage( PageParameters pageParameters ) {
        super( pageParameters );

        geoMarkers = getGeoMarkers( pageParameters );
        String title = pageParameters.get( TITLE_PARAM ).toString();

        boolean hideMap = !geoService.isConfigured() || geoMarkers.isEmpty();
        add( new Label( "title", title ),
                new Label( "caption", title ),
                new WebMarkupContainer( "nothing" ).setVisible( hideMap ),
                hideMap ? new Label( "map", "" ).setVisible( false ) : createGmap() );
    }

    /**
     * Find all implied geolocations for a geolocatable.
     *
     * @param queryService a query service
     * @param geoLocatable a geolocatable
     * @return a list of geolocations
     */
    public static List<GeoLocation> getImpliedGeoLocations( QueryService queryService, GeoLocatable geoLocatable ) {

        List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();

        for ( GeoLocatable geo : getImpliedGeoLocatables( geoLocatable, queryService ) ) {
            Place placeBasis = geo.getPlaceBasis();
            if ( placeBasis != null ) {
                GeoLocation geoLocation = placeBasis.getGeoLocation();
                if ( geoLocation != null )
                    geoLocations.add( geoLocation );
            }
        }
        return geoLocations;
    }


    private static List<GeoMarker> getGeoMarkers( PageParameters params ) {
        List<GeoMarker> markers = new ArrayList<GeoMarker>();
        List<StringValue> values = params.getValues( MARKER_PARAM );
        if ( values != null )
            for ( StringValue value : values )
                markers.add( new GeoMarker( value.toString() ) );

        return markers;
    }

    private GMapWebMarkupContainer createGmap() {
        GMapWebMarkupContainer map = new GMapWebMarkupContainer(
                "map",
                makeGMapOptions()
        );
/*
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
*/
        return map;
    }

    private GMapOptions makeGMapOptions() {
        GMapOptions options = new GMapOptions( getGoogleMapsAPIkey() );
        options.zoom( 6 );
        options.markers( makeMarkers() );
        return options;
    }

    private GMapMarker[] makeMarkers() {
        List<GMapMarker> markers = new ArrayList<GMapMarker>();
        for ( GeoMarker geoMarker : geoMarkers ) {
            GMapMarker marker = new GMapMarker();
            marker.latitude( geoMarker.getLatitude() );
            marker.longitude( geoMarker.getLongitude() );
            marker.html( geoMarker.getHtmlLabel() ).popup( true );
            markers.add( marker );
        }
        return markers.toArray( new GMapMarker[markers.size()] );
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
        geos.addAll( getImpliedGeoLocatables( geo, queryService ) );
        return makeLink( id, titleModel, geos, queryService );
    }

    public static List<? extends GeoLocatable> getImpliedGeoLocatables( GeoLocatable geo, QueryService queryService ) {
        if ( geo instanceof Job ) {
            Place jurisdiction = ( (Job) geo ).getJurisdiction();
            return jurisdiction == null ? new ArrayList<Place>()
                    : queryService.listEntitiesNarrowingOrEqualTo( jurisdiction );
        } else if ( geo instanceof Organization ) {
            List<Organization> result = new ArrayList<Organization>();
            for ( Organization org : queryService.listEntitiesNarrowingOrEqualTo( (Organization) geo ) )
                if ( org.isActual() && org.getPlaceBasis() != null )
                    result.add( org );

            return result;
        } else if ( geo instanceof Place ) {
            List<Place> result = new ArrayList<Place>();
            for ( Place place : queryService.listEntitiesNarrowingOrEqualTo( (Place) geo ) )
                if ( place.isActual() && place.getPlaceBasis() != null )
                    result.add( place );

            return result;
        }

        return geo.getImpliedGeoLocatables();
    }


    public static BookmarkablePageLink<GeoMapPage> makeLink(
            String id,
            IModel<String> titleModel,
            List<? extends GeoLocatable> geos,
            QueryService queryService ) {

        PageParameters params = makeGeoMapParameters( titleModel, geos, queryService );
        BookmarkablePageLink<GeoMapPage> link = makeLink( id, params );
        addPlanParameters( link, queryService.getCollaborationModel() );
        return link;
    }

    public static BookmarkablePageLink<GeoMapPage> makeLink(
            String id, IModel<String> titleModel, GeoLocation geoLocation, CollaborationModel collaborationModel ) {

        PageParameters params = makeGeoMapParameters( titleModel, geoLocation );
        BookmarkablePageLink<GeoMapPage> link = makeLink( id, params );
        addPlanParameters( link, collaborationModel );
        return link;
    }

    private static BookmarkablePageLink<GeoMapPage> makeLink( String id, PageParameters params ) {
        PopupSettings popupSettings = new PopupSettings( PopupSettings.LOCATION_BAR );
        popupSettings.setHeight( 450 );
        popupSettings.setWidth( 620 );
        popupSettings.setTop( 100 );
        popupSettings.setLeft( 100 );
        BookmarkablePageLink<GeoMapPage> geomapLink = new BookmarkablePageLink<GeoMapPage>( id, GeoMapPage.class, params );
        geomapLink.add( new AttributeModifier( "target", new Model<String>( "geomap" ) ) );
        return geomapLink;
    }

    private static PageParameters makeGeoMapParameters( IModel<String> titleModel, GeoLocation geoLocation ) {
        PageParameters params = new PageParameters();
        params.set( TITLE_PARAM, titleModel.getObject() );
        String value = makeMarkerParam( geoLocation.toString(), geoLocation );
        params.set( MARKER_PARAM, value );
        return params;
    }

    private static PageParameters makeGeoMapParameters(
            IModel<String> titleModel, List<? extends GeoLocatable> geos, QueryService queryService ) {

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
        params.set( TITLE_PARAM, titleModel.getObject() );
        int querySize = 0;
        Iterator<GeoLocation> iter = locatedGeos.keySet().iterator();
        while ( iter.hasNext() && querySize < MAX_QUERY_SIZE ) {
            GeoLocation geoLocation = iter.next();
            StringBuilder sb = new StringBuilder();
            Set<String> labels = new HashSet<String>();
            for ( GeoLocatable geo : locatedGeos.get( geoLocation ) ) {
                String label = getGeoMarkerLabel( queryService, geo );
                if ( !labels.contains( label ) ) {
                    labels.add( label );
                    sb.append( label );
                    sb.append( LABEL_SEP );
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

    private static String getGeoMarkerLabel( QueryService queryService, GeoLocatable geo ) {
        if ( geo instanceof Part ) {
            Part part = (Part) geo;
            StringBuilder sb = new StringBuilder();
            sb.append( queryService.getFullTitle( " ", part ) );
            Place location = part.getKnownLocation();
            if ( location != null ) {
                sb.append( " at " );
                sb.append( location.getName() );
            }
            return sb.toString();
        } else
            return geo.getGeoMarkerLabel();
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

    private static final class GeoMarker implements Serializable {

        private final String label;

        private final float latitude;

        private final float longitude;

        private GeoMarker( String param ) {
            String[] vals = StringUtils.split( param, MARKER_SEP );
            assert vals.length == 3;
            label = vals[0];
            latitude = Float.valueOf( vals[1] );
            longitude = Float.valueOf( vals[2] );
        }

        public String getLabel() {
            return label;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public String getHtmlLabel() {
            StringBuilder sb = new StringBuilder();
            sb.append( "<ol>" );
            for ( String item : getLabel().split( LABEL_SEP ) ) {
                sb.append( "<li>" );
                sb.append( item.replaceAll( "<", "(" ).replaceAll( ">", ")" ) );
                sb.append( "</li>" );
            }
            sb.append( "</ol>" );
            return sb.toString();
        }
    }
}
