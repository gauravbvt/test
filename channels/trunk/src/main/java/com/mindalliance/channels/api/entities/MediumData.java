package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.SecurityClassificationData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.TransmissionMedium;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for a transmission medium.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/8/11
 * Time: 2:29 PM
 */
@XmlType( propOrder = {"name", "id", "categories", "mode", "synchronous", "security", "reach", "qualification", "delegatesTo", "documentation"} )
public class MediumData extends ModelEntityData {

    public MediumData() {
        // required
    }

    public MediumData( String serverUrl, TransmissionMedium medium, Plan plan ) {
        super( serverUrl, medium, plan );
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

    @XmlElement
    public String getMode() {
        return getMedium().getCast() == null
                ? null
                : getMedium().getCast().name();
    }

    @XmlElement
    public boolean getSynchronous() {
        return getMedium().isSynchronous();
    }

    @XmlElement( name = "security" )
    public List<SecurityClassificationData> getSecurity() {
        List<SecurityClassificationData> security = new ArrayList<SecurityClassificationData>(  );
        for ( Classification classification : getMedium().getSecurity() ) {
            security.add( new SecurityClassificationData( classification ) );
        }
        return security;
    }

    @XmlElement
    public PlaceData getReach() {
        return getMedium().getReach() == null
                ? null
                : new PlaceData( getServerUrl(), getMedium().getReach(), getPlan() );
    }

    @XmlElement
    public ActorData getQualification() {
        return getMedium().getQualification() == null
                ? null
                : new ActorData( getServerUrl(), getMedium().getQualification(), getPlan() );
    }

    @XmlElement
    public List<MediumData> getDelegatesTo() {
        List<MediumData> delegates = new ArrayList<MediumData>(  );
        for ( TransmissionMedium delegate : getMedium().getEffectiveDelegatedToMedia() ) {
            delegates.add(  new MediumData( getServerUrl(), delegate, getPlan() ) );
        }
        return delegates;
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private TransmissionMedium getMedium() {
        return (TransmissionMedium)getModelObject();
    }
}
