// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentMissionArea;
import com.mindalliance.sb.model.IncidentMissionAreaPK;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

privileged aspect IncidentMissionArea_Roo_Jpa_Entity {
    
    declare @type: IncidentMissionArea: @Entity;
    
    declare @type: IncidentMissionArea: @Table(name = "incident_mission_area");
    
    @EmbeddedId
    private IncidentMissionAreaPK IncidentMissionArea.id;
    
    public IncidentMissionAreaPK IncidentMissionArea.getId() {
        return this.id;
    }
    
    public void IncidentMissionArea.setId(IncidentMissionAreaPK id) {
        this.id = id;
    }
    
}
