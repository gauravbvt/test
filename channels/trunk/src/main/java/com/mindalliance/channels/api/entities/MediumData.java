package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.AssetConnectionData;
import com.mindalliance.channels.api.SecurityClassificationData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Classification;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.core.model.asset.AssetConnection;

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
@XmlType(propOrder = {"name", "id", "description", "categories", "mode", "synchronous", "security", "reach", "qualification", "delegatesTo", "assetConnections", "documentation"})
public class MediumData extends ModelEntityData {

    private ActorData actorData;
    private List<MediumData> delegates;
    private PlaceData reach;
    private List<AssetConnectionData> assetConnectionDataList;


    public MediumData() {
        // required
    }

    public MediumData( String serverUrl, TransmissionMedium medium, CommunityService communityService ) {
        super( serverUrl, medium, communityService );
        init( communityService );
    }

    private void init( CommunityService communityService ) {
        actorData = getMedium().getQualification() == null
                ? null
                : new ActorData( getServerUrl(), getMedium().getQualification(), communityService );
        delegates = new ArrayList<MediumData>();
        for ( TransmissionMedium delegate : getMedium().getEffectiveDelegatedToMedia() ) {
            delegates.add( new MediumData( getServerUrl(), delegate, communityService ) );
        }
        reach = getMedium().getReach() == null
                ? null
                : new PlaceData( getServerUrl(), communityService.resolveLocation( getMedium().getReach() ), communityService );
        initAssetConnections( );
    }

    private void initAssetConnections() {
        assetConnectionDataList = new ArrayList<AssetConnectionData>(  );
        for ( AssetConnection assetConnection : getMedium().getAssetConnections() ) {
            assetConnectionDataList.add( new AssetConnectionData( assetConnection ));
        }
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
    @XmlElement(name = "categoryId")
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

    @XmlElement(name = "security")
    public List<SecurityClassificationData> getSecurity() {
        List<SecurityClassificationData> security = new ArrayList<SecurityClassificationData>();
        for ( Classification classification : getMedium().getSecurity() ) {
            security.add( new SecurityClassificationData( classification ) );
        }
        return security;
    }

    @XmlElement
    public PlaceData getReach() {
        return reach;
    }

    @XmlElement
    public ActorData getQualification() {
        return actorData;
    }

    @XmlElement
    public List<MediumData> getDelegatesTo() {
        return delegates;
    }

    @XmlElement( name = "assetConnection" )
    public List<AssetConnectionData> getAssetConnections() {
        return assetConnectionDataList;
    }


    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private TransmissionMedium getMedium() {
        return (TransmissionMedium) getModelObject();
    }
}
