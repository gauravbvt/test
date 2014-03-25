package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.checklist.AssetProvisioning;
import com.mindalliance.channels.core.model.checklist.Checklist;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 2:41 PM
 */
@XmlType( name="assetProvisioned", propOrder = {"assetId", "taskId", "segmentId", "label"} )
public class AssetProvisioningData implements Serializable {

    private AssetProvisioning assetProvisioning;
    private String label;
    private Part part;


    public AssetProvisioningData() {
        //required
    }

    public AssetProvisioningData( ChecklistData checklistData, AssetProvisioning assetProvisioning, CommunityService communityService ) {
        this.assetProvisioning = assetProvisioning;
        Checklist checklist = checklistData.checklist();
        part = assetProvisioning.getPart( checklist );
        label = assetProvisioning.getLabel( checklist, communityService );
    }

    @XmlElement
    public Long getAssetId() {
        return assetProvisioning.getAssetId();
    }

    @XmlElement
    public Long getTaskId() {
        return part.getId();
    }

    @XmlElement
    public Long getSegmentId() {
        return part.getSegment().getId();
    }

    @XmlElement
    public String getLabel() {
        return label;
    }
}
