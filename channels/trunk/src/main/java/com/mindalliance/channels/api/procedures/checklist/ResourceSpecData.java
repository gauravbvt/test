package com.mindalliance.channels.api.procedures.checklist;

import com.mindalliance.channels.api.entities.ActorData;
import com.mindalliance.channels.api.entities.OrganizationData;
import com.mindalliance.channels.api.entities.PlaceData;
import com.mindalliance.channels.api.entities.RoleData;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.ResourceSpec;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/25/13
 * Time: 11:24 AM
 */
@XmlType( name = "resourceSpec" )
public class ResourceSpecData implements Serializable {

    private ActorData actor;
    private RoleData role;
    private PlaceData jurisdiction;
    private OrganizationData organization;

    public ResourceSpecData() {
        // required
    }

     public ResourceSpecData( String serverUrl,
                             ResourceSpec resourceSpec,
                             CommunityService communityService ) {
        if ( resourceSpec.getActor() != null )
            actor = new ActorData( serverUrl, resourceSpec.getActor(), communityService );
        if ( resourceSpec.getRole() != null )
            role = new RoleData( serverUrl, resourceSpec.getRole(), communityService );
        if ( resourceSpec.getJurisdiction() != null )
            jurisdiction = new PlaceData(
                    serverUrl,
                    communityService.resolveLocation( resourceSpec.getJurisdiction() ),
                    communityService );
        if ( resourceSpec.getOrganization() != null )
            organization = new OrganizationData( serverUrl, resourceSpec.getOrganization(), communityService );
    }

    @XmlElement
    public ActorData getActor() {
        return actor;
    }

    @XmlElement
    public PlaceData getJurisdiction() {
        return jurisdiction;
    }

    @XmlElement
    public OrganizationData getOrganization() {
        return organization;
    }

    @XmlElement
    public RoleData getRole() {
        return role;
    }


    public Set<Long> allActorIds() {
        Set<Long> ids = new HashSet<Long>(  );
        if ( actor != null )
            ids.add( actor.getId() );
        return ids;
    }

    public Set<Long> allOrganizationIds() {
        Set<Long> ids = new HashSet<Long>(  );
        if ( organization != null )
            ids.add( organization.getId() );
        return ids;
    }

    public Set<Long> allPlaceIds() {
        Set<Long> ids = new HashSet<Long>(  );
        if ( jurisdiction != null )
            ids.add( jurisdiction.getId() );
        return ids;
    }

    public Set<Long> allRoleIds() {
        Set<Long> ids = new HashSet<Long>(  );
        if ( role != null )
            ids.add( role.getId() );
        return ids;
    }
}
