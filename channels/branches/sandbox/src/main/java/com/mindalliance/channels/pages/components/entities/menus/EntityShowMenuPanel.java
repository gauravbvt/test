package com.mindalliance.channels.pages.components.entities.menus;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.geo.GeoLocation;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.components.entities.EntityPanel;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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

    public EntityShowMenuPanel( String id, IModel<? extends Identifiable> model ) {
        super( id, "Show", model, null );
    }

    /**
     * {@inheritDoc}
     */
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
                    !GeoLocation.getImpliedGeoLocations(
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
                        changeAspectTo( target, field );
                    }
                } );
    }

    private void changeAspectTo( AjaxRequestTarget target, String aspect ) {
        entityPanel.setAspectShown( target, aspect );
    }

    public void setEntityPanel( EntityPanel entityPanel ) {
        this.entityPanel = entityPanel;
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }
}
