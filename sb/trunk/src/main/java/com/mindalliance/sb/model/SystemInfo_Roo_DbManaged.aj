// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentSystem;
import com.mindalliance.sb.model.SystemInfo;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

privileged aspect SystemInfo_Roo_DbManaged {
    
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "respondent", referencedColumnName = "respondent", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "organization", referencedColumnName = "organization", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "incident", referencedColumnName = "incident", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "system_name", referencedColumnName = "name", nullable = false, insertable = false, updatable = false) })
    private IncidentSystem SystemInfo.incidentSystem;
    
    @Column(name = "description", length = 127)
    private String SystemInfo.description;
    
    @Column(name = "read", columnDefinition = "BIT")
    private Boolean SystemInfo.read;
    
    @Column(name = "write", columnDefinition = "BIT")
    private Boolean SystemInfo.write;
    
    public IncidentSystem SystemInfo.getIncidentSystem() {
        return incidentSystem;
    }
    
    public void SystemInfo.setIncidentSystem(IncidentSystem incidentSystem) {
        this.incidentSystem = incidentSystem;
    }
    
    public String SystemInfo.getDescription() {
        return description;
    }
    
    public void SystemInfo.setDescription(String description) {
        this.description = description;
    }
    
    public Boolean SystemInfo.getRead() {
        return read;
    }
    
    public void SystemInfo.setRead(Boolean read) {
        this.read = read;
    }
    
    public Boolean SystemInfo.getWrite() {
        return write;
    }
    
    public void SystemInfo.setWrite(Boolean write) {
        this.write = write;
    }
    
}