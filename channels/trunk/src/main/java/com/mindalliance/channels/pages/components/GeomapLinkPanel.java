/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.pages.GeoMapPage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Geomap link panel.
 */
public class GeomapLinkPanel extends AbstractUpdatablePanel {

    private List<? extends GeoLocatable> geoLocatables;

    private IModel<String> titleModel;

    private IModel<String> hintModel;

    public GeomapLinkPanel( String id, IModel<String> titleModel, List<? extends GeoLocatable> geoLocatables,
                            IModel<String> hintModel ) {
        super( id );
        this.geoLocatables = geoLocatables;
        this.titleModel = titleModel;
        this.hintModel = hintModel;
        init();
    }

    private void init() {
        add( GeoMapPage.makeLink( "mapLink", titleModel, geoLocatables, getQueryService() )
                .add( new AttributeModifier( "title", hintModel ) )
                .setVisible( hasMappableContent() ) );
    }

    public boolean hasMappableContent() {
        return CollectionUtils.exists( geoLocatables, new Predicate() {
            @Override
            public boolean evaluate( Object object ) {
                return ( (GeoLocatable) object ).getPlaceBasis() != null;
            }
        } );
    }
}
