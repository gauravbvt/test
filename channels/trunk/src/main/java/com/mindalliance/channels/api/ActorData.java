package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Web Service data element for an actor.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 10:02 AM
 */
@XmlRootElement( name = "agent", namespace = "http://mind-alliance.com/api/isp/v1/" )
@XmlType( propOrder = {"id", "name", "categories", "kind"} )
public class ActorData extends ModelEntityData {

    public ActorData() {
    }

    public ActorData( ModelObject modelObject ) {
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

    @Override
    @XmlElement
    public String getKind() {
        return super.getKind();
    }

    private Actor getActor() {
        return (Actor)getModelObject();
    }

}
