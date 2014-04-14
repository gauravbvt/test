package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.asset.AssetField;
import com.mindalliance.channels.core.model.asset.MaterialAsset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for a material asset.
 * Copyright (C) 2008-2014 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 1:17 PM
 */
@XmlType( propOrder = {"id", "name", "description", "categories", "kind", "dependencies", "fields", "placeholder", "documentation"} )
public class MaterialAssetData extends ModelEntityData {

    private List<AssetFieldData> assetFieldDataList;
    private List<MaterialAsset> dependencies = new ArrayList<MaterialAsset>(  );

    public MaterialAssetData() {
    }

    public MaterialAssetData( String serverUrl, ModelObject modelObject, CommunityService communityService ) {
        super( serverUrl, modelObject, communityService );
        init( communityService );
    }

    private void init( CommunityService communityService ) {
        assetFieldDataList = new ArrayList<AssetFieldData>(  );
        for ( AssetField assetField : getMaterialAsset().getFields() ) {
            assetFieldDataList.add( new AssetFieldData( assetField ));
        }
        for ( MaterialAsset dependency : getMaterialAsset().getDependencies() ) {
            dependencies.add( communityService.resolveAsset( dependency ) ); // todo - don't assume resolved asset has same dependencies
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

    @Override
    @XmlElement
    public String getKind() {
        return super.getKind();
    }

    @Override
    @XmlElement
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    @XmlElement( name="dependencyId" )
    public List<Long> getDependencies() {
        List<Long> ids = new ArrayList<Long>(  );
        for ( MaterialAsset dependency : dependencies ) {
            ids.add( dependency.getId() );
        }
        return ids;
    }

    @XmlElement( name="field")
    public List<AssetFieldData>getFields() {
        return assetFieldDataList;
    }

    @XmlElement
    public boolean getPlaceholder() {
        return getMaterialAsset().isPlaceholder();
    }

    private MaterialAsset getMaterialAsset() {
        return (MaterialAsset)getModelObject();
    }

    public MaterialAsset materialAsset() {
        return getMaterialAsset();
    }
}
