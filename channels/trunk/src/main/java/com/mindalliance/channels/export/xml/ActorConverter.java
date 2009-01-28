package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.ModelEntity;
import com.mindalliance.channels.pages.Project;

/**
 * XStream Actor converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:42:21 PM
 */
public class ActorConverter extends EntityConverter {

    public ActorConverter() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Actor.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    ModelEntity findOrMakeEntity( String name ) {
        return Project.dao().findOrMakeActor( name );
    }
}
