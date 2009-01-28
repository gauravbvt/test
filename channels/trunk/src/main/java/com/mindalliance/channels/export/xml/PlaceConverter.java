package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.ModelEntity;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.pages.Project;

/**
 * XStream place converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:54:07 PM
 */
public class PlaceConverter extends EntityConverter {

    public PlaceConverter() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Place.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    ModelEntity findOrMakeEntity( String name ) {
        return Project.dao().findOrMakePlace( name );
    }

}
