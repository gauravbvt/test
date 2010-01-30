package com.mindalliance.channels.pages.components.segment.menus;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.segment.SegmentEditPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Segment show menu.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 8, 2009
 * Time: 12:54:28 PM
 */
public class SegmentShowMenuPanel extends MenuPanel {

    private SegmentEditPanel segmentEditPanel;

    public SegmentShowMenuPanel( String s, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( s, "Show", model, expansions );
    }

    /**
     * {@inheritDoc}
     */
    public List<Component> getMenuItems() {
        List<Component> menuItems = new ArrayList<Component>();
        // Details
        Link detailsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, SegmentEditPanel.DETAILS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Details" ),
                detailsLink ) );
        // Risks
        Link incidentsLink = new AjaxFallbackLink( "link" ) {
            public void onClick( AjaxRequestTarget target ) {
                changeAspectTo( target, SegmentEditPanel.RISKS );
            }
        };
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Risks" ),
                incidentsLink ) );

        // Map
        List<GeoLocatable> geoLocatables = new ArrayList<GeoLocatable>();
        Iterator<Part> parts = getSegment().parts();
        while ( parts.hasNext() ) {
            geoLocatables.add( parts.next() );
        }
        BookmarkablePageLink<GeoMapPage> geomapLink = GeoMapPage.makeLink(
                "link",
                new Model<String>( "Tasks with known locations in plan segment " + getSegment().getName() ),
                geoLocatables );
        if ( geoLocatables.isEmpty() ) {
            geomapLink.setEnabled( false );
        }
        menuItems.add( new LinkMenuItem(
                "menuItem",
                new Model<String>( "Map" ),
                geomapLink ) );

        return menuItems;
    }

    private void changeAspectTo( AjaxRequestTarget target, String aspect ) {
        segmentEditPanel.setAspectShown( target, aspect );
    }

    public void setSegmentEditPanel( SegmentEditPanel segmentEditPanel ) {
        this.segmentEditPanel = segmentEditPanel;
    }

    /**
     * Get segment from model.
     *
     * @return a segment
     */
    public Segment getSegment() {
        return (Segment) getModel().getObject();
    }
}
