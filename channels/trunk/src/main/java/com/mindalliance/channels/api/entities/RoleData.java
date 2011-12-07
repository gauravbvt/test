package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.ModelEntityData;
import com.mindalliance.channels.core.model.ModelObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement( name = "role", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"id", "name", "categories"} )
public class RoleData extends ModelEntityData {

    public RoleData() {
        // required
    }

    public RoleData( ModelObject modelObject ) {
        super( modelObject );
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
    public List<Long> getCategories() {
        return super.getCategories();
    }

}
