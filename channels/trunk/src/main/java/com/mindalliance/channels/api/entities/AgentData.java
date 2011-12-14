package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for an actor.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 10:02 AM
 */
@XmlType( propOrder = {"id", "name", "categories", "kind", "availability", "languages", "documentation"} )
public class AgentData extends ModelEntityData {

    public AgentData() {
    }

    public AgentData( ModelObject modelObject, Plan plan ) {
        super( modelObject, plan );
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
    @XmlElement( name = "categoryId" )
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @Override
    @XmlElement
    public String getKind() {
        return super.getKind();
    }

    @XmlElement( name = "language" )
    public List<String> getLanguages() {
        List<String> languages = new ArrayList<String>(  );
        for ( String language : getActor().getEffectiveLanguages( getPlan() ) ) {
            languages.add( language );
        }
        return languages;
    }

    @XmlElement
    public AvailabilityData getAvailability() {
        return new AvailabilityData( getActor().getAvailability() );
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private Actor getActor() {
        return (Actor) getModelObject();
    }

}
