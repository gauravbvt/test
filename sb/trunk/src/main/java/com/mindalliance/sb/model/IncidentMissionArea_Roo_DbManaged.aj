// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentMissionArea;
import com.mindalliance.sb.model.MissionArea;
import com.mindalliance.sb.model.OrganizationIncident;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

privileged aspect IncidentMissionArea_Roo_DbManaged {
    
    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "respondent", referencedColumnName = "respondent", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "organization", referencedColumnName = "organization", nullable = false, insertable = false, updatable = false), @JoinColumn(name = "incident", referencedColumnName = "incident", nullable = false, insertable = false, updatable = false) })
    private OrganizationIncident IncidentMissionArea.organizationIncident;
    
    @ManyToOne
    @JoinColumn(name = "mission_area", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private MissionArea IncidentMissionArea.missionArea;
    
    public OrganizationIncident IncidentMissionArea.getOrganizationIncident() {
        return organizationIncident;
    }
    
    public void IncidentMissionArea.setOrganizationIncident(OrganizationIncident organizationIncident) {
        this.organizationIncident = organizationIncident;
    }
    
    public MissionArea IncidentMissionArea.getMissionArea() {
        return missionArea;
    }
    
    public void IncidentMissionArea.setMissionArea(MissionArea missionArea) {
        this.missionArea = missionArea;
    }
    
}