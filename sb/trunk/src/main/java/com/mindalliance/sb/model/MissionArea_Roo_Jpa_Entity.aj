// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.MissionArea;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

privileged aspect MissionArea_Roo_Jpa_Entity {
    
    declare @type: MissionArea: @Entity;
    
    declare @type: MissionArea: @Table(name = "mission_area");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "INT")
    private Integer MissionArea.id;
    
    public Integer MissionArea.getId() {
        return this.id;
    }
    
    public void MissionArea.setId(Integer id) {
        this.id = id;
    }
    
}
