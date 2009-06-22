package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.pages.GeoMapPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Geomap link panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 22, 2009
 * Time: 10:22:09 AM
 */
public class GeomapLinkPanel extends Panel {

    private List<? extends GeoLocatable> geoLocatables;
    private IModel<String> titleModel;
    private IModel<String> hintModel;

    public GeomapLinkPanel(
            String id,
            IModel<String> titleModel,
            List<? extends GeoLocatable> geoLocatables,
            IModel<String> hintModel ) {
        super( id );
        this.geoLocatables = geoLocatables;
        this.titleModel = titleModel;
        this.hintModel = hintModel;
        init();
    }

    private void init() {
        BookmarkablePageLink geomapLink = GeoMapPage.makeLink(
                "mapLink",
                titleModel,
                geoLocatables
        );
        geomapLink.add( new AttributeModifier(
                "title",
                true,
                hintModel ) );
        boolean somethingToMap = CollectionUtils.find(
                geoLocatables,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (GeoLocatable) obj ).getGeoLocation() != null;
                    }
                } ) != null;
        geomapLink.setVisible( somethingToMap );
        add( geomapLink );
    }

}
