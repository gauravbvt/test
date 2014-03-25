package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.AssetConnectionData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Function;
import com.mindalliance.channels.core.model.Information;
import com.mindalliance.channels.core.model.Objective;
import com.mindalliance.channels.core.model.asset.AssetConnection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Web Service data element for a function.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/26/13
 * Time: 2:21 PM
 */
@XmlType( propOrder = {"name", "id", "description", "categories", "assetConnections", "documentation", "objectives", "infoNeeds", "infoAcquired"} )
public class FunctionData extends ModelEntityData {

    private Function function;
    private List<ObjectiveData> objectiveDataList;
    private List<InformationData> infoAcquiredDataList;
    private List<InformationData> infoNeedDataList;
    private List<AssetConnectionData> assetConnectionDataList;


    public FunctionData() {
        // required
    }

    public FunctionData( String serverUrl, Function function, CommunityService communityService ) {
        super( serverUrl, function, communityService );
        this.function = function;
        init( serverUrl, function, communityService );
    }

    private void init( String serverUrl, Function function, CommunityService communityService ) {
        objectiveDataList = new ArrayList<ObjectiveData>();
        for ( Objective objective : function.getObjectives() ) {
            objectiveDataList.add( new ObjectiveData( serverUrl, objective, communityService ) );
        }
        infoNeedDataList = new ArrayList<InformationData>();
        for ( Information info : function.getInfoNeeded() ) {
            infoNeedDataList.add( new InformationData( serverUrl, info, communityService ) );
        }
        infoAcquiredDataList = new ArrayList<InformationData>();
        for ( Information info : function.getInfoAcquired() ) {
            infoAcquiredDataList.add( new InformationData( serverUrl, info, communityService ) );
        }
        initAssetConnections( );
    }

    private void initAssetConnections() {
        assetConnectionDataList = new ArrayList<AssetConnectionData>(  );
        for ( AssetConnection assetConnection : function.getAssetConnections().getAll() ) {
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
    @XmlElement( name = "categoryId" )
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    @XmlElement( name = "infoToShare" )
    public List<InformationData> getInfoAcquired() {
        return infoAcquiredDataList;
    }

    @XmlElement( name = "infoNeeded" )
    public List<InformationData> getInfoNeeds() {
        return infoNeedDataList;
    }

    @XmlElement( name = "objective" )
    public List<ObjectiveData> getObjectives() {
        return objectiveDataList;
    }

    @XmlElement( name = "assetConnection" )
    public List<AssetConnectionData> getAssetConnections() {
        return assetConnectionDataList;
    }

    public Set<Long> allInfoProductIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( Information info : function.getInfoNeeded() ) {
            if ( info.getInfoProduct() != null )
                ids.add( info.getInfoProduct().getId() );
        }
        for ( Information info : function.getInfoAcquired() ) {
            if ( info.getInfoProduct() != null )
                ids.add( info.getInfoProduct().getId() );
        }
        return ids;
    }

    public Set<Long> allAssetIds() {
        Set<Long> ids = new HashSet<Long>();
        for ( AssetConnectionData assetConnectionData : assetConnectionDataList ) {
            ids.add( assetConnectionData.getAssetId() );
        }
        return ids;
    }
}
