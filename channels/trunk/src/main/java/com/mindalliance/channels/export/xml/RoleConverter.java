package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Role;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.Project;

/**
 * XStream Role converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:52:06 PM
 */
public class RoleConverter extends EntityConverter {

    public RoleConverter() {        
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Role.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    ModelObject findOrMakeEntity( String name ) {
        return Project.dao().findOrMakeRole( name );
    }
}
