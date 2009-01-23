package com.mindalliance.channels.export.xml;

import com.mindalliance.channels.Entity;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.pages.Project;

/**
 * XStream organization converter
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 16, 2009
 * Time: 6:53:05 PM
 */
public class OrganizationConverter extends EntityConverter {

    public OrganizationConverter() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConvert( Class aClass ) {
        return Organization.class.isAssignableFrom( aClass );
    }

    /**
     * {@inheritDoc}
     */
    Entity findOrMakeEntity( String name ) {
        return Project.dao().findOrMakeOrganization( name );
    }
}
