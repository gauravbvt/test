package com.mindalliance.channels.pages.components.entities.menus;

import com.mindalliance.channels.geo.GeoService;
import com.mindalliance.channels.model.*;
import com.mindalliance.channels.model.GeoLocation;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * EntityPanel show menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 24, 2009
 * Time: 12:52:15 PM
 */
public class EntityShowMenuPanel extends MenuPanel {

    private EntityPanel entityPanel;

    @SpringBean
    private GeoService geoService;

    public EntityShowMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, "Show", model, null );
    }

    /**
     * Find all implied geolocations for a geolocatable.
     *
     * @param geoLocatable a geolocatable
     * @param queryService a query service
     * @return a list of geolocations
     */
    public List<GeoLocation> getImpliedGeoLocations( GeoLocatable geoLocatable, QueryService queryService ) {
        List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();

        for ( GeoLocatable geo : geoLocatable.getImpliedGeoLocatables( queryService ) ) {
            Place placeBasis = geo.getPlaceBasis();
            if ( placeBasis != null ) {
                GeoLocation geoLocation = placeBasis.getGeoLocation();
                if ( geoLocation != null )
                    geoLocations.add( geoLocation );
            }
        }
        return geoLocations;
    }

    @Override
    public List<Component> getMenuItems() {
        synchronized ( getCommander() ) {
            List<Component> menuItems = new ArrayList<Component>();

            menuItems.add( link( "Details", "details" ) );

            ModelEntity entity = getEntity();
            if ( !( entity instanceof Event || entity instanceof Place || entity instanceof Phase
                    || entity instanceof TransmissionMedium ) )

                menuItems.add( link( "Network", "network" ) );

            // Map
            if ( entity instanceof GeoLocatable ) {
                GeoLocatable geo = (GeoLocatable) getEntity();
                BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                    "link",
                    new Model<String>(
                        entity.isActual() ? "Location of " + entity.getName() :
                            "Locations of organizations of type \"" + entity.getName() + "\"" ),
                    geo,
                    getQueryService() );

                geomapLink.setEnabled(
                    !getImpliedGeoLocations(
                        geo, getQueryService() ).isEmpty() );

                menuItems.add(
                    new LinkMenuItem(
                        "menuItem", new Model<String>( "Map" ), geomapLink ) );
            }

            menuItems.add( link( "Issues", "issues" ) );

            return menuItems;
        }    }

    private LinkMenuItem link( String title, final String field ) {
        return new LinkMenuItem(
                "menuItem",
                new Model<String>( title ), new AjaxFallbackLink( "link" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        entityPanel.setAspectShown( target, field );
                    }
                } );
    }

    public void setEntityPanel( EntityPanel entityPanel ) {
        this.entityPanel = entityPanel;
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }
}
