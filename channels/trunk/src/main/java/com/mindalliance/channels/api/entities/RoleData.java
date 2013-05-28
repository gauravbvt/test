package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 *  Web Service data element for a role.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 10:45 AM
 */
@XmlType( propOrder = {"id", "name", "description", "categories", "documentation"} )
public class RoleData extends ModelEntityData {

    public RoleData() {
        // required
    }

    public RoleData( String serverUrl, ModelObject modelObject, CommunityService communityService ) {
        super( serverUrl, modelObject, communityService );
    }

    @Override
    @XmlElement
    public long getId() {
        return super.getId();
    }

    @Override
    @XmlElement
    public String getName() {
        return super.getName();
    }

    @Override
    @XmlElement
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    @XmlElement( name = "categoryId" )
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

}
