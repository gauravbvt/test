package com.mindalliance.channels.api;

import com.mindalliance.channels.core.model.asset.AssetConnection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/24/14
 * Time: 7:49 PM
 */
@XmlType( propOrder = {"type", "assetId", "label", "consuming", "critical", "forwarding"} )
public class AssetConnectionData implements Serializable {

    private AssetConnection assetConnection;

    public AssetConnectionData() {
        // required
    }

    public AssetConnectionData( AssetConnection assetConnection ) {
        this.assetConnection = assetConnection;
    }

    @XmlElement
    public String getType() {
        return assetConnection.getDetailedTypeLabel();
    }

    @XmlElement
    public Long getAssetId() {
        return assetConnection.getAsset().getId();
    }

    @XmlElement
    public String getLabel() {
        return assetConnection.getLabel();
    }

    @XmlElement
    public String getConsuming() {
        return assetConnection.hasProperty( AssetConnection.CONSUMING )
                ? "true"
                : null;
    }

    @XmlElement
    public String getCritical() {
        return assetConnection.hasProperty( AssetConnection.CRITICAL)
                ? "true"
                : null;
    }

    @XmlElement
    public String getForwarding() {
        return assetConnection.hasProperty( AssetConnection.FORWARDING )
                ? "true"
                : null;
    }

}
