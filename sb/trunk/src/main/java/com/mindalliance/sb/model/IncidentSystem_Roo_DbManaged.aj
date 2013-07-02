// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentSystem;
import com.mindalliance.sb.model.Organization;
import com.mindalliance.sb.model.OrganizationIncident;
import com.mindalliance.sb.model.SystemInfo;
import com.mindalliance.sb.model.SystemIssue;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

privileged aspect IncidentSystem_Roo_DbManaged {
    
    @OneToMany(mappedBy = "incidentSystem", cascade = CascadeType.REMOVE)
    private Set<SystemInfo> IncidentSystem.systemInfoes;
    
    @OneToMany(mappedBy = "incidentSystem", cascade = CascadeType.REMOVE)
    private Set<SystemIssue> IncidentSystem.systemIssues;
    
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "respondent", referencedColumnName = "respondent", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "organization", referencedColumnName = "organization", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "incident", referencedColumnName = "incident", nullable = false, insertable = false, updatable = false) })
    private OrganizationIncident IncidentSystem.organizationIncident;
    
    @ManyToOne
    @JoinColumn(name = "maintainer", referencedColumnName = "id", nullable = false)
    private Organization IncidentSystem.maintainer;
    
    @Column(name = "system", columnDefinition = "INT")
    private Integer IncidentSystem.system;
    
    @Column(name = "acronym", length = 127)
    private String IncidentSystem.acronym;
    
    @Column(name = "actual", columnDefinition = "BIT")
    private Boolean IncidentSystem.actual;
    
    @Column(name = "send", columnDefinition = "BIT")
    private Boolean IncidentSystem.send;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String IncidentSystem.comment;
    
    @Column(name = "logo", columnDefinition = "BLOB")
    private byte[] IncidentSystem.logo;
    
    public Set<SystemInfo> IncidentSystem.getSystemInfoes() {
        return systemInfoes;
    }
    
    public void IncidentSystem.setSystemInfoes(Set<SystemInfo> systemInfoes) {
        this.systemInfoes = systemInfoes;
    }
    
    public Set<SystemIssue> IncidentSystem.getSystemIssues() {
        return systemIssues;
    }
    
    public void IncidentSystem.setSystemIssues(Set<SystemIssue> systemIssues) {
        this.systemIssues = systemIssues;
    }
    
    public OrganizationIncident IncidentSystem.getOrganizationIncident() {
        return organizationIncident;
    }
    
    public void IncidentSystem.setOrganizationIncident(OrganizationIncident organizationIncident) {
        this.organizationIncident = organizationIncident;
    }
    
    public Organization IncidentSystem.getMaintainer() {
        return maintainer;
    }
    
    public void IncidentSystem.setMaintainer(Organization maintainer) {
        this.maintainer = maintainer;
    }
    
    public Integer IncidentSystem.getSystem() {
        return system;
    }
    
    public void IncidentSystem.setSystem(Integer system) {
        this.system = system;
    }
    
    public String IncidentSystem.getAcronym() {
        return acronym;
    }
    
    public void IncidentSystem.setAcronym(String acronym) {
        this.acronym = acronym;
    }
    
    public Boolean IncidentSystem.getActual() {
        return actual;
    }
    
    public void IncidentSystem.setActual(Boolean actual) {
        this.actual = actual;
    }
    
    public Boolean IncidentSystem.getSend() {
        return send;
    }
    
    public void IncidentSystem.setSend(Boolean send) {
        this.send = send;
    }
    
    public String IncidentSystem.getComment() {
        return comment;
    }
    
    public void IncidentSystem.setComment(String comment) {
        this.comment = comment;
    }
    
    public byte[] IncidentSystem.getLogo() {
        return logo;
    }
    
    public void IncidentSystem.setLogo(byte[] logo) {
        this.logo = logo;
    }
    
}