package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import wicket.contrib.gmap.GMap2;
import wicket.contrib.gmap.api.GControl;
import wicket.contrib.gmap.api.GLatLng;
import wicket.contrib.gmap.api.GMarker;
import wicket.contrib.gmap.api.GMarkerOptions;

import java.util.Set;

/**
 * Place map panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 17, 2009
 * Time: 10:53:30 AM
 */
public class PlaceMapPanel extends AbstractCommandablePanel {

    public PlaceMapPanel( String id, PropertyModel<ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addMap();
    }

    private void addMap() {
        if ( getPlace().hasLatLong() ) {
            GMap2 map = new GMap2( "map", getGoogleMapsAPIkey() );
            map.addControl( GControl.GMapTypeControl );
            map.addControl( GControl.GLargeMapControl );
            GLatLng gLatLng = new GLatLng( getPlace().getLatitude(), getPlace().getLongitude() );
            map.setCenter( gLatLng );
            map.addOverlay( new GMarker( gLatLng, new GMarkerOptions( getPlace().getName() ) ) );
            add( map );
        } else {
            add( new Label( "map", "Geolocation is unknown." ) );
        }
    }

    private Place getPlace() {
        return (Place) getModel().getObject();
    }

    public String getGoogleMapsAPIkey() {
        return Channels.instance().getGoogleMapsAPIKey();
    }
}
