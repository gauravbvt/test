// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.OrganizationCapabilityPK;
import javax.persistence.Column;
import javax.persistence.Embeddable;

privileged aspect OrganizationCapabilityPK_Roo_Identifier {
    
    declare @type: OrganizationCapabilityPK: @Embeddable;
    
    @Column(name = "respondent", columnDefinition = "INT", nullable = false)
    private Integer OrganizationCapabilityPK.respondent;
    
    @Column(name = "organization", columnDefinition = "INT", nullable = false)
    private Integer OrganizationCapabilityPK.organization;
    
    @Column(name = "core_capability", columnDefinition = "INT", nullable = false)
    private Integer OrganizationCapabilityPK.coreCapability;
    
    public OrganizationCapabilityPK.new(Integer respondent, Integer organization, Integer coreCapability) {
        super();
        this.respondent = respondent;
        this.organization = organization;
        this.coreCapability = coreCapability;
    }

    private OrganizationCapabilityPK.new() {
        super();
    }

    public Integer OrganizationCapabilityPK.getRespondent() {
        return respondent;
    }
    
    public Integer OrganizationCapabilityPK.getOrganization() {
        return organization;
    }
    
    public Integer OrganizationCapabilityPK.getCoreCapability() {
        return coreCapability;
    }
    
}
