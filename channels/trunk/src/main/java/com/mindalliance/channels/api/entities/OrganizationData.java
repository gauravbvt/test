package com.mindalliance.channels.api.entities;

import com.mindalliance.channels.api.procedures.ChannelData;
import com.mindalliance.channels.api.procedures.DocumentationData;
import com.mindalliance.channels.core.model.Channel;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.query.PlanService;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Web Service data element for an organization.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/1/11
 * Time: 9:15 AM
 */
@XmlType( propOrder = {"id", "name", "categories", "kind", "parentId", "fullAddress", "mission", "participating", "documentation"} )
public class OrganizationData extends ModelEntityData {

    private boolean participating;
    private List<ChannelData> channelsDataList;

    public OrganizationData() {
    }

    public OrganizationData( ModelObject modelObject, PlanService planService ) {
        super( modelObject, planService.getPlan() );
        init( planService );
    }

    private void init( PlanService planService ) {
        participating = planService.getPlan().isInScope( getOrganization() );
        channelsDataList = new ArrayList<ChannelData>(  );
        for ( Channel channel : getOrganization().getEffectiveChannels() ) {
            channelsDataList.add( new ChannelData( channel, planService ) );
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
    @XmlElement( name = "categoryId" )
    public List<Long> getCategories() {
        return super.getCategories();
    }

    @Override
    @XmlElement
    public String getKind() {
        return super.getKind();
    }

    @XmlElement
    public Boolean getParticipating() {
        return participating;
    }

    @XmlElement
    public Long getParentId() {
        return getOrganization().getParent() != null
                ? getOrganization().getParent().getId()
                : null;
    }

    @XmlElement
    public String getFullAddress() {
        Place location = getOrganization().getLocation();
        return location == null ? null : location.getFullAddress();
    }

    @XmlElement
    public String getMission() {
        return getOrganization().getMission().isEmpty() ? null : getOrganization().getMission();
    }

    @XmlElement
    @Override
    public DocumentationData getDocumentation() {
        return super.getDocumentation();
    }

    private Organization getOrganization() {
        return (Organization)getModelObject();
    }

    public List<ChannelData> getChannels() {
        return channelsDataList;
    }

    public Organization organization() {
        return getOrganization();
    }
}
