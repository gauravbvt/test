// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SharingPK;
import javax.persistence.Column;
import javax.persistence.Embeddable;

privileged aspect SharingPK_Roo_Identifier {
    
    declare @type: SharingPK: @Embeddable;
    
    @Column(name = "respondent", columnDefinition = "INT", nullable = false)
    private Integer SharingPK.respondent;
    
    @Column(name = "respondent_organization", columnDefinition = "INT", nullable = false)
    private Integer SharingPK.respondentOrganization;
    
    @Column(name = "incident", columnDefinition = "INT", nullable = false)
    private Integer SharingPK.incident;
    
    @Column(name = "organization", columnDefinition = "INT", nullable = false)
    private Integer SharingPK.organization;
    
    public SharingPK.new(Integer respondent, Integer respondentOrganization, Integer incident, Integer organization) {
        super();
        this.respondent = respondent;
        this.respondentOrganization = respondentOrganization;
        this.incident = incident;
        this.organization = organization;
    }

    private SharingPK.new() {
        super();
    }

    public Integer SharingPK.getRespondent() {
        return respondent;
    }
    
    public Integer SharingPK.getRespondentOrganization() {
        return respondentOrganization;
    }
    
    public Integer SharingPK.getIncident() {
        return incident;
    }
    
    public Integer SharingPK.getOrganization() {
        return organization;
    }
    
}
