// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.mindalliance.sb.model;

import com.mindalliance.sb.model.OrgType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

privileged aspect OrgType_Roo_Jpa_Entity {
    
    declare @type: OrgType: @Entity;
    
    declare @type: OrgType: @Table(name = "org_type");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "INT")
    private Integer OrgType.id;
    
    public Integer OrgType.getId() {
        return this.id;
    }
    
    public void OrgType.setId(Integer id) {
        this.id = id;
    }
    
}
