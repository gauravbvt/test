// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.IncidentTask;
import com.mindalliance.sb.model.IncidentTaskPK;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

privileged aspect IncidentTask_Roo_Jpa_Entity {
    
    declare @type: IncidentTask: @Entity;
    
    declare @type: IncidentTask: @Table(name = "incident_task");
    
    @EmbeddedId
    private IncidentTaskPK IncidentTask.id;
    
    public IncidentTaskPK IncidentTask.getId() {
        return this.id;
    }
    
    public void IncidentTask.setId(IncidentTaskPK id) {
        this.id = id;
    }
    
}