// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.SystemInfo;
import com.mindalliance.sb.model.SystemInfoPK;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

privileged aspect SystemInfo_Roo_Jpa_Entity {
    
    declare @type: SystemInfo: @Entity;
    
    declare @type: SystemInfo: @Table(name = "system_info");
    
    @EmbeddedId
    private SystemInfoPK SystemInfo.id;
    
    public SystemInfoPK SystemInfo.getId() {
        return this.id;
    }
    
    public void SystemInfo.setId(SystemInfoPK id) {
        this.id = id;
    }
    
}