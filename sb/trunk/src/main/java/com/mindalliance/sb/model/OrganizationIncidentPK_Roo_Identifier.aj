// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.OrganizationIncidentPK;
import javax.persistence.Column;
import javax.persistence.Embeddable;

privileged aspect OrganizationIncidentPK_Roo_Identifier {
    
    declare @type: OrganizationIncidentPK: @Embeddable;
    
    @Column(name = "respondent", columnDefinition = "INT", nullable = false)
    private Integer OrganizationIncidentPK.respondent;
    
    @Column(name = "organization", columnDefinition = "INT", nullable = false)
    private Integer OrganizationIncidentPK.organization;
    
    @Column(name = "incident", columnDefinition = "INT", nullable = false)
    private Integer OrganizationIncidentPK.incident;
    
    public OrganizationIncidentPK.new(Integer respondent, Integer organization, Integer incident) {
        super();
        this.respondent = respondent;
        this.organization = organization;
        this.incident = incident;
    }

    private OrganizationIncidentPK.new() {
        super();
    }

    public Integer OrganizationIncidentPK.getRespondent() {
        return respondent;
    }
    
    public Integer OrganizationIncidentPK.getOrganization() {
        return organization;
    }
    
    public Integer OrganizationIncidentPK.getIncident() {
        return incident;
    }
    
}