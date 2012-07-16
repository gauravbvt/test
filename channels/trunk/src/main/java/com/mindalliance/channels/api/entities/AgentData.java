package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.SecurityClassificationData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Plan;
import org.apache.commons.lang.StringUtils;

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
@XmlType( propOrder = {"id", "name", "hasUniqueIdentity", "isAnonymous", "categories", "kind",
        "availability", "languages", "classifications", "documentation", "openParticipationToAll", "openParticipationToEmployed"} )
public class AgentData extends ModelEntityData {

    public AgentData() {
    }

    public AgentData( String serverUrl, ModelObject modelObject, Plan plan ) {
        super( serverUrl, modelObject, plan );
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

    @XmlElement
    public boolean getHasUniqueIdentity() {
        return getActor().isSingularParticipation();
    }

    @XmlElement
    public boolean getIsAnonymous() {
        return getActor().isAnonymousParticipation();
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
        List<String> languages = new ArrayList<String>();
        for ( String language : getActor().getEffectiveLanguages( getPlan() ) ) {
            languages.add( StringUtils.capitalize( language.toLowerCase() ) );
        }
        return languages;
    }

    @XmlElement
    public AvailabilityData getAvailability() {
        return new AvailabilityData( getActor().getAvailability() );
    }

    @XmlElement( name = "clearance" )
    public List<SecurityClassificationData> getClassifications() {
        List<SecurityClassificationData> classifications = new ArrayList<SecurityClassificationData>();
        for ( Classification classification : getActor().getClearances() ) {
            classifications.add( new SecurityClassificationData( classification ) );
        }
        return classifications;
    }

    @XmlElement
    public boolean getOpenParticipationToAll() {
        return getActor().isOpenParticipation();
    }

    @XmlElement
    public boolean getOpenParticipationToEmployed() {
        return getActor().isParticipationRestrictedToEmployed();
    }


    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private Actor getActor() {
        return (Actor) getModelObject();
    }

    public String getLanguagesLabel() {
        return StringUtils.join( getLanguages(), ", ");
    }

    public Actor actor() {
        return getActor();
    }
}
