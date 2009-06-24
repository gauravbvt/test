package com.mindalliance.channels.pages.components.entities.menus;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Place;
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
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        Link detailsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, "details" );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Details" ),
                detailsLink ) );
        if ( !( getEntity() instanceof Event || getEntity() instanceof Place ) ) {
            Link networkLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    changeAspectTo( target, "network" );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Network" ),
                    networkLink ) );
        }
        // Map
        if ( getEntity() instanceof GeoLocatable ) {
            GeoLocatable geo = (GeoLocatable) getEntity();
            BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                    "link",
                    new Model<String>( "Location of " + getEntity().getName() ),
                    geo );
            if ( geo.geoLocate() == null ) {
                geomapLink.setEnabled( false );
            }
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Map" ),
                    geomapLink ) );
        }

        /*       Link mapLink = new AjaxFallbackLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        changeAspectTo( target, "map" );
                    }
                };
                menuItems.add( new LinkMenuItem(
                        "menuItem",
                        new Model<String>( "Map" ),
                        mapLink ) );

        */
        if ( !( getEntity() instanceof Event ) ) {
            Link issuesLink = new AjaxFallbackLink( "link" ) {
                public void onClick( AjaxRequestTarget target ) {
                    changeAspectTo( target, "issues" );
                }
            };
            menuItems.add( new LinkMenuItem(
                    "menuItem",
                    new Model<String>( "Issues" ),
                    issuesLink ) );
        }
        return menuItems;
    }

    private void changeAspectTo( AjaxRequestTarget target, String aspect ) {
        entityPanel.setAspectShown( target, aspect );
    }

    public void setEntityPanel( EntityPanel entityPanel ) {
        this.entityPanel = entityPanel;
    }

    private ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }
}
